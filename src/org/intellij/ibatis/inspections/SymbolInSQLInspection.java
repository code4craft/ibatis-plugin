package org.intellij.ibatis.inspections;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.provider.*;
import org.intellij.ibatis.util.IbatisBundle;
import org.jetbrains.annotations.*;

import java.util.*;

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
        checkSymbol(sqlMapModel, sqlMap, select, holder, select.getParameterClass().getValue());
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
        checkSymbol(sqlMapModel, sqlMap, insert, holder, insert.getParameterClass().getValue());
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
        checkSymbol(sqlMapModel, sqlMap, update, holder, update.getParameterClass().getValue());
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
        checkSymbol(sqlMapModel, sqlMap, delete, holder, delete.getParameterClass().getValue());
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
        checkSymbol(sqlMapModel, sqlMap, statement, holder, statement.getParameterClass().getValue());
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
        checkSymbol(sqlMapModel, sqlMap, procedure, holder, procedure.getParameterClass().getValue());
    }

    protected void checkSymbol(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, DomElement domElement, DomElementAnnotationHolder holder, PsiClass parameterClass) {
        if (parameterClass == null) return;
        Set<String> inlineParameters = new HashSet<String>();
        String[] words = getAllTextInTag(domElement.getXmlTag()).trim().split("\\s+");
        if (words != null) {
            for (String word : words) {
                if (word.startsWith("#") && word.endsWith("#"))  // symbol
                {
                    String parameterName = word.replaceAll("#", "");
                    if (parameterName.contains(":")) {   //parameter:jdbctype:value
                        parameterName = parameterName.substring(0, parameterName.indexOf(":"));
                    }
                    inlineParameters.add(parameterName);
                }
            }
        }
        //domain class validation
        if (IbatisClassShortcutsReferenceProvider.isDomain(parameterClass.getName())) {
            for (String inlineParameter : inlineParameters) {
                if (!isFieldOfPsiClass(parameterClass, inlineParameter.split("\\."))) { //not a valid field for domain class
                    holder.createProblem(domElement, HighlightSeverity.WARNING, IbatisBundle.message("ibatis.sqlmap.inspection.symbolinsql.error", inlineParameter));
                }
            }
        }
    }

    /**
     * validate the psi class contains sub field
     *
     * @param psiClass base class
     * @param path     path info
     * @return contained mark
     */
    private boolean isFieldOfPsiClass(PsiClass psiClass, String[] path) {
        PsiClass referenceClass = psiClass;
        for (String item : path) {
            referenceClass = FieldAccessMethodReferenceProvider.findGetterMethodReturnType(referenceClass, "get" + StringUtil.capitalize(item));
            if (referenceClass == null) return false;
        }
        return true;
    }

    /**
     * get all text in xml tag including sub tags
     *
     * @param xmlTag xml tag
     * @return xml text
     */
    @SuppressWarnings({"ConstantConditions"})
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
                            sql.append(" ").append(idAttribute.getParent().getValue().getText());
                        }
                    }
                } else {
                    sql.append(getAllTextInTag(tag));
                }
            } else if (child instanceof XmlText) {
                sql.append(" ").append(((XmlText) child).getValue());
            }
        }
        return sql.toString();
    }

    /**
     * get all parameter in xml tag
     *
     * @param xmlTag xml tag
     * @return parameter name list
     */
    public static Set<String> getAllParameterInTag(XmlTag xmlTag) {
        Set<String> inlineParameters = new HashSet<String>();
        String[] words = getAllTextInTag(xmlTag).trim().split("\\s+");
        if (words != null) {
            for (String word : words) {
                if (word.startsWith("#") && word.endsWith("#"))  // symbol
                {
                    String parameterName = word.replaceAll("#", "");
                    if (parameterName.contains(":")) {   //parameter:jdbctype:value
                        parameterName = parameterName.substring(0, parameterName.indexOf(":"));
                    }
                    inlineParameters.add(parameterName);
                }
            }
        }
        return inlineParameters;
    }
}