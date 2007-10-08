package org.intellij.ibatis.dom.sqlMap;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * base result statement, such select, statement, procedure.
 */
public interface BaseResultStatement extends BaseStatement {
    /**
     * get result class for statement
     *
     * @return result class
     */
    @Attribute("resultClass")
    public GenericAttributeValue<String> getResultClass();

    /**
     * get result map for statement
     *
     * @return result map
     */
    @Attribute("resultMap")
    public GenericAttributeValue<String> getResultMap();
}
