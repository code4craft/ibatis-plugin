package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * base reference
 */
public class BaseReferenceProvider implements PsiReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        return PsiReference.EMPTY_ARRAY;
    }

    @Deprecated
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement, ReferenceType referenceType) {
        return new PsiReference[0];
    }

    @NotNull
    @Deprecated
    public PsiReference[] getReferencesByString(String s, PsiElement psiElement, ReferenceType referenceType, int i) {
        return new PsiReference[0];  
    }

    public void handleEmptyContext(PsiScopeProcessor psiScopeProcessor, PsiElement psiElement) {

    }
}
