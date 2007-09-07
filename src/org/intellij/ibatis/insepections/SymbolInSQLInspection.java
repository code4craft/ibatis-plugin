package org.intellij.ibatis.insepections;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.intellij.ibatis.provider.SqlMapSymbolCompletionData;
import org.intellij.ibatis.util.IbatisBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * symbol in sql inspection
 */
public class SymbolInSQLInspection extends SqlMapInspection {
    @Nls @NotNull public String getDisplayName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.symbolinsql.name");
    }

    @NonNls @NotNull public String getShortName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.symbolinsql.id");
    }

    protected void checkSelect(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Select select, DomElementAnnotationHolder holder) {
        List<String> nameList = SqlMapSymbolCompletionData.getAllSymbolsInXmlTag(select.getXmlTag());
        checkSymbol(sqlMapModel, sqlMap, select, holder, nameList);
    }


    protected void checkSymbol(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, DomElement domElement, DomElementAnnotationHolder holder, List<String> names) {
        String[] words = getAllTextInTag(domElement.getXmlTag()).trim().split("\\s+");
        if (words != null) {
            for (String word : words) {
                if (word.startsWith("#") && word.endsWith("#"))  // symbol
                {
                    if (!names.contains(word.replaceAll("#", ""))) {
                        holder.createProblem(domElement, HighlightSeverity.WARNING, IbatisBundle.message("ibatis.sqlmap.inspection.symbolinsql.error", word.replace("#", "")));
                    }
                }
            }
        }
    }

    /**
     * get all text in xml tag including sub tags
     *
     * @param xmlTag xml tag
     * @return xml text
     */
    public String getAllTextInTag(XmlTag xmlTag) {
        StringBuilder sql = new StringBuilder();
        PsiElement[] children = xmlTag.getChildren();
        for (PsiElement child : children) {
            if (child instanceof XmlTag) {
                XmlTag tag = (XmlTag) child;
                if (tag.getName().equals("include")) {   // include element
                    XmlAttribute refid = tag.getAttribute("refid");
                    if (refid != null && StringUtil.isNotEmpty(refid.getText())) {
                        PsiElement psiElement = refid.getValueElement().getReference().resolve();
                        if (psiElement instanceof XmlAttribute) {
                            XmlAttribute idAttribute = (XmlAttribute) psiElement;
                            sql.append(" " + idAttribute.getParent().getValue().getText());
                        }
                    }
                } else {
                    sql.append(getAllTextInTag(tag));
                }
            } else if (child instanceof XmlText) {
                sql.append(" " + ((XmlText) child).getValue());
            }
        }
        return sql.toString();
    }
}