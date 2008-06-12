package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.IbatisConfigurationModel;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.facet.IbatisFacetConfiguration;
import org.intellij.ibatis.provider.*;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * generate statement xml code according to sql map client invocation when statement id absent
 *
 * @author Jacky
 */
public class GenerateStatementXmlCodeAction extends PsiIntentionBase {
    private static final String NAME = "GenerateStatementXmlCode";
    private static final String TEXT = "Generate statement xml code when statement id absent";

    protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
        IbatisFacetConfiguration config = IbatisUtil.getConfig(element);
        if (config != null)  //iBATIS enabled
        {
            String resultClass = null;
            String parameterClass = null;
            String statementId = null;
            String operationType = null;
            PsiLiteralExpression expression = (PsiLiteralExpression) element;
            PsiMethodCallExpression methodCallExpression = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
            if (methodCallExpression == null) return;
            PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
            String[] path = methodExpression.getText().split("\\.");
            operationType = path[path.length - 1].trim();
            statementId = expression.getText().replaceAll("\"", "");
            PsiReturnStatement returnStatement = PsiTreeUtil.getParentOfType(methodExpression, PsiReturnStatement.class);
            if (returnStatement != null) {
                PsiExpression returnValue = returnStatement.getReturnValue();
                if (returnValue != null) {
                    PsiType returnType = returnValue.getType();
                    if (returnType != null) {
                        resultClass = returnType.getCanonicalText();
                        if (resultClass.contains("<")) {  //generic in java 5
                            resultClass = resultClass.substring(resultClass.indexOf("<") + 1, resultClass.indexOf(">"));
                        } else  //common class name
                        {
                            //ignore collection class
                            if (resultClass.equals("java.util.List") || resultClass.equals("java.util.Map")) {
                                resultClass = null;
                            }
                        }
                    }
                }
            }
            PsiExpression[] argumentExpressionList = methodCallExpression.getArgumentList().getExpressions();
            if (argumentExpressionList.length > 1) {
                parameterClass = argumentExpressionList[1].getType().getCanonicalText();
                if(parameterClass.equals("null")) parameterClass=null;
            }
            IbatisConfigurationModel model = IbatisManager.getInstance().getConfigurationModel(ModuleUtil.findModuleForPsiElement(element));
            if (model != null) {
                boolean isSpaceUsed = model.isUseStatementNamespaces();
                XmlFile destinationSQLMapFile = null;  //if destination file not found, the last one will be used
                Set<XmlFile> sqlMapFiles = model.getSqlMapFiles();
                for (XmlFile sqlMapFile : sqlMapFiles) {
                    String fileName = sqlMapFile.getName().trim();
                    XmlDocument document = sqlMapFile.getDocument();
                    if (document != null) {
                        XmlTag rootTag = document.getRootTag();
                        if (rootTag != null) {
                            destinationSQLMapFile = sqlMapFile;
                            String nameSpace = rootTag.getAttributeValue("namespace");
                            if (isMatchedSQLMapFile(fileName, nameSpace, statementId)) {
                                break;
                            }
                        }
                    }
                }
                //destination found?
                if (destinationSQLMapFile != null) {
                    XmlTag xmlTag = generateStatementXMLCode(destinationSQLMapFile, operationType, statementId, isSpaceUsed, resultClass, parameterClass);
                    //open the SQL Map file and navigate to the xml tag
                    if (xmlTag != null) {
                        OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(xmlTag.getAttribute("id").getValueElement());
                        FileEditorManager.getInstance(project).openEditor(fileDescriptor, true);
                    }
                }
            }
        }
    }

    /**
     * validate file name is suitable for writing xml code
     *
     * @param fileName    file name
     * @param nameSpace   name space
     * @param statementId statement id
     * @return match mark
     */
    public boolean isMatchedSQLMapFile(@NotNull String fileName, @Nullable String nameSpace, @NotNull String statementId) {
        String[] parts = statementId.split("\\.");
        fileName = fileName.toLowerCase().replace("\\.xml", ""); // file name converter
        //name space and file name match
        boolean isMatched = fileName.contains(parts[0]) || parts[0].equals(nameSpace);
        //words match, "." not included
        if (!isMatched && !statementId.contains(".")) {
            List<String> words = IbatisUtil.grep(parts[parts.length - 1], "[A-Z]*[a-z]*");
            for (String word : words) {
                if (word.length() > 3 && fileName.contains(word.toLowerCase())) {
                    isMatched = true;
                    break;
                }
            }
        }
        return isMatched;
    }

    /**
     * generate statement xml code
     *
     * @param sqlMapFile     destination file
     * @param operationType  operation type
     * @param statementId    statement id
     * @param isSpaceUsed    name space used?
     * @param resultClass    result class
     * @param parameterClass parameter class
     * @return generated xml tag
     */
    @Nullable
    private XmlTag generateStatementXMLCode(XmlFile sqlMapFile, String operationType, String statementId, boolean isSpaceUsed, String resultClass, String parameterClass) {
        DomFileElement<SqlMap> fileElement = DomManager.getDomManager(sqlMapFile.getProject()).getFileElement(sqlMapFile, SqlMap.class);
        if (fileElement == null) return null;
        SqlMap sqlMap = fileElement.getRootElement();
        BaseStatement statement = createStatement(operationType, sqlMap);
        statement.getId().setStringValue(isSpaceUsed ? statementId.split("\\.")[0] : statementId);
        //parameterClass assign
        if (StringUtil.isNotEmpty(parameterClass)) {
            boolean isShortCut = false;  // indicate shortcut
            //internal type alias introduced
            for (Map.Entry<String, String> entry : IbatisClassShortcutsReferenceProvider.classShortcuts.entrySet()) {
                if (entry.getValue().equals(parameterClass)) {
                    parameterClass = entry.getKey();
                    isShortCut = true;
                    break;
                }
            }
            if (!isShortCut) {   // none shortcut class
                //type alias introduced
                Map<String, PsiClass> typeAliasMap = IbatisManager.getInstance().getAllTypeAlias(sqlMapFile);
                for (Map.Entry<String, PsiClass> entry : typeAliasMap.entrySet()) {
                    if (parameterClass.equals(entry.getValue().getQualifiedName())) {
                        parameterClass = entry.getKey();
                        break;
                    }
                }
            }
            statement.getParameterClass().setStringValue(parameterClass);
        }
        //resultMap or resultClass assign
        if (StringUtil.isNotEmpty(resultClass) && statement instanceof BaseResultStatement) {
            BaseResultStatement resultStatement = (BaseResultStatement) statement;
            String resultMapId = null;
            Map<String, PsiClass> resultMap = IbatisManager.getInstance().getAllResultMap(sqlMapFile);
            for (Map.Entry<String, PsiClass> entry : resultMap.entrySet()) {
                if (resultClass.equals(entry.getValue().getQualifiedName())) {
                    resultMapId = entry.getKey();
                }
            }
            if (StringUtil.isNotEmpty(resultMapId)) {
                resultStatement.getResultMap().setStringValue(resultMapId);
            } else if (StringUtil.isNotEmpty(resultClass)) {
                resultStatement.getResultClass().setStringValue(resultClass);
            }
        }
        //default content
       statement.setStringValue("\n");
        return statement.getXmlTag();
    }

    /**
     * create statement according to operation type
     *
     * @param operationType operation type, such as insert, queryForObject
     * @param sqlMap        SQLMap object
     * @return BaseStatement or sub object
     */
    private BaseStatement createStatement(String operationType, SqlMap sqlMap) {
        BaseStatement statement;
        if (operationType.contains("insert")) {
            statement = sqlMap.addInsert();
        } else if (operationType.contains("delete")) {
            statement = sqlMap.addDelete();
        } else if (operationType.contains("update")) {
            statement = sqlMap.addUpdate();
        } else if (operationType.contains("query")) {
            statement = sqlMap.addSelect();
        } else {
            statement = sqlMap.addStatement();
        }
        return statement;
    }

    /**
     * validate the element in caret is right for intention action
     *
     * @param project project
     * @param editor  editor
     * @param file    psi file,  java file need
     * @param element element in caret, PsiLiteralExpression need
     * @return available mark
     */
    protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
        if (file instanceof PsiJavaFile && element instanceof PsiLiteralExpression) {
            PsiLiteralExpression expression = (PsiLiteralExpression) element;
            PsiElement parent = element.getParent().getParent();
            if (parent instanceof PsiMethodCallExpression) {
                //method name validation simply
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) parent).getMethodExpression();
                String[] path = methodExpression.getText().split("\\.");
                String methodName = path[path.length - 1].trim().toLowerCase();
                if (methodName.matches(SqlClientElementFilter.operationPattern)) {
                    PsiReference[] references = expression.getReferences();
                    for (PsiReference reference : references) {
                        if (reference instanceof StatementIdReferenceProvider.StatementIdReference) {
                            if (reference.resolve() == null) {  //the target statement resolved failed
                                return true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    @NotNull public String getText() {
        return TEXT;
    }

    @NotNull public String getFamilyName() {
        return NAME;
    }
}
