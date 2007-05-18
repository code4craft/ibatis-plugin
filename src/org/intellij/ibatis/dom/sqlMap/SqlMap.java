package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelRootElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import com.intellij.util.xml.SubTagsList;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * iBATIS sqlmap configuration file
 */
public interface SqlMap extends CommonDomModelRootElement {
    @Nullable
    public GenericAttributeValue<String> getNamesapce();

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

    @SubTagList("insert")
    public List<Insert> getInserts();

    @SubTagList("update")
    public List<Update> getUpdates();

    @SubTagList("delete")
    public List<Delete> getDeletes();

    @SubTagList("statement")
    public List<Statement> getStatements();

    @SubTagList("procedue")
    public List<Procedure> getProcedures();

    @SubTagsList({"select", "insert", "delete", "update", "statement", "procedure"})
    public List<DomElement> getAllReference();

}
