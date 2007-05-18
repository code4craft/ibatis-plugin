package org.intellij.ibatis;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.ide.IconProvider;
import com.intellij.javaee.ExternalResourceManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.psi.PsiElement;
import org.intellij.ibatis.facet.IbatisFacetType;
import org.intellij.ibatis.insepections.SqlMapIdUniqueProblemsInspection;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IbatisApplicationComponent implements ApplicationComponent, InspectionToolProvider, IconProvider {

    public IbatisApplicationComponent() {
    }

    public void initComponent() {
        registerDTDs(IbatisConstants.CONFIGURATION_DTDS);
        registerDTDs(IbatisConstants.SQLMAP_DTDS);
        FacetTypeRegistry.getInstance().registerFacetType(IbatisFacetType.INSTANCE);
    }

    public void disposeComponent() {
    }

    @NotNull public String getComponentName() {
        return "iBATIS Application Component";
    }

    private static void registerDTDs(String dtds[]) {
        for (String url : dtds) {
            if (url.startsWith("http://")) {
                int pos = url.lastIndexOf('/');
                @NonNls String file = "/org/intellij/ibatis/dtds" + url.substring(pos);
                ExternalResourceManager.getInstance().addStdResource(url, file, IbatisApplicationComponent.class);
            }
        }
    }

    @Nullable public Icon getIcon(@NotNull PsiElement psiElement, int i) {
        return null;
    }

    public Class[] getInspectionClasses() {
        return new Class[]{SqlMapIdUniqueProblemsInspection.class};
    }
}
