package org.intellij.ibatis;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.ide.IconProvider;
import com.intellij.javaee.ExternalResourceManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.psi.PsiElement;
import org.intellij.ibatis.facet.IbatisFacetType;
import org.intellij.ibatis.inspections.*;
import org.intellij.ibatis.util.IbatisBundle;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.*;

import javax.swing.*;

/**
 * iBATIS application component, include inspection, IconProvider
 *
 * @authtor Jacky
 */
public class IbatisApplicationComponent implements ApplicationComponent, InspectionToolProvider, IconProvider {

    public IbatisApplicationComponent() {
    }

    public void initComponent() {
        registerDTDs(IbatisConstants.CONFIGURATION_DTDS);
        registerDTDs(IbatisConstants.SQLMAP_DTDS);
        registerDTDs(IbatisConstants.ABATOR_DTDS);
        FacetTypeRegistry.getInstance().registerFacetType(IbatisFacetType.INSTANCE);
    }

    public void disposeComponent() {
    }

    @NotNull public String getComponentName() {
        return IbatisBundle.message("ibatis.application.component.name");
    }

    /**
     * register DTD for iBATIS
     *
     * @param dtdArray URLs
     */
    private void registerDTDs(String dtdArray[]) {
        for (String url : dtdArray) {
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
     * @param psiElement PsiFile sub class
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
        return new Class[]{SqlMapFileInConfigurationInspection.class,
                NullSettedToPrimaryTypeInspection.class, ResultMapInSelectInspection.class,
                SymbolInSQLInspection.class, ParameterMapInStatementInspection.class};
    }
}
