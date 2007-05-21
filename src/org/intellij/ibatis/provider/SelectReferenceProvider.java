package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * select sentence reference provider
 */
public class SelectReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
            public boolean isSoft() {
                return false;
            }

            @Nullable public PsiElement resolve() {
                String resultMapId = getCanonicalText();
                Map<String, Select> allResultMap = IbatisManager.getInstance().getAllSelect(getElement());
                Select select = allResultMap.get(resultMapId);
                return select == null ? null : select.getId().getXmlAttribute();
            }

            public Object[] getVariants() {
                List<Object> variants = new ArrayList<Object>();
                Set<String> selectList = IbatisManager.getInstance().getAllSelect(getElement()).keySet();
                for (String select : selectList) {
                    variants.add(LookupValueFactory.createLookupValue(select, IbatisConstants.SQLMAP_SELECT));
                }
                return variants.toArray();
            }
        };
        return new PsiReference[]{psiReference};
    }

}