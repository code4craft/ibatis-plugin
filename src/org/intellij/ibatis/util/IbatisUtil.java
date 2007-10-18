package org.intellij.ibatis.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.intellij.ibatis.facet.IbatisFacetConfiguration;
import org.intellij.ibatis.facet.IbatisFacet;
import org.jetbrains.annotations.NotNull;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;

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

	public static IbatisFacetConfiguration getConfig(PsiElement... elements){
		if(null == elements || elements.length == 0){
			return null;
		}

		for (PsiElement e : elements) {
			if (null != e) {
				Module module = ModuleUtil.findModuleForPsiElement(e);
				if (null != module) {
					IbatisFacet facet = IbatisFacet.getInstance(module);
					if (facet != null) {
						return facet.getConfiguration();
					}
				}
			}
		}
		return null;
	}

	public static String evaluateVelocityTemplate(
		VelocityContext context,
		String template
	) throws Exception {
		StringWriter sw = new StringWriter();
		getVelocityEngine().evaluate(context, sw, "iBATIS Plugin", template);
		return sw.toString();
	}

	public static VelocityEngine getVelocityEngine() throws Exception {
		VelocityEngine engine = new VelocityEngine();
		engine.init();
		return engine;
	}
}
