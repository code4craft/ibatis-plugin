package org.intellij.ibatis.structure;

import com.intellij.ide.structureView.*;
import com.intellij.ide.structureView.impl.xml.XmlStructureViewBuilderProvider;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * SQL Map config structure view builder
 *
 * @author linux_china@hotmail.com
 */
public class SqlMapConfigStructureViewBuilderProvider implements XmlStructureViewBuilderProvider {

    /**
     * construct view builder
     *
     * @param xmlFile xml file
     * @return structure view builder
     */
    @Nullable public StructureViewBuilder createStructureViewBuilder(@NotNull final XmlFile xmlFile) {
        final DomFileElement fileElement = getFileElement(xmlFile);
        if (fileElement == null) {
            return null;
        }
        return new TreeBasedStructureViewBuilder() {
            @NotNull
            public StructureView createStructureView(final FileEditor fileEditor, final Project project) {
                return new StructureViewComponent(fileEditor, createStructureViewModel(), project);
            }

            @NotNull
            public StructureViewModel createStructureViewModel() {
                return new SqlMapConfigStructureViewTreeModel(xmlFile, fileElement.getRootElement());
            }
        };
    }

    /**
     * get dom file element
     *
     * @param xmlFile xml file
     * @return dom file element
     */
    @Nullable
    protected DomFileElement getFileElement(@NotNull final XmlFile xmlFile) {
        final DomManager domManager = DomManager.getDomManager(xmlFile.getProject());
        return domManager.getFileElement(xmlFile, SqlMapConfig.class);
    }
}
