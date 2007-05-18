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

    public Icon IBATIS_LOGO = IconLoader.findIcon("/icons/logo.png");
    public Icon DATABASE_PK_FIELD = IconLoader.findIcon("/icons/dataPkColumn.png");
    public Icon DATABASE_COMMON_FIELD = IconLoader.findIcon("/icons/dataColumn.png");
    public Icon CLASS_FIELD = IconLoader.findIcon("/icons/class_field.png");
}
