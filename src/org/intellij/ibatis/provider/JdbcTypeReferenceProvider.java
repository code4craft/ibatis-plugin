package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * jdbcType reference provider
 */
public class JdbcTypeReferenceProvider extends BaseReferenceProvider {
    private static List<String> jdbcTypeList = new ArrayList<String>();

    static {
        jdbcTypeList.add("VARCHAR");
        jdbcTypeList.add("NUMBER");
        jdbcTypeList.add("DATE");
    }

    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        return new PsiReference[]{new XmlAttributeValuePsiReference((XmlAttributeValue) psiElement) {
            public Object[] getVariants() {
                return jdbcTypeList.toArray();
            }
        }};
    }
}
