package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * statement element
 */
public interface Statement extends BaseStatement {
    public GenericAttributeValue<String> getResultClass();

    public GenericAttributeValue<String> getResultMap();
}