package org.intellij.ibatis.dom.configuration;

import com.intellij.javaee.model.xml.CommonDomModelRootElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * iBATIS configuration xml file model
 */
public interface SqlMapConfig extends CommonDomModelRootElement {
    public Properties getProperties();

    public Settings getSettings();

    public ResultObjectFactory getResultObjectFactory();

    @SubTagList("typeAlias")
    public List<TypeAlias> getTypeAlias();

    @SubTagList("typeHandler")
    public List<TypeHandler> getTypeHandlers();

    @SubTagList("sqlMap")
    public List<SqlMap> getSqlMaps();

    @SubTagList("sqlMap")
    public SqlMap addSqlMap();
}
