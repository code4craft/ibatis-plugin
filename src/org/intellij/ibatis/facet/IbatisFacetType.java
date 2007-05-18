/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package org.intellij.ibatis.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Dmitry Avdeev
 */
public class IbatisFacetType extends FacetType<IbatisFacet, IbatisFacetConfiguration> {

    public final static IbatisFacetType INSTANCE = new IbatisFacetType();

    private IbatisFacetType() {
        super(IbatisFacet.FACET_TYPE_ID, "iBATIS", "iBATIS");
    }

    public IbatisFacetConfiguration createDefaultConfiguration() {
        return new IbatisFacetConfiguration();
    }

    public IbatisFacet createFacet(@NotNull final Module module, final String name, @NotNull final IbatisFacetConfiguration configuration,
                                   final Facet underlyingFacet) {
        return new IbatisFacet(this, module, name, configuration, underlyingFacet);
    }

    public boolean isOnlyOneFacetAllowed() {
        return true;
    }

    public boolean isSuitableModuleType(final ModuleType moduleType) {
        return moduleType == ModuleType.JAVA;
    }

    public Icon getIcon() {
        return IbatisConstants.IBATIS_LOGO;
    }
}