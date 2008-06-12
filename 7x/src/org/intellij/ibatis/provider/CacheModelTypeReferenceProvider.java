package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author yangsy.cq@gmail.com
 *
 * cache model reference provider
 */
public class CacheModelTypeReferenceProvider extends BaseReferenceProvider {
  private String[] types = new String[]{
      "LRU","MEMORY","FIFO","OSCACHE"
  };

  @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {

            public Object[] getVariants() {
              return types;
            }
        };
        return new PsiReference[]{psiReference};
    }

}