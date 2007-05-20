package org.intellij.ibatis;

import com.intellij.util.xml.DomFileDescription;
import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.dom.sqlMap.impl.*;

/**
 * file description for iBATIS configuration xml file
 */
public class IbatisSqlMapFileDescription extends DomFileDescription<SqlMap> {
    public IbatisSqlMapFileDescription() {
        super(SqlMap.class, "sqlMap");
    }

    protected void initializeFileDescription() {
        registerImplementation(TypeAlias.class, TypeAliasImpl.class);
        registerImplementation(ParameterMap.class, ParameterMapImpl.class);
        registerImplementation(ResultMap.class, ResultMapImpl.class);
        registerImplementation(Result.class, ResultImpl.class);
        registerImplementation(Sql.class, SqlImpl.class);
        registerImplementation(Select.class, SelectImpl.class);
        registerImplementation(Insert.class, InsertImpl.class);
        registerImplementation(Update.class, UpdateImpl.class);
        registerImplementation(Delete.class, DeleteImpl.class);
        registerImplementation(Statement.class, StatementImpl.class);
        registerImplementation(Procedure.class, ProcedureImpl.class);
        registerImplementation(CacheModel.class, CacheModelImpl.class);
    }
}