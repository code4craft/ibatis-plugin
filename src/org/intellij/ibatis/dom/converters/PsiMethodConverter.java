/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package org.intellij.ibatis.dom.converters;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixProvider;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * psi method converter
 *
 * @author jacky
 */
public abstract class PsiMethodConverter extends Converter<PsiMethod> implements CustomReferenceConverter<PsiMethod> {

    protected final static Object[] EMPTY_ARRAY = new Object[0];

    private final MethodAccepter myMethodAccepter;

    public PsiMethodConverter(MethodAccepter accepter) {
        myMethodAccepter = accepter;
    }

    public PsiMethodConverter() {
        this(new MethodAccepter());
    }

    protected static class MethodAccepter {
        public boolean accept(PsiMethod method) {
            return !method.isConstructor() &&
                    method.hasModifierProperty(PsiModifier.PUBLIC) &&
                    !method.hasModifierProperty(PsiModifier.STATIC);
        }
    }

    public PsiMethod fromString(@Nullable final String methodName, final ConvertContext context) {
        if (methodName == null || methodName.length() == 0) {
            return null;
        }
        final PsiClass psiClass = getPsiClass(context);
        if (psiClass != null) {
            final PsiMethod[] psiMethods = psiClass.findMethodsByName(methodName, true);
            if (psiMethods.length == 0) {
                return null;
            }
            final MethodAccepter accepter = getMethodAccepter(context, false);
            for (PsiMethod method : psiMethods) {
                if (accepter.accept(method)) {
                    return method;
                }
            }
            return psiMethods[0];
        } else {
            return null;
        }
    }

    public String toString(@Nullable final PsiMethod psiMethods, final ConvertContext context) {
        return null;
    }

    @Nullable
    protected abstract PsiClass getPsiClass(final ConvertContext context);

    protected MethodAccepter getMethodAccepter(ConvertContext context, final boolean forCompletion) {
        return myMethodAccepter;
    }

    protected Object[] getVariants(ConvertContext context) {
        final PsiClass psiClass = getPsiClass(context);
        if (psiClass == null) {
            return EMPTY_ARRAY;
        }
        final ArrayList<Object> result = new ArrayList<Object>();
        final MethodAccepter methodAccepter = getMethodAccepter(context, true);
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (methodAccepter.accept(method)) {
                result.add(method);
            }
        }
        return ElementPresentationManager.getInstance().createVariants(result);
    }

    @NotNull
    public PsiReference[] createReferences(final GenericDomValue<PsiMethod> genericDomValue, final PsiElement element, final ConvertContext context) {

        return new PsiReference[]{new MyReference(element, genericDomValue, context)};
    }

    protected class MyReference extends PsiReferenceBase<PsiElement> implements LocalQuickFixProvider {
        private final GenericDomValue<PsiMethod> myGenericDomValue;
        private final ConvertContext myContext;

        public MyReference(final PsiElement element,
                           final GenericDomValue<PsiMethod> genericDomValue,
                           ConvertContext context) {
            super(element);
            myGenericDomValue = genericDomValue;
            myContext = context;
        }

        public Object[] getVariants() {
            return PsiMethodConverter.this.getVariants(myContext);
        }

        @Nullable
        public PsiElement resolve() {
            return myGenericDomValue.getValue();
        }

        public boolean isSoft() {
            return true;
        }

        public PsiElement bindToElement(final PsiElement element) throws IncorrectOperationException {
            assert element instanceof PsiMethod : "PsiMethod expected";
            final PsiMethod psiMethod = (PsiMethod) element;
            myGenericDomValue.setStringValue(psiMethod.getName());
            return psiMethod;
        }

        public LocalQuickFix[] getQuickFixes() {
            return PsiMethodConverter.this.getQuickFixes(myContext);
        }
    }

    public LocalQuickFix[] getQuickFixes(final ConvertContext context) {
        return new LocalQuickFix[0];
    }
}