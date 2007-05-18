package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * sql select element
 */
public interface Sql extends CommonDomModelElement {
    @NotNull
    public GenericAttributeValue<String> getId();
}
