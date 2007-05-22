package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * sql reference provider
 */
public class SqlReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
            public boolean isSoft() {
                return false;
            }

            @Nullable public PsiElement resolve() {
//                String sqlId = getCanonicalText();
                String sqlId = getReferenceId(getElement());
                Map<String, Sql> sqlList = IbatisManager.getInstance().getAllSql(getElement());
                Sql sql = sqlList.get(sqlId);
                return sql == null ? null : sql.getXmlTag().getAttribute("id");
            }

            public Object[] getVariants() {
                Map<String, Sql> sqlList = IbatisManager.getInstance().getAllSql(getElement());
                List<String> variants = new ArrayList<String>();
                variants.addAll(sqlList.keySet());
                return variants.toArray();
            }
        };
        return new PsiReference[]{psiReference};
    }

}