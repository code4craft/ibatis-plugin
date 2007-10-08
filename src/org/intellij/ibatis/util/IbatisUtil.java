package org.intellij.ibatis.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.jetbrains.annotations.NotNull;

/**
 * utility class in iBATIS plug-in
 */
public class IbatisUtil {
    /**
     * get the table name with schema
     *
     * @param tableName table name
     * @return clear table name
     */
    public static String getTableNameWithoutSchema(String tableName) {
        return tableName.indexOf('.') != -1 ? tableName.substring(tableName.indexOf('.') + 1) : tableName;
    }

    /**
     * get the SQL code in xml tag
     * @param xmlTag xml tag
     * @return SQL in xml tag
     */
    @SuppressWarnings({"ConstantConditions"}) @NotNull public static String getSQLForXmlTag(XmlTag xmlTag) {
        StringBuilder sql = new StringBuilder();
        PsiElement[] children = xmlTag.getChildren();
        for (PsiElement child : children) {
            if (child instanceof XmlTag) {
                XmlTag tag = (XmlTag) child;
                if (tag.getName().equals("include")) {
                    XmlAttribute refid = tag.getAttribute("refid");
                    if (refid != null && StringUtil.isNotEmpty(refid.getText())) {
                        PsiElement psiElement = refid.getValueElement().getReference().resolve();
                        if (psiElement != null && psiElement instanceof XmlAttribute) {
                            XmlAttribute idAttribute = (XmlAttribute) psiElement;
                            Sql sqlDom = (Sql) DomManager.getDomManager(psiElement.getProject()).getDomElement(idAttribute.getParent());
                            if (sqlDom != null)
                                sql.append(" ").append(sqlDom.getSQL());
                        }
                    }
                }
            } else if (child instanceof XmlText) {
                sql.append(" ").append(((XmlText) child).getValue());
            }
        }
        return sql.toString();
    }
}
