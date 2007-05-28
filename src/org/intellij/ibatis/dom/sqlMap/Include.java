package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.TagValue;

/**
 * sql select element
 */
public interface Include extends CommonDomModelElement {
    public GenericAttributeValue<String> getRefid();

    @TagValue String getValue();
}