package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelRootElement;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * iBATIS sqlmap configuration file
 */
public interface SqlMap extends CommonDomModelRootElement {
    @Nullable
    public GenericAttributeValue<String> getNamespace();

    @SubTagList("typeAlias")
    public List<TypeAlias> getTypeAlias();

    @SubTagList("parameterMap")
    public List<ParameterMap> getParameterMap();

    @SubTagList("resultMap")
    public List<ResultMap> getResultMaps();

    @SubTagList("sql")
    public List<Sql> getSqls();

    @SubTagList("select")
    public List<Select> getSelects();

    @NotNull
    public Select addSelect();

    @SubTagList("insert")
    public List<Insert> getInserts();

    @NotNull
    public Insert addInsert();

    @SubTagList("update")
    public List<Update> getUpdates();

    @NotNull
    public Update addUpdate();
    
    @SubTagList("delete")
    public List<Delete> getDeletes();

    @NotNull
    public Delete addDelete();
    
    @SubTagList("statement")
    public List<Statement> getStatements();

    @NotNull
    public Statement addStatement();

    @SubTagList("procedure")
    public List<Procedure> getProcedures();

    @NotNull
    public Procedure addProcedure();

    @SubTagsList({"select", "insert", "delete", "update", "statement", "procedure"})
    public List<DomElement> getAllReference();

    @SubTagList("cacheModel")
    public List<CacheModel> getCacheModels();

}
