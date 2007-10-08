package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.intellij.ibatis.dom.converters.ResultMapConverter;

/**
 * parameter element for parameterMap
 *
 * @author Jacky
 */
public interface Parameter extends CommonDomModelElement {

    public GenericAttributeValue<String> getProperty();

    public GenericAttributeValue<String> getJavaType();

    public GenericAttributeValue<String> getJdbcType();

    public GenericAttributeValue<String> getMode();

    @Convert(ResultMapConverter.class)
    public GenericAttributeValue<String> getResultMap();

    public GenericAttributeValue<Integer> getNumericScale();

}
