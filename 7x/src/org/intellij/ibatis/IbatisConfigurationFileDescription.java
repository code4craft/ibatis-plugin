package org.intellij.ibatis;

import com.intellij.util.xml.DomFileDescription;
import org.intellij.ibatis.dom.configuration.*;
import org.intellij.ibatis.dom.configuration.impl.*;

/**
 * file description for iBATIS configuration xml file
 */
public class IbatisConfigurationFileDescription extends DomFileDescription<SqlMapConfig> {
    public IbatisConfigurationFileDescription() {
        super(SqlMapConfig.class, "sqlMapConfig");
    }

    protected void initializeFileDescription() {
        registerImplementation(Properties.class, PropertiesImpl.class);
        registerImplementation(Settings.class, SettingsImpl.class);
        registerImplementation(ResultObjectFactory.class, ResultObjectFactoryImpl.class);
        registerImplementation(TypeAlias.class, TypeAliasImpl.class);
        registerImplementation(SqlMap.class, SqlMapImpl.class);
    }
}
