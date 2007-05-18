package org.intellij.ibatis.dom.converters;

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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                Map<String, Select> allSelect = IbatisManager.getInstance().getAllSelect(getElement());
                List<String> allSelectNames = new ArrayList<String>();
                for (Select select : allSelect.values()) {
                    allSelectNames.add(select.getId().getValue());
                }
                return allSelectNames.toArray();
            }

            @Nullable public PsiElement resolve() {
                String selectId = getCanonicalText();
                Map<String, Select> allSelect = IbatisManager.getInstance().getAllSelect(getElement());
                for (Select select : allSelect.values()) {
                    if (selectId.equals(select.getId().getValue()))
                        return select.getXmlTag();
                }
                return null;
            }
        }};
    }
}
