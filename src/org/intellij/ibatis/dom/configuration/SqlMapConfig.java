package org.intellij.ibatis.dom.configuration;

import com.intellij.javaee.model.xml.CommonDomModelRootElement;
import com.intellij.util.xml.SubTagList;
import com.intellij.util.xml.SubTagsList;

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

    @SubTagList("sqlMap")
    public List<SqlMap> getSqlMaps();
}
