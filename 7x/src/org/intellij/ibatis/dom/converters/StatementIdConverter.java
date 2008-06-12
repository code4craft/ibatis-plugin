/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package org.intellij.ibatis.dom.converters;

import com.intellij.psi.*;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;


/**
 * statement id converter
 *
 * @author linux_china@hotmail.com
 */
public class StatementIdConverter implements CustomReferenceConverter<String> {
    @NotNull
    public PsiReference[] createReferences(final GenericDomValue<String> genericDomValue, final PsiElement element, final ConvertContext context) {
        return createDefaultReferences(genericDomValue, element);
    }

    private static PsiReference[] createDefaultReferences(final GenericDomValue<String> genericDomValue, final PsiElement element) {
        return new PsiReference[]{new PsiReferenceBase<PsiElement>(element) {

            public PsiElement resolve() {
                return getElement().getParent().getParent();
            }

            public boolean isSoft() {
                return true;
            }

            public Object[] getVariants() {
                return null;
            }
        }};
    }

}