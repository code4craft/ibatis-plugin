package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.intellij.ibatis.dom.converters.JdbcTypeConverter;
import org.intellij.ibatis.dom.converters.ResultMapConverter;
import org.intellij.ibatis.model.JdbcType;

/**
 * parameter element for parameterMap
 *
 * @author Jacky
 */
public interface Parameter extends CommonDomModelElement {

    public GenericAttributeValue<String> getProperty();

    public GenericAttributeValue<String> getJavaType();

   @Convert(JdbcTypeConverter.class)
    public GenericAttributeValue<JdbcType> getJdbcType();

    public GenericAttributeValue<String> getMode();

    @Convert(ResultMapConverter.class)
    public GenericAttributeValue<String> getResultMap();

    public GenericAttributeValue<Integer> getNumericScale();

}
