package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.converters.ResultMapConverter;
import org.intellij.ibatis.dom.converters.SelectConverter;

/**
 * result element
 *
 * @author Jacky
 */
public interface Result extends CommonDomModelElement {
    public GenericAttributeValue<String> getProperty();

    public GenericAttributeValue<String> getColumn();

    public GenericAttributeValue<String> getJdbcType();

    @Convert(ResultMapConverter.class)
    public GenericAttributeValue<ResultMap> getResultMap();

    public GenericAttributeValue<String> getNullValue();

    @Convert(SelectConverter.class)
    @NameValue(referencable = false)
    public GenericAttributeValue<Select> getSelect();
}
