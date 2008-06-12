package org.intellij.ibatis.dom.converters;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.intellij.ibatis.provider.XmlAttributeValuePsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * String array reference convert
 */
public abstract class StringArrayReferenceConverter implements CustomReferenceConverter<String> {
    /**
     * return string array render
     *
     * @return object for String array
     */
    public abstract Object[] getStringVariants();

    @NotNull
    public PsiReference[] createReferences(GenericDomValue<String> genericDomValue, PsiElement element, ConvertContext context) {
        if (!(element instanceof XmlAttributeValue)) return PsiReference.EMPTY_ARRAY;
        return new PsiReference[]{new XmlAttributeValuePsiReference((XmlAttributeValue) element) {
            public Object[] getVariants() {
                return getStringVariants();
            }
        }};
    }
}
