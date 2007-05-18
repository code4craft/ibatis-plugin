package org.intellij.ibatis.provider;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;

/**
 * xml attribute value psi reference, a XmlAttributeValue object should be supplied
 */
public class XmlAttributeValuePsiReference implements PsiReference {
    private XmlAttributeValue xmlAttributeValue;

    public XmlAttributeValuePsiReference(XmlAttributeValue xmlAttributeValue) {
        this.xmlAttributeValue = xmlAttributeValue;
    }

    public PsiElement getElement() {
        return this.xmlAttributeValue;
    }

    public TextRange getRangeInElement() {
        return new TextRange(1, xmlAttributeValue.getValue().length() + 1);
    }

    @Nullable public PsiElement resolve() {
        return null;
    }

    public String getCanonicalText() {
        return xmlAttributeValue.getValue();
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;
    }

    public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException {
        return null;
    }

    public boolean isReferenceTo(PsiElement element) {
        return element == resolve();
    }

    public Object[] getVariants() {
        return new Object[0];
    }

    public boolean isSoft() {
        return true;
    }
}
