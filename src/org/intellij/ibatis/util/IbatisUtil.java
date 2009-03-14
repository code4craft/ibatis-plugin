package org.intellij.ibatis.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.xml.DomManager;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.intellij.ibatis.facet.IbatisFacet;
import org.intellij.ibatis.facet.IbatisFacetConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     *
     * @param xmlTag xml tag
     * @return SQL in xml tag
     */
    @SuppressWarnings({"ConstantConditions"})
    @NotNull
    public static String getSQLForXmlTag(XmlTag xmlTag) {
        StringBuilder sql = new StringBuilder();
        PsiElement[] children = xmlTag.getChildren();
        for (PsiElement child : children) {
            if (child instanceof XmlTag) {
                XmlTag tag = (XmlTag) child;
                if (tag.getName().equals("include")) {
                    XmlAttribute refid = tag.getAttribute("refid");
                    if (refid != null && StringUtil.isNotEmpty(refid.getText())) {
                        PsiElement psiElement = refid.getValueElement().getReference().resolve();
                        if (psiElement != null && psiElement instanceof XmlTag) {
                            Sql sqlDom = (Sql) DomManager.getDomManager(psiElement.getProject()).getDomElement((XmlTag) psiElement);
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

    /**
     * tools just like unix's grep
     *
     * @param input   input string
     * @param pattern regex pattern
     * @return scanned  string list
     */
    public static List<String> grep(String input, String pattern) {
        List<String> matchedResults = new ArrayList<String>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        while (m.find()) {
            matchedResults.add(m.group());
        }
        return matchedResults;
    }

    /**
     * get the configuration for iBATIS
     *
     * @param elements element list
     * @return iBATIS configuration
     */
    @Nullable
    public static IbatisFacetConfiguration getConfig(PsiElement... elements) {
        if (null == elements || elements.length == 0) {
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

    /**
     * evaluate velocity template
     *
     * @param context  context
     * @param template template content
     * @return output
     * @throws Exception exception
     */
    public static String evaluateVelocityTemplate(VelocityContext context, String template) throws Exception {
        StringWriter sw = new StringWriter();
        getVelocityEngine().evaluate(context, sw, "iBATIS Plugin", template);
        return sw.toString();
    }

    /**
     * get the velocity engine
     *
     * @return engine object
     * @throws Exception exception
     */
    public static VelocityEngine getVelocityEngine() throws Exception {
        VelocityEngine engine = new VelocityEngine();
        engine.init();
        return engine;
    }

    /**
     * convert to underscore string, just like personId to person_id
     *
     * @param name string
     * @return converted string
     */
    public static String convertToUnderscore(String name) {
        StringBuilder builder = new StringBuilder();
        char[] characters = name.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            char character = characters[i];
            String temp = String.valueOf(character).toLowerCase();
            if (character >= 'A' && character <= 'Z' && i > 0 ) {
                builder.append("_");
            }
            builder.append(temp);
        }
        return builder.toString();
    }

    /**
     * convert to  underscore to capital style
     *
     * @param name name
     * @return converted string
     */
    public static String convertToCapital(String name) {
        StringBuilder builder = new StringBuilder();
        String[] parts = name.split("_");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i > 0) {
                builder.append(StringUtil.capitalize(part));
            } else {
                builder.append(StringUtil.decapitalize(part));
            }
        }
        return builder.toString();
    }
}
