package org.intellij.ibatis.inspections;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.util.IbatisBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * semicolon end inspection
 */
public class SemicolonEndInspection extends SqlMapInspection {
    @Nls
    @NotNull
    public String getDisplayName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.semicolonend.name");
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.semicolonend.id");
    }


    protected void checkSelect(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Select select, DomElementAnnotationHolder holder) {
        checkSemicolonEnded(sqlMapModel, sqlMap, select, holder, select.getParameterClass().getValue());
    }

    /**
     * check insert
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param insert      current select
     * @param holder      dom element annotation holder
     */
    protected void checkInsert(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Insert insert, DomElementAnnotationHolder holder) {
        checkSemicolonEnded(sqlMapModel, sqlMap, insert, holder, insert.getParameterClass().getValue());
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
        checkSemicolonEnded(sqlMapModel, sqlMap, update, holder, update.getParameterClass().getValue());
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
        checkSemicolonEnded(sqlMapModel, sqlMap, delete, holder, delete.getParameterClass().getValue());
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
        checkSemicolonEnded(sqlMapModel, sqlMap, statement, holder, statement.getParameterClass().getValue());
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
        checkSemicolonEnded(sqlMapModel, sqlMap, procedure, holder, procedure.getParameterClass().getValue());
    }

    protected void checkSemicolonEnded(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, DomElement domElement, DomElementAnnotationHolder holder, PsiClass parameterClass) {
        String sqlCode = SymbolInSQLInspection.getAllTextInTag(domElement.getXmlTag());
        if (sqlCode.trim().endsWith(";")) {
            holder.createProblem(domElement, HighlightSeverity.ERROR, IbatisBundle.message("ibatis.sqlmap.inspection.semicolonend.error"));
        }
    }

}