package org.intellij.ibatis.util;

/**
 * utility class in iBATIS plug-in
 */
public class IbatisUtil {
    /**
     * get the table name with schema
     *
     * @param tableName table name
     * @return clear table name
     */
    public static String getTableNameWithoutSchema(String tableName) {
        return tableName.indexOf('.') != -1 ? tableName.substring(tableName.indexOf('.') + 1) : tableName;
    }
}
