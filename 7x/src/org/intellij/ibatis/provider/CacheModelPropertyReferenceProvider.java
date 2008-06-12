package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yangsy.cq@gmail.com
 *
 * cache model reference provider
 */
public class CacheModelPropertyReferenceProvider extends BaseReferenceProvider {
  private String[] sizes = new String[]{"cache-size","size"};
  private String[] memorys = new String[]{"reference-type","referenceType"};


  @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
          public boolean isSoft() {
            return true;
          }

          @Nullable
          public PsiElement resolve() {
            return super.resolve();
          } 

          public Object[] getVariants() {
            if (getElement().getParent() != null && getElement().getParent().getParent()!= null && getElement().getParent().getParent().getParent() != null) {
              XmlTag cacheModel = (XmlTag) getElement().getParent().getParent().getParent();
              XmlAttribute attribute = cacheModel.getAttribute("type");
              if (attribute != null) {
                String type = attribute.getValue();
                if("LRU".equals(type) || "FIFO".equals(type)) {
                  return sizes;
                } else if("MEMORY".equals(type)) {
                  return memorys;
                }
              }
            }

            return null;
          }
        };
        return new PsiReference[]{psiReference};
    }

}