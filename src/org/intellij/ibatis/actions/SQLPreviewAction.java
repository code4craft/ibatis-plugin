package org.intellij.ibatis.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.BaseStatement;
import org.intellij.ibatis.inspections.SymbolInSQLInspection;
import org.intellij.ibatis.provider.SqlMapSymbolCompletionData;
import org.intellij.ibatis.util.IbatisUtil;

import java.util.Set;

/**
 * SQL code preview action
 *
 * @author jacky
 */
public class SQLPreviewAction extends AnAction {
    /**
     * display preview popup menu
     *
     * @param e event
     */
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
                        Set<String> parameters = SymbolInSQLInspection.getAllParameterInTag(baseStatement.getXmlTag());
                        String SQLCode = IbatisUtil.getSQLForXmlTag(xmlTag);
                        showPopup(e.getData(DataKeys.PROJECT), e.getData(DataKeys.EDITOR), xmlTag.getAttributeValue("id"), SQLCode.trim(), parameterClass, parameters);
                    }
                }
            }
        }

    }

    /**
     * show popup menu
     *
     * @param project        project
     * @param editor         editor
     * @param title          title
     * @param SQLCode        search text
     * @param parameterClass parameter class
     * @param parameters     parameter names
     */
    private void showPopup(Project project, Editor editor, String title, String SQLCode, PsiClass parameterClass, Set<String> parameters) {
        SQLPopupView popupView = new SQLPopupView(parameterClass, parameters, SQLCode);
        JBPopup jbPopup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(popupView.mainPanel, popupView.mainPanel)
                .setDimensionServiceKey(project, "ibatis-sql-preview", false)
                .setRequestFocus(true)
                .setResizable(true)
                .setMovable(true)
                .setTitle(title)
                .createPopup();
        jbPopup.showInBestPositionFor(editor);
    }

    /**
     * validate action available
     *
     * @param e event
     */
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
