package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import org.intellij.ibatis.dom.converters.SelectConverter;

/**
 * result element
 */
public interface Result extends CommonDomModelElement {
    public GenericAttributeValue<String> getProperty();

    public GenericAttributeValue<String> getColumn();

    public GenericAttributeValue<String> getJdbcType();

    public GenericAttributeValue<String> getResultMap();

    public GenericAttributeValue<String> getNullValue();

    @Convert(SelectConverter.class)
    @NameValue(referencable = false)
    public GenericAttributeValue<Select> getSelect();
}
