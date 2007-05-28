package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.TagValue;

/**
 * select element
 */
public interface Select extends CommonDomModelElement {

    @TagValue
    public String getvalue();

    public GenericAttributeValue<String> getId();

    public GenericAttributeValue<String> getParameterClass();

    public GenericAttributeValue<String> getParameterMap();

    public GenericAttributeValue<String> getResultClass();

    public GenericAttributeValue<String> getResultMap();

    public ResultMap getReferencedResultMap();
}
