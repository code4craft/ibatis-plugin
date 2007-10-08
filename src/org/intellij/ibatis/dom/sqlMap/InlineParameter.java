package org.intellij.ibatis.dom.sqlMap;

/**
 * in line parameter in SQL
 */
public class InlineParameter {
    private String name;
    private String jdbcType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
}
