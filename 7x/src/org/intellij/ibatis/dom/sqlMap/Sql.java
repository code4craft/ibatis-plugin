package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * SQL select element
 */
public interface Sql extends CommonDomModelElement {
    @NotNull
    public GenericAttributeValue<String> getId();

    /**
     * get the SQL code
     *
     * @return SQL sentence
     */
    @NotNull public String getSQL();
}
