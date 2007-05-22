package org.intellij.ibatis.insepections;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisProjectComponent;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * sql map base isnpection
 */
public abstract class SqlMapInspection extends BasicDomElementsInspection<SqlMap> {
    public SqlMapInspection() {
        super(SqlMap.class);
    }

    @Nls @NotNull public String getGroupDisplayName() {
        return "iBATIS Sql Map Model";
    }

    @SuppressWarnings({"ConstantConditions"})
    public void checkFileElement(DomFileElement<SqlMap> fileElement, DomElementAnnotationHolder holder) {
        Module module = ModuleUtil.findModuleForPsiElement(fileElement.getXmlElement());
        IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(module.getProject());
        List<IbatisSqlMapModel> models = projectComponent.getSqlMapModelFactory().getAllModels(module);
        if (models.size() < 1) return;
        IbatisSqlMapModel model = models.get(0);
        SqlMap sqlMap = fileElement.getRootElement();
        for (Select select : sqlMap.getSelects()) {
            checkSelect(model, sqlMap, select, holder);
        }
        for (Update update : sqlMap.getUpdates()) {
            checkUpdate(model, sqlMap, update, holder);
        }
        for (Delete delete : sqlMap.getDeletes()) {
            checkDelete(model, sqlMap, delete, holder);
        }
        for (Insert insert : sqlMap.getInserts()) {
            checkInsert(model, sqlMap, insert, holder);
        }
        for (Procedure procedure : sqlMap.getProcedures()) {
            checkProcedure(model, sqlMap, procedure, holder);
        }
        for (Statement statement : sqlMap.getStatements()) {
            checkStatement(model, sqlMap, statement, holder);
        }
        for (ResultMap resultMap : sqlMap.getResultMaps()) {
            checkResultMap(model, sqlMap, resultMap, holder);
        }
    }

    /**
     * check select bean
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param select      current select
     * @param holder      domelement annotation holder
     */
    protected void checkResultMap(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, ResultMap resultMap, final DomElementAnnotationHolder holder) {
    }

    /**
     * check select bean
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param select      current select
     * @param holder      domelement annotation holder
     */
    protected void checkSelect(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Select select, final DomElementAnnotationHolder holder) {
    }

    /**
     * check update
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param update      current select
     * @param holder      domelement annotation holder
     */
    protected void checkUpdate(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Update update, final DomElementAnnotationHolder holder) {
    }


    /**
     * check delete
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param delete      current select
     * @param holder      domelement annotation holder
     */
    protected void checkDelete(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Delete delete, final DomElementAnnotationHolder holder) {
    }


    /**
     * check insert
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param insert      current select
     * @param holder      domelement annotation holder
     */
    protected void checkInsert(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Insert insert, final DomElementAnnotationHolder holder) {
    }

    /**
     * check statement
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param statement   current select
     * @param holder      domelement annotation holder
     */
    protected void checkStatement(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Statement statement, final DomElementAnnotationHolder holder) {
    }

    /**
     * check procedure
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param procedure   current select
     * @param holder      domelement annotation holder
     */
    protected void checkProcedure(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Procedure procedure, final DomElementAnnotationHolder holder) {
    }
}
