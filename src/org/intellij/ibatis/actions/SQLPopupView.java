package org.intellij.ibatis.actions;

import com.intellij.codeInsight.hint.HintUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * SQL code preview form
 *
 * @author jacky
 */
public class SQLPopupView {
    private JTextPane textPane;
    public JPanel mainPanel;
    public JTable paramsTable;
    private String rawSqlCode;
    private Map<String, String> inlineParameters = new HashMap<String, String>();
    private PsiClass parameterClass;

    /**
     * default construction
     */
    public SQLPopupView() {
        paramsTable.setAutoscrolls(true);
        textPane.setAutoscrolls(true);
    }

    /**
     * construct parameter table and SQL panel
     *
     * @param parameterClass parameter class
     * @param parameters     parameter list
     * @param rawSqlCode     SQL code
     */
    public SQLPopupView(PsiClass parameterClass, Set<String> parameters, String rawSqlCode) {
        this.rawSqlCode = rawSqlCode;
        this.parameterClass = parameterClass;
        try {
            ParametersTableModel tableModel = new ParametersTableModel();
            for (String parameter : parameters) {
                tableModel.add(parameter, "string", "");
                updateParameterValue(parameter, "");
            }
            paramsTable.setModel(tableModel);
            textPane.setText(rawSqlCode);
            textPane.setBackground(HintUtil.INFORMATION_COLOR);
        }
        catch (Exception e) {
            textPane.setText(e.getMessage());
        }
    }

    /**
     * update parameter value
     *
     * @param name  name
     * @param value value
     */
    public void updateParameterValue(String name, String value) {
        PsiMethod[] methodNames = parameterClass.findMethodsByName("get" + StringUtil.capitalize(name), true);
        if (methodNames.length > 0l) {
            PsiType returnType = methodNames[0].getReturnType();
            if (returnType != null && "string".equalsIgnoreCase(returnType.getPresentableText())) {
                value = "\"" + value + "\"";
            }
        }
        inlineParameters.put(name, value);
        textPane.setText(getClearSQLCode());
    }

    /**
     * get clear SQL Code
     *
     * @return clear SQL Code
     */
    private String getClearSQLCode() {
        String clearSQLCode = rawSqlCode;
        for (Map.Entry<String, String> entry : inlineParameters.entrySet()) {
            clearSQLCode = clearSQLCode.replaceAll("#" + entry.getKey() + "([^#]*)#", entry.getValue());
        }
        return clearSQLCode;
    }

    /**
     * parameter table model
     */
    class ParametersTableModel extends AbstractTableModel {
        List<List<String>> rowData = new ArrayList<List<String>>();

        /**
         * 添加行数据
         *
         * @param name  parameter name
         * @param type  parameter type
         * @param value parameter value
         */
        public void add(String name, String type, String value) {
            List<String> row = new ArrayList<String>();
            row.add(name);
            row.add(type);
            row.add(value);
            rowData.add(row);
        }

        String columnNames[] = {"Parameter Name", "Type", "Value"};

        /**
         * column count
         *
         * @return column count
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * column name
         *
         * @param column column number
         * @return column name
         */
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * get row count
         *
         * @return row count
         */
        public int getRowCount() {
            return rowData.size();
        }

        /**
         * get value at  cell
         *
         * @param row    row number
         * @param column column number
         * @return value
         */
        public Object getValueAt(int row, int column) {
            return rowData.get(row).get(column);
        }

        /**
         * column class
         *
         * @param column column number
         * @return column class
         */
        public Class getColumnClass(int column) {
            return (getValueAt(0, column).getClass());
        }

        /**
         * set value at cell
         *
         * @param value  value
         * @param row    row number
         * @param column column number
         */
        public void setValueAt(Object value, int row, int column) {
            if (value != null) {
                rowData.get(row).add(column, value.toString());
                updateParameterValue(rowData.get(row).get(0), value.toString());
            }
        }

        /**
         * is cell editable
         *
         * @param row    row
         * @param column column
         * @return editable mark
         */
        public boolean isCellEditable(int row, int column) {
            return (column == 2);
        }
    }

}
