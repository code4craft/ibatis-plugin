package org.intellij.ibatis.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

/**
 * SQL code preview action
 *
 * @author jacky
 */
public class SQLPreviewAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        //todo
        showPopup(e.getData(DataKeys.PROJECT), e.getData(DataKeys.EDITOR), "search");
    }

    private void showPopup(Project project, Editor editor, String searchText) {
        SQLPopupView popupView = new SQLPopupView(searchText);
        JBPopup jbPopup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(popupView.mainPanel, popupView.mainPanel)
                .setDimensionServiceKey(project, "ibatis-sql-preview", false)
                .setRequestFocus(true)
                .setResizable(true)
                .setMovable(true)
                .setTitle("SQL code preview")
                .createPopup();
        jbPopup.showInBestPositionFor(editor);
    }


}
