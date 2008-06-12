package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class WrappedReferenceProvider extends PsiReferenceProvider {

    protected final PsiReferenceProvider myProvider;

    protected WrappedReferenceProvider(PsiReferenceProvider provider) {
        myProvider = provider;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        return myProvider.getReferencesByElement(psiElement, processingContext);
    }

}
