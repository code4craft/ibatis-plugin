package org.intellij.ibatis.usages;

import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.sqlMap.BaseStatement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * statement meta data
 *
 * @author linux_china@hotmail.com
 */
public class StatementMetaData extends DomMetaData<BaseStatement> {
    /**
     * get name element
     *
     * @param element element
     * @return dom value
     */
    @Nullable
    protected GenericDomValue getNameElement(final BaseStatement element) {
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
        return getElement().getIcon(0);
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