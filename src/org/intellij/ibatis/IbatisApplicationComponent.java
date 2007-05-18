package org.intellij.ibatis;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.ide.IconProvider;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.impl.FileTemplateImpl;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.javaee.ExternalResourceManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.psi.PsiElement;
import org.intellij.ibatis.facet.IbatisFacetType;
import org.intellij.ibatis.insepections.SqlMapFileInConfigurationInspection;
import org.intellij.ibatis.insepections.SqlMapUniqueIdInspection;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * iBATIS application component, include inspection, IconProvider
 *
 * @authtor Jacky
 */
public class IbatisApplicationComponent implements ApplicationComponent, InspectionToolProvider, IconProvider {
    private static String SQLMAP_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n" +
            "<!DOCTYPE sqlMap PUBLIC \"-//ibatis.apache.org//DTD SQL Map 2.0//EN\"\n" +
            "    \"http://ibatis.apache.org/dtd/sql-map-2.dtd\">\n\n" +
            "<sqlMap>\n\n</sqlMap>";

    public IbatisApplicationComponent() {
    }

    public void initComponent() {
        registerDTDs(IbatisConstants.CONFIGURATION_DTDS);
        registerDTDs(IbatisConstants.SQLMAP_DTDS);
        FacetTypeRegistry.getInstance().registerFacetType(IbatisFacetType.INSTANCE);
        final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance();
        if (fileTemplateManager.getInternalTemplate("iBATIS Sql Map File") == null) {
            FileTemplateImpl template = (FileTemplateImpl) fileTemplateManager.addTemplate("iBATIS Sql Map File", XmlFileType.DEFAULT_EXTENSION);
            template.setInternal(true);
            template.setText(SQLMAP_CONTENT);
        }
    }

    public void disposeComponent() {
    }

    @NotNull public String getComponentName() {
        return "iBATIS Application Component";
    }

    /**
     * register DTD for iBATIS
     *
     * @param dtds DTD URLs
     */
    private static void registerDTDs(String dtds[]) {
        for (String url : dtds) {
            if (url.startsWith("http://")) {
                int pos = url.lastIndexOf('/');
                @NonNls String file = "/org/intellij/ibatis/dtds" + url.substring(pos);
                ExternalResourceManager.getInstance().addStdResource(url, file, IbatisApplicationComponent.class);
            }
        }
    }

    /**
     * icon for special psiFile
     *
     * @param psiElement PsiFile sub calss
     * @param i          i
     * @return icon for special psiFile
     */
    @Nullable public Icon getIcon(@NotNull PsiElement psiElement, int i) {
        return null;
    }

    /**
     * get all inspection class
     *
     * @return inspection class array
     */
    public Class[] getInspectionClasses() {
        return new Class[]{SqlMapUniqueIdInspection.class, SqlMapFileInConfigurationInspection.class};
    }
}
