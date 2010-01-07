package org.intellij.ibatis.structure;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.Function;
import com.intellij.util.xml.*;
import com.intellij.util.xml.structure.DomStructureTreeElement;
import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL Map structure view tree element.
 *
 * @author linux_china@hotmail.com
 */
@SuppressWarnings({"ConstantConditions", "MissingClassJavaDoc", "MissingMethodJavaDoc"})
public class SqlMapStructureViewTreeElement extends DomStructureTreeElement {

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
    public SqlMapStructureViewTreeElement(@NotNull final DomElement domElement) {
        super(domElement, MY_STRUCTURE_VIEW_MODE_FUNCTION,
                DomElementsNavigationManager.getManager(domElement.getManager().getProject()).
                        getDomElementsNavigateProvider(DomElementsNavigationManager.DEFAULT_PROVIDER_NAME));
    }

    @Override @Nullable public Icon getIcon(boolean b) {
        DomElement element = getElement();
        if (element instanceof SqlMap) {
            return IbatisConstants.IBATIS_LOGO;
        } else if (element instanceof TypeAlias) {
            return IbatisConstants.TYPE_ALIAS;
        } else if (element instanceof BaseStatement) {
            return ((BaseStatement) element).getIcon(0);
        } else if (element instanceof ParameterMap) {
            return IconLoader.findIcon("/nodes/parameter.png");
        } else if (element instanceof ResultMap) {
            return IconLoader.findIcon("/debugger/value.png");
        } else if (element instanceof CacheModel) {
            return IconLoader.findIcon("/javaee/persistenceEntity.png");
        } else if (element instanceof Sql) {
            return IconLoader.findIcon("/hierarchy/callee.png");
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
        final List<SqlMapStructureViewTreeElement> myList = new ArrayList<SqlMapStructureViewTreeElement>(elements.length);
        for (final TreeElement treeElement : elements) {
            myList.add(new SqlMapStructureViewTreeElement(((DomStructureTreeElement) treeElement).getElement()));
        }
        return myList.toArray(new SqlMapStructureViewTreeElement[myList.size()]);
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
            return ((SqlMap) element).getNamespace().getStringValue();
        } else if (element instanceof BaseStatement) {
            return ((BaseStatement) element).getId().getValue();
        } else if (element instanceof Parameter) {
            return ((Parameter) element).getProperty().getValue();
        } else if (element instanceof ResultMap) {
            return ((ResultMap) element).getId().getValue();
        } else if (element instanceof Result) {
            return ((Result) element).getProperty().getStringValue();
        } else if (element instanceof TypeAlias) {
            return ((TypeAlias) element).getAlias().getStringValue();
        } else if (element instanceof CacheModel) {
            return ((CacheModel) element).getId().getStringValue();
        } else if (element instanceof Sql) {
            return ((Sql) element).getId().getStringValue();
        }
        return super.getLocationString();
    }

}