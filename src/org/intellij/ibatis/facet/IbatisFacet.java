/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package org.intellij.ibatis.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dmitry Avdeev
 */
public class IbatisFacet extends Facet<IbatisFacetConfiguration> {
  public final static FacetTypeId<IbatisFacet> FACET_TYPE_ID = new FacetTypeId<IbatisFacet>();

  public IbatisFacet(final FacetType facetType, final Module module, final String name, final IbatisFacetConfiguration configuration, final Facet underlyingFacet) {
    super(facetType, module, name, configuration, underlyingFacet);
  }

  @Nullable
  public static IbatisFacet getInstance(Module module) {
	  if(module == null) return null;
	  FacetManager facetManager = FacetManager.getInstance(module);
	  if (null != facetManager) {
		  return facetManager.getFacetByType(FACET_TYPE_ID);
	  } else {
		  return null;
	  }
  }
}