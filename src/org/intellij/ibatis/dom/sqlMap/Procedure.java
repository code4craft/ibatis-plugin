package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * procedure element
 */
public interface Procedure extends CommonDomModelElement {
    public GenericAttributeValue<String> getId();

     public GenericAttributeValue<String> getParameterClass();

    public GenericAttributeValue<String> getParameterMap();

    public GenericAttributeValue<String> getResultClass();

    public GenericAttributeValue<String> getResultMap();
}