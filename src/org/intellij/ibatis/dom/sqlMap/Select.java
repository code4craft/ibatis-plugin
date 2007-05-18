package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * select element
 */
public interface Select extends CommonDomModelElement {
    public GenericAttributeValue<String> getId();
}
