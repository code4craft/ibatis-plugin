package org.intellij.ibatis.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.xml.XmlStructureViewTreeModel;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * SQL Map config structure view tree model
 *
 * @author linux_china@hotmail.com
 */
public class SqlMapConfigStructureViewTreeModel extends XmlStructureViewTreeModel {
    private final DomElement rootElement;

    /**
     * construct view tree model
     *
     * @param xmlFile     xml file
     * @param rootElement root element
     */
    public SqlMapConfigStructureViewTreeModel(@NotNull final XmlFile xmlFile, @NotNull final DomElement rootElement) {
        super(xmlFile);
        this.rootElement = rootElement;
    }

    /**
     * get root tree element element
     *
     * @return root tree element
     */
    @NotNull
    public StructureViewTreeElement getRoot() {
        return new SqlMapConfigStructureViewTreeElement(rootElement);
    }

    /**
     * get sorter,current alpha sorted used
     *
     * @return sorter
     */
    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }

}