package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * cacheModel select element
 */
public interface CacheModel extends CommonDomModelElement {
    @NotNull
    public GenericAttributeValue<String> getId();

    public GenericAttributeValue<String> getType();
}