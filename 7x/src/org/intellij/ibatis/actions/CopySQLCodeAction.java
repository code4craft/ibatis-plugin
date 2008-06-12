package org.intellij.ibatis.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.BaseStatement;
import org.intellij.ibatis.provider.*;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * action to copy SQL Code in statement
 *
 * @author jacky
 */
public class CopySQLCodeAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
        Editor editor = e.getData(DataKeys.EDITOR);
        if ((psiFile != null && psiFile instanceof XmlFile) && editor != null) {
            PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
            if (psiElement != null) {
                XmlTag xmlTag = SqlMapSymbolCompletionData.getXmlTagForSQLCompletion(psiElement, psiFile);
                if (xmlTag != null) {
                    DomElement domElement = DomManager.getDomManager(e.getData(DataKeys.PROJECT)).getDomElement(xmlTag);
                    if (domElement != null && domElement instanceof BaseStatement) {
                        BaseStatement baseStatement = (BaseStatement) domElement;
                        PsiClass parameterClass = baseStatement.getParameterClass().getValue();
                        String SQLCode = IbatisUtil.getSQLForXmlTag(xmlTag);
                        String pattern = "#[\\w:\\.]*#";
                        if (parameterClass == null) {     //type is unknown
                            SQLCode = SQLCode.replaceAll(pattern, "''");
                        } else
                        if (IbatisClassShortcutsReferenceProvider.isDomain(parameterClass.getName())) // domain class
                        {
                            List<String> inlineParameters = IbatisUtil.grep(SQLCode, pattern);
                            for (String parameter : inlineParameters) {
                                PsiClass referencedClass = parameterClass;
                                String fieldName = parameter.replaceAll("#", "").replaceAll(":\\w*", "");
                                String[] referencePath = fieldName.split("\\.");
                                for (String part : referencePath) {
                                    referencedClass = FieldAccessMethodReferenceProvider.findGetterMethodReturnType(referencedClass, "get" + StringUtil.capitalize(part));
                                    if (referencedClass == null) break;
                                }
                                SQLCode = SQLCode.replaceAll(parameter, getDefaultValueForType(referencedClass == null ? "" : referencedClass.getQualifiedName()));
                            }
                        } else   //domain class
                        {
                            SQLCode = SQLCode.replaceAll(pattern, getDefaultValueForType(parameterClass.getQualifiedName()));
                        }
                        CopyPasteManager copyPasteManager = CopyPasteManager.getInstance();
                        copyPasteManager.setContents(new StringSelection(SQLCode.trim()));
                    }
                }
            }
        }
    }

    /**
     * get default value for type
     *
     * @param className class names
     * @return default value
     */
    @NotNull
    public String getDefaultValueForType(String className) {
        if (className == null) return "''";
        className = className.replace("java.lang.", "");
        className = className.replace("java.util.", "");
        if (className.equalsIgnoreCase("date")) //date
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return "'" + format.format(new Date()) + "'";
        } else if (className.equalsIgnoreCase("string"))  //varchar
        {
            return "''";
        } else if (className.equalsIgnoreCase("map") || className.equalsIgnoreCase("hashmap")) //map
        {
            return "''";
        } else //as number
        {
            return "1";
        }
    }

    @Override public void update(AnActionEvent e) {
        super.update(e);
        PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
        Editor editor = e.getData(DataKeys.EDITOR);
        if ((psiFile != null && psiFile instanceof XmlFile) && editor != null) {
            PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
            if (psiElement != null) {
                XmlTag tag = SqlMapSymbolCompletionData.getXmlTagForSQLCompletion(psiElement, psiFile);
                if (tag != null) {
                    e.getPresentation().setEnabled(true);
                    e.getPresentation().setVisible(true);
                    return;
                }
            }
        }
        e.getPresentation().setEnabled(false);
        e.getPresentation().setVisible(false);
    }
}
