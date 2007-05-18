package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import org.jetbrains.annotations.NotNull;

public class WrappedReferenceProvider extends BaseReferenceProvider {

    protected final PsiReferenceProvider myProvider;

    protected WrappedReferenceProvider(PsiReferenceProvider provider) {
        myProvider = provider;
    }

    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        return myProvider.getReferencesByElement(psiElement);
    }

}
