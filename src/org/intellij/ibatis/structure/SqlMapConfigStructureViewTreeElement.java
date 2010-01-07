package org.intellij.ibatis.structure;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.Function;
import com.intellij.util.xml.*;
import com.intellij.util.xml.structure.DomStructureTreeElement;
import org.intellij.ibatis.dom.configuration.*;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL Map config structure view tree element.
 *
 * @author linux_china@hotmail.com
 */
@SuppressWarnings({"ConstantConditions", "MissingClassJavaDoc", "MissingMethodJavaDoc"})
public class SqlMapConfigStructureViewTreeElement extends DomStructureTreeElement {

    private static final Function<DomElement, DomService.StructureViewMode> MY_STRUCTURE_VIEW_MODE_FUNCTION =
            new Function<DomElement, DomService.StructureViewMode>() {
                public DomService.StructureViewMode fun(final DomElement domElement) {
                    return DomService.StructureViewMode.SHOW;
                }
            };

    /**
     * construct structure view tree element
     *
     * @param domElement dom element
     */
    public SqlMapConfigStructureViewTreeElement(@NotNull final DomElement domElement) {
        super(domElement, MY_STRUCTURE_VIEW_MODE_FUNCTION,
                DomElementsNavigationManager.getManager(domElement.getManager().getProject()).
                        getDomElementsNavigateProvider(DomElementsNavigationManager.DEFAULT_PROVIDER_NAME));
    }

    @Override @Nullable public Icon getIcon(boolean b) {
        DomElement element = getElement();
        if (element instanceof SqlMapConfig) {
            return IbatisConstants.IBATIS_LOGO;
        } else if (element instanceof TypeAlias) {
            return IbatisConstants.TYPE_ALIAS;
        } else if (element instanceof SqlMap) {
            return IconLoader.findIcon("/fileTypes/xml.png");
        } else if (element instanceof Properties) {
            return IconLoader.findIcon("/fileTypes/properties.png");
        }
        return null;
    }

    /**
     * get presentable text for tree node
     *
     * @return tree node name
     */
    @Override public String getPresentableText() {
        return getElement().getXmlTag().getName();
    }

    /**
     * get children for tree element
     *
     * @return tree element
     */
    public TreeElement[] getChildren() {
        final TreeElement[] elements = super.getChildren();
        final List<SqlMapConfigStructureViewTreeElement> myList = new ArrayList<SqlMapConfigStructureViewTreeElement>(elements.length);
        for (final TreeElement treeElement : elements) {
            myList.add(new SqlMapConfigStructureViewTreeElement(((DomStructureTreeElement) treeElement).getElement()));
        }
        return myList.toArray(new SqlMapConfigStructureViewTreeElement[myList.size()]);
    }

    /**
     * Add some extra text behind element presentation.
     *
     * @return null if no extra text is provided for the current element.
     */
    @Nullable
    public String getLocationString() {
        final DomElement element = getElement();
        if (element instanceof SqlMap) {
            return ((SqlMap) element).getResource().getStringValue();
        } else if (element instanceof TypeAlias) {
            return ((TypeAlias) element).getAlias().getStringValue();
        } else if (element instanceof Properties) {
            return ((Properties) element).getResource().getStringValue();
        }
        return super.getLocationString();
    }

}