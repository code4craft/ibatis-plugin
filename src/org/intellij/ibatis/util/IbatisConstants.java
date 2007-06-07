package org.intellij.ibatis.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface IbatisConstants {

    public static final String CONFIGURATION_DTDS[] = {
            "http://ibatis.apache.org/dtd/sql-map-config-2.dtd", "http://www.ibatis.com/dtd/sql-map-config-2.dtd"
    };
    public static final String SQLMAP_DTDS[] = {
            "http://ibatis.apache.org/dtd/sql-map-2.dtd", "http://www.ibatis.com/dtd/sql-map-2.dtd"
    };

    public static final String ABATOR_DTDS[] = {"http://ibatis.apache.org/dtd/abator-config_1_0.dtd"};

    public Icon IBATIS_LOGO = IconLoader.findIcon("/icons/logo.png");

    //class related
    public Icon CLASS_FIELD = IconLoader.findIcon("/icons/class_field.png");
    public Icon INTERNAL_CLASS = IconLoader.findIcon("/icons/internal_class.png");
    public Icon TYPE_ALIAS = IconLoader.findIcon("/icons/typealias.png");

    //database related field
    public Icon DATABASE_TABLE = IconLoader.findIcon("/icons/dbTable.png");
    public Icon DATABASE_PK_FIELD = IconLoader.findIcon("/icons/dataPkColumn.png");
    public Icon DATABASE_COMMON_FIELD = IconLoader.findIcon("/icons/dataColumn.png");

    //icon for sentence in sql map xml file
    public Icon SQLMAP_SELECT = IconLoader.findIcon("/icons/sqlmap/select.png");
    public Icon SQLMAP_DELETE = IconLoader.findIcon("/icons/sqlmap/delete.png");
    public Icon SQLMAP_INSERT = IconLoader.findIcon("/icons/sqlmap/insert.png");
    public Icon SQLMAP_UPDATE = IconLoader.findIcon("/icons/sqlmap/update.png");
    public Icon SQLMAP_PROCEDURE = IconLoader.findIcon("/icons/sqlmap/procedure.png");
    public Icon SQLMAP_STATEMENT = IconLoader.findIcon("/icons/sqlmap/statement.png");
}
