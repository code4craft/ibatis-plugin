package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.CacheModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * cache model reference provider
 */
public class CacheModelReferenceProvider extends BaseReferenceProvider {
  @NotNull
  public PsiReference[] getReferencesByElement(PsiElement psiElement) {
    XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
    XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
      public boolean isSoft() {
        return false;
      }

      @Nullable
      public PsiElement resolve() {
        String cacheModelId = getReferenceId(getElement());

        IbatisManager manager = IbatisManager.getInstance();
        Map<String, CacheModel> allResultMap = manager.getAllCacheModel(getElement());
        CacheModel cacheModel = allResultMap.get(cacheModelId);
        return cacheModel == null ? null : cacheModel.getId().getXmlAttribute();
      }

      public Object[] getVariants() {
        Set<String> cacheModelList = IbatisManager.getInstance().getAllCacheModel(getElement()).keySet();
        return cacheModelList.toArray();
      }
    };
    return new PsiReference[]{psiReference};
  }


}