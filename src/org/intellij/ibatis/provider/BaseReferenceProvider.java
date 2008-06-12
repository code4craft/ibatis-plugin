package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * base reference  provider
 */
public class BaseReferenceProvider extends PsiReferenceProvider {
    @NotNull
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        return getReferencesByElement(psiElement);
    }

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        return PsiReference.EMPTY_ARRAY;
    }

}
