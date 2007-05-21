package org.intellij.ibatis.dom.converters;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.provider.XmlAttributeValuePsiReference;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * select statement converter
 */
public class SelectConverter extends Converter<Select> implements CustomReferenceConverter<Select> {
    public Select fromString(@Nullable @NonNls String s, ConvertContext convertContext) {
        Map<String, Select> allSelect = IbatisManager.getInstance().getAllSelect(convertContext.getReferenceXmlElement());
        return allSelect.get(s);
    }

    public String toString(@Nullable Select select, ConvertContext convertContext) {
        return null;
    }

    @NotNull
    public PsiReference[] createReferences(GenericDomValue<Select> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        return new PsiReference[]{new XmlAttributeValuePsiReference((XmlAttributeValue) convertContext.getReferenceXmlElement()) {
            public boolean isSoft() {
                return false;
            }

            public Object[] getVariants() {
                List<Object> variants = new ArrayList<Object>();
                Set<String> selectList = IbatisManager.getInstance().getAllSelect(getElement()).keySet();
                for (String select : selectList) {
                    variants.add(LookupValueFactory.createLookupValue(select, IbatisConstants.SQLMAP_SELECT));
                }
                return variants.toArray();
            }

            @Nullable public PsiElement resolve() {
                String resultMapId = getCanonicalText();
                Map<String, Select> allResultMap = IbatisManager.getInstance().getAllSelect(getElement());
                Select select = allResultMap.get(resultMapId);
                return select == null ? null : select.getId().getXmlAttribute();
            }
        }};
    }
}
