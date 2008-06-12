package org.intellij.ibatis.structure;

import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.ide.structureView.xml.XmlStructureViewBuilderProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * iBATIS SQL Map structure view builder provider
 *
 * @author linux_china@hotmail.com
 */
public class SqlMapStructureViewBuilderProvider implements XmlStructureViewBuilderProvider {
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
                return new SqlMapStructureViewTreeModel(xmlFile, fileElement.getRootElement());
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
        return domManager.getFileElement(xmlFile, SqlMap.class);
    }

}


