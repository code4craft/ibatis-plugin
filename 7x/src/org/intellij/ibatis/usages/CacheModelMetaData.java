package org.intellij.ibatis.usages;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.sqlMap.CacheModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * cache model meta data
 *
 * @author linux_china@hotmail.com
 */
public class CacheModelMetaData extends DomMetaData<CacheModel> {
    /**
     * get name element
     *
     * @param element element
     * @return dom value
     */
    @Nullable
    protected GenericDomValue getNameElement(final CacheModel element) {
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
        return IconLoader.findIcon("/javaee/persistenceEntity.png");
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