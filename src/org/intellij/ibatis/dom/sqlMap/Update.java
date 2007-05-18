package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * update element
 */
public interface Update extends CommonDomModelElement {
    public GenericAttributeValue<String> getId();
}