package org.intellij.ibatis.inspections;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.*;
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

    /**
     * check insert
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param insert      current select
     * @param holder      domelement annotation holder
     */
    protected void checkInsert(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Insert insert, DomElementAnnotationHolder holder) {
        List<String> nameList = SqlMapSymbolCompletionData.getAllSymbolsInXmlTag(insert.getXmlTag());
        checkSymbol(sqlMapModel, sqlMap, insert, holder, nameList);
    }


    /**
     * check update
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param update      current select
     * @param holder      domelement annotation holder
     */
    protected void checkUpdate(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Update update, DomElementAnnotationHolder holder) {
        List<String> nameList = SqlMapSymbolCompletionData.getAllSymbolsInXmlTag(update.getXmlTag());
        checkSymbol(sqlMapModel, sqlMap, update, holder, nameList);
    }

    /**
     * check delete
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param delete      current select
     * @param holder      domelement annotation holder
     */
    protected void checkDelete(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Delete delete, DomElementAnnotationHolder holder) {
        List<String> nameList = SqlMapSymbolCompletionData.getAllSymbolsInXmlTag(delete.getXmlTag());
        checkSymbol(sqlMapModel, sqlMap, delete, holder, nameList);
    }

    /**
     * check statement
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param statement   current select
     * @param holder      domelement annotation holder
     */
    protected void checkStatement(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Statement statement, DomElementAnnotationHolder holder) {
        List<String> nameList = SqlMapSymbolCompletionData.getAllSymbolsInXmlTag(statement.getXmlTag());
        checkSymbol(sqlMapModel, sqlMap, statement, holder, nameList);
    }

    /**
     * check procedure
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param procedure   current select
     * @param holder      domelement annotation holder
     */
    protected void checkProcedure(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Procedure procedure, DomElementAnnotationHolder holder) {
        List<String> nameList = SqlMapSymbolCompletionData.getAllSymbolsInXmlTag(procedure.getXmlTag());
        checkSymbol(sqlMapModel, sqlMap, procedure, holder, nameList);
    }

    protected void checkSymbol(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, DomElement domElement, DomElementAnnotationHolder holder, List<String> names) {
        if (names.size() == 0) return;  //map parameter class
        if (names.size() == 1 && names.get(0).equals("value")) return; // internal map
        String[] words = getAllTextInTag(domElement.getXmlTag()).trim().split("\\s+");
        if (words != null) {
            for (String word : words) {
                if (word.startsWith("#") && word.endsWith("#"))  // symbol
                {
                    String parameterName = word.replaceAll("#", "");
                   if(parameterName.contains(":")) {   //parameter:jdbctype:value
                       parameterName = parameterName.substring(0, parameterName.indexOf(":"));
                   }
                    if (!names.contains(parameterName)) {
                        holder.createProblem(domElement, HighlightSeverity.WARNING, IbatisBundle.message("ibatis.sqlmap.inspection.symbolinsql.error", parameterName));
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
    public static String getAllTextInTag(XmlTag xmlTag) {
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