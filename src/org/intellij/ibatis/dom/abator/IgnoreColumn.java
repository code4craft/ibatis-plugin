package org.intellij.ibatis.dom.abator;

import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.intellij.ibatis.dom.converters.abator.TableColumnConverter;
import org.jetbrains.annotations.NotNull;

/**
 * ignoreColumn element
 */
public interface IgnoreColumn extends CommonDomModelElement {
    @NotNull
    @Convert(value = TableColumnConverter.class, soft = false)
    @Attribute("column")
    public GenericAttributeValue<DatabaseTableFieldData> getColumn();
}
