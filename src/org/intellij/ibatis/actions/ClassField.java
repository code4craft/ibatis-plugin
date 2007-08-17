package org.intellij.ibatis.actions;

import com.intellij.openapi.util.text.StringUtil;

/**
 * class field model
 */
public class ClassField {
    private String name; //field name
    private String type; //java type
    private String columnName;   //table field name

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getGetterMethodName() {
        return "get" + StringUtil.capitalize(name);
    }

    public String getSetterMethodName() {
        return "set" + StringUtil.capitalize(name);
    }
}
