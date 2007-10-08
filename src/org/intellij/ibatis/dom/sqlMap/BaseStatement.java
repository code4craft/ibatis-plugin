package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Attribute;
import org.jetbrains.annotations.NotNull;

/**
 * base statement in SQL Map file
 */
public interface BaseStatement extends CommonDomModelElement {
    /**
     * get the id for statement
     *
     * @return statement id
     */
    @Attribute("id")
    public GenericAttributeValue<String> getId();

    /**
     * get the parameter class for
     *
     * @return get parameter class
     */
    @Attribute("parameterClass")
    public GenericAttributeValue<String> getParameterClass();

    /**
     * get parameter Map for class
     *
     * @return parameter map class
     */
    @Attribute("parameterMap")
    public GenericAttributeValue<String> getParameterMap();

    /**
     * get the SQL code for statement
     *
     * @return SQL sentence
     */
    @NotNull public String getSQL();

}
