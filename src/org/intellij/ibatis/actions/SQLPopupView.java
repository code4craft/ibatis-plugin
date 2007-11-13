package org.intellij.ibatis.actions;

import com.intellij.codeInsight.hint.HintUtil;

import javax.swing.*;

/**
 * SQL code preview form
 *
 * @author jacky
 */
public class SQLPopupView {
    private JTextPane textPane;
    public JPanel mainPanel;
    private JTable paramsTable;

    public SQLPopupView(String searchText) {
        try {
            textPane.setText(searchText);
            textPane.setBackground(HintUtil.INFORMATION_COLOR);
        }
        catch (Exception e) {
            textPane.setText(e.getMessage());
        }
    }

}
