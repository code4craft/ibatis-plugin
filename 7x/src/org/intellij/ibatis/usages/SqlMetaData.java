package org.intellij.ibatis.usages;

import com.intellij.util.xml.*;
import com.intellij.openapi.util.IconLoader;
import org.intellij.ibatis.dom.sqlMap.BaseStatement;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * sql meta data
 *
 * @author linux_china@hotmail.com
 */
public class SqlMetaData extends DomMetaData<Sql> {
    /**
     * get name element
     *
     * @param element element
     * @return dom value
     */
    @Nullable
    protected GenericDomValue getNameElement(final Sql element) {
        final GenericAttributeValue<String> id = element.getId();
        if (id.getXmlElement() != null) {
            return id;
        }
        return null;
    }

    /**
     * get icon for statement
     *
     * @return icon
     */
    @Override public Icon getIcon() {
        return IconLoader.findIcon("/hierarchy/callee.png");
    }


    /**
     * get display name for usage find
     *
     * @return dialog
     */
    public String getTypeName() {
        return getElement().getXmlElementName() + " Id: ";
    }
}