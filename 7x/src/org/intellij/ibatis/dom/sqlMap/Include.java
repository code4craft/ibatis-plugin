package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.converters.SqlConverter;

/**
 * SQL include element
 */
public interface Include extends CommonDomModelElement {
    @Convert(SqlConverter.class)
    public GenericAttributeValue<Sql> getRefid();

    @TagValue String getValue();
}