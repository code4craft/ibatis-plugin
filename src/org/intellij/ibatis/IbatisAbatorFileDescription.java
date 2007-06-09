package org.intellij.ibatis;

import com.intellij.util.xml.DomFileDescription;
import org.intellij.ibatis.dom.abator.*;
import org.intellij.ibatis.dom.abator.impl.*;

/**
 * file description for abator
 */
public class IbatisAbatorFileDescription extends DomFileDescription<AbatorConfiguration> {
    public IbatisAbatorFileDescription() {
        super(AbatorConfiguration.class, "abatorConfiguration");
    }

    protected void initializeFileDescription() {
        registerImplementation(AbatorContext.class, AbatorContextImpl.class);
        registerImplementation(Table.class, TableImpl.class);
        registerImplementation(GeneratedKey.class, GeneratedKeyImpl.class);
        registerImplementation(ColumnOverride.class, ColumnOverrideImpl.class);
        registerImplementation(IgnoreColumn.class, IgnoreColumnImpl.class);
    }
}
