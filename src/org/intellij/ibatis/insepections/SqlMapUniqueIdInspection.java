package org.intellij.ibatis.insepections;

import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * unique id inspection
 *
 * @author Jacky
 */
public class SqlMapUniqueIdInspection extends SqlMapInspection {
    @Nls @NotNull public String getDisplayName() {
        return "check unique name";
    }

    @NonNls @NotNull public String getShortName() {
        return "ibatis_inspections_unique_id";
    }

    protected void checkSelect(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Select select, DomElementAnnotationHolder holder) {
//        holder.createProblem(select, "unique name required");    @todo
    }
}
