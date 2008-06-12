package org.intellij.ibatis.provider;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;

public class WrappedPsiReference implements PsiReference {

    public WrappedPsiReference(PsiReference psiReference) {
        myReference = psiReference;
    }

    public PsiElement getElement() {
        return myReference.getElement();
    }

    public TextRange getRangeInElement() {
        return myReference.getRangeInElement();
    }

    public PsiElement resolve() {
        return myReference.resolve();
    }

    public String getCanonicalText() {
        return myReference.getCanonicalText();
    }

    public PsiElement handleElementRename(String newElementName)
            throws IncorrectOperationException {
        return myReference.handleElementRename(newElementName);
    }

    public PsiElement bindToElement(PsiElement element)
            throws IncorrectOperationException {
        return myReference.bindToElement(element);
    }

    public boolean isReferenceTo(PsiElement element) {
        return myReference.isReferenceTo(element);
    }

    public Object[] getVariants() {
        return myReference.getVariants();
    }

    public boolean isSoft() {
        return myReference.isSoft();
    }

    protected PsiReference myReference;
}
