package org.intellij.ibatis.inspections;

import org.jetbrains.annotations.*;
import org.intellij.ibatis.util.IbatisBundle;
import org.intellij.ibatis.util.IbatisUtil;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.DomElement;
import com.intellij.lang.annotation.HighlightSeverity;

/**
 * inspection for parameter map used in statement
 *
 * @author Jacky
 */
public class ParameterMapInStatementInspection extends SqlMapInspection {
    @Nls @NotNull
    public String getDisplayName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.parametermapinstatement.name");
    }

    @NonNls @NotNull
    public String getShortName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.parametermapinstatement.id");
    }

    /**
     * check select bean
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param select      current select
     * @param holder      dom element annotation holder
     */
    protected void checkSelect(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Select select, DomElementAnnotationHolder holder) {
        checkParameterMap(sqlMapModel, sqlMap, select, holder, select.getParameterMap().getValue());
    }

    /**
     * check update
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param update      current select
     * @param holder      dom element annotation holder
     */
    protected void checkUpdate(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Update update, DomElementAnnotationHolder holder) {
        checkParameterMap(sqlMapModel, sqlMap, update, holder, update.getParameterMap().getValue());
    }

    /**
     * check delete
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param delete      current select
     * @param holder      dom element annotation holder
     */
    protected void checkDelete(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Delete delete, DomElementAnnotationHolder holder) {
        checkParameterMap(sqlMapModel, sqlMap, delete, holder, delete.getParameterMap().getValue());
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
        checkParameterMap(sqlMapModel, sqlMap, insert, holder, insert.getParameterMap().getValue());
    }

    /**
     * check statement
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param statement   current select
     * @param holder      dom element annotation holder
     */
    protected void checkStatement(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Statement statement, DomElementAnnotationHolder holder) {
        checkParameterMap(sqlMapModel, sqlMap, statement, holder, statement.getParameterMap().getValue());
    }

    /**
     * check procedure
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param procedure   current select
     * @param holder      dom element annotation holder
     */
    protected void checkProcedure(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Procedure procedure, DomElementAnnotationHolder holder) {
        super.checkProcedure(sqlMapModel, sqlMap, procedure, holder);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void checkParameterMap(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, DomElement domElement, DomElementAnnotationHolder holder, ParameterMap parameterMap) {
        if (parameterMap == null) return;
        String sql = IbatisUtil.getSQLForXmlTag(domElement.getXmlTag());
        int count = (sql + " ").split("\\?").length - 1;
        int count2 = parameterMap.getParameters().size();
        if (count != count2) {
            holder.createProblem(domElement, HighlightSeverity.WARNING, IbatisBundle.message("ibatis.sqlmap.inspection.parametermapinstatement.error"));
        }
    }
}
