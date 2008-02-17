package org.intellij.ibatis.inspections;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.intellij.ibatis.util.IbatisBundle;
import org.jetbrains.annotations.NotNull;

/**
 * inspection for select without result class or map absent
 *
 * @author linux_china@hotmail.com
 */
public class SelectResultClassAbsentInspection extends SqlMapInspection {
    @NotNull
    public String getDisplayName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.selectresultabsent.name");
    }

    @NotNull
    public String getShortName() {
        return IbatisBundle.message("ibatis.sqlmap.inspection.selectresultabsent.id");
    }

    /**
     * check select  to validate result map or class
     *
     * @param sqlMapModel sqlMapModel
     * @param sqlMap      current sqlMap
     * @param select      current select
     * @param holder      dom element annotation holder
     */
    @Override
    protected void checkSelect(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Select select, DomElementAnnotationHolder holder) {
        if (select.getResultClass().getValue() == null && select.getResultMap().getValue() == null) {
            holder.createProblem(select, HighlightSeverity.WARNING, IbatisBundle.message("ibatis.sqlmap.inspection.selectresultabsent.error"));
        }
    }
}
