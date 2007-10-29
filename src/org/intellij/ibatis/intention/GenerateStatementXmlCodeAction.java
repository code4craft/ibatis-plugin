package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.IbatisConfigurationModel;
import org.intellij.ibatis.facet.IbatisFacetConfiguration;
import org.intellij.ibatis.provider.SqlClientElementFilter;
import org.intellij.ibatis.provider.StatementIdReferenceProvider;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
            PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
            String[] path = methodExpression.getText().split("\\.");
            operationType = path[path.length - 1].trim();
            statementId = expression.getText().replaceAll("\"", "");
            PsiReturnStatement returnStatement = PsiTreeUtil.getParentOfType(methodExpression, PsiReturnStatement.class);
            PsiType returnType = returnStatement.getReturnValue().getType();
            if (returnType != null) {
                resultClass = returnType.getCanonicalText();
                if (resultClass.contains("<")) {
                    resultClass = resultClass.substring(resultClass.indexOf("<") + 1, resultClass.indexOf(">"));
                }
            }
            PsiExpression[] argumentExpressionList = methodCallExpression.getArgumentList().getExpressions();
            if (argumentExpressionList.length > 1) {
                parameterClass = argumentExpressionList[1].getType().getCanonicalText();
            }
            IbatisConfigurationModel model = IbatisManager.getInstance().getConfigurationModel(ModuleUtil.findModuleForPsiElement(element));
            Set<XmlFile> sqlMapFiles = model.getSqlMapFiles();
            for (XmlFile sqlMapFile : sqlMapFiles) {
                String fileName = sqlMapFile.getVirtualFile().getName();
            }
            boolean isSpaceUsed = model.isUseStatementNamespaces();
            getGenerateCode(null,operationType, statementId, isSpaceUsed, resultClass, parameterClass);
        }
    }

    private void getGenerateCode(PsiFile sqlMapFile,String operationType, String statementId, boolean isSpaceUsed, String resultClass, String parameterClass)
    {
     
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
