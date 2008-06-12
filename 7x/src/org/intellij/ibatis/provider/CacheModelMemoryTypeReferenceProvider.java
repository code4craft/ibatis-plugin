package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author yangsy.cq@gmail.com
 *         <p/>
 *         cache model reference provider
 */
public class CacheModelMemoryTypeReferenceProvider extends BaseReferenceProvider {
  private String[] types = new String[]{
      "WEAK", "SOFT", "STRONG"
  };

  @NotNull
  public PsiReference[] getReferencesByElement(PsiElement psiElement) {
    XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
    XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
      public boolean isSoft() {
        return true;
      }

      public Object[] getVariants() {
        if (getElement().getParent() != null && getElement().getParent().getParent() != null && getElement().getParent().getParent().getParent() != null) {
          XmlTag property = (XmlTag) getElement().getParent().getParent();
          XmlTag cacheModel = (XmlTag) property.getParent();
          XmlAttribute attribute = cacheModel.getAttribute("type");
          if (attribute != null) {
            String type = attribute.getValue();
            if ("MEMORY".equals(type)) {
              XmlAttribute name = property.getAttribute("name");
              if(name != null) {
                String val = name.getValue();
                if("reference-type".equals(val) || "referenceType".equals(val)) {
                  return types;
                }
              }
            }
          }
        }

        return null;
      }
    };
    return new PsiReference[]{psiReference};
  }

}