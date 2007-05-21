package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.ibatis.IbatisManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author yangsy.cq@gmail.com
 *         <p/>
 *         cache model reference provider
 */
public class ParameterResultMapReferenceProvider extends BaseReferenceProvider {


  @NotNull
  public PsiReference[] getReferencesByElement(PsiElement psiElement) {
    XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
    XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {

      public boolean isSoft() {
        return false;
      }

      @Nullable
      public PsiElement resolve() {
        String val = getCanonicalText();

        if (val.indexOf('.') == -1) {
          XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) getElement();
          XmlTag element = (XmlTag) xmlAttributeValue.getParent().getParent().getParent();
          String namespace = ((XmlTag) element.getParent()).getAttributeValue("namespace");
          if (namespace != null) {
            val = namespace + "." + val;
          }
        }
        Map<String, XmlTag> xmlTags = IbatisManager.getInstance().getAllResultMap2(getElement());
        if (xmlTags != null) {
          if (xmlTags.containsKey(val)) {
            return xmlTags.get(val).getAttribute("id");
          }
        }
        return null;

      }

      public Object[] getVariants() {
        Map<String, XmlTag> map = IbatisManager.getInstance().getAllResultMap2(getElement());
        return map.keySet().toArray();
      }
    };
    return new PsiReference[]{psiReference};
  }

}