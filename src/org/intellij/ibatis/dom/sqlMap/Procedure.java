package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * procedure element
 */
public interface Procedure extends BaseStatement {
    
    public GenericAttributeValue<String> getResultClass();

    public GenericAttributeValue<String> getResultMap();
}