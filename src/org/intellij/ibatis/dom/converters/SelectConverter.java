package org.intellij.ibatis.dom.converters;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.provider.XmlAttributeValuePsiReference;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * select statement converter
 */
public class SelectConverter extends Converter<Select> implements CustomReferenceConverter<Select> {
    @Nullable public Select fromString(@Nullable @NonNls String selectName, ConvertContext convertContext) {
        if (StringUtil.isNotEmpty(selectName)) {
            Map<String, Select> allSelect = IbatisManager.getInstance().getAllSelect(convertContext.getReferenceXmlElement());
            return allSelect.get(selectName);
        }
        return null;
    }

    public String toString(@Nullable Select select, ConvertContext convertContext) {
        return select != null ? select.getId().getValue() : "";
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
                return select == null ? null : select.getXmlTag();
            }
        }};
    }
}
