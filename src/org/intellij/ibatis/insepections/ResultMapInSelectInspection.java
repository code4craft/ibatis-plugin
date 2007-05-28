package org.intellij.ibatis.insepections;

import Zql.ZQuery;
import Zql.ZSelectItem;
import Zql.ZStatement;
import Zql.ZqlParser;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * result map in select inspection
 */
public class ResultMapInSelectInspection extends SqlMapInspection {
    @Nls @NotNull public String getDisplayName() {
        return "resultmap in select";
    }

    @NonNls @NotNull public String getShortName() {
        return "resultmap in select";
    }

    protected void checkSelect(IbatisSqlMapModel sqlMapModel, SqlMap sqlMap, Select select, DomElementAnnotationHolder holder) {
        ResultMap resultMap = select.getReferencedResultMap();
        if (resultMap != null) {
            String sql = select.getValue() + ";";
            sql = sql.replaceAll("\\#[\\w\\.]*\\#", "''");
            sql = sql.replaceAll("\\$[\\w\\.]*\\$", "temp1");
            try {
                Map<String, String> allSelectItems = new HashMap<String, String>();
                ZqlParser parser = new ZqlParser(new ByteArrayInputStream(sql.getBytes()));
                ZStatement statement = parser.readStatement();
                if (statement != null && statement instanceof ZQuery) {
                    ZQuery query = (ZQuery) statement;
                    Vector selectedItems = query.getSelect();
                    for (Object selectedItem : selectedItems) {
                        if (selectedItem instanceof ZSelectItem) {
                            ZSelectItem zSelectItem = (ZSelectItem) selectedItem;
                            String alias = zSelectItem.getAlias();
                            if (alias == null) alias = zSelectItem.getColumn();
                            if (alias.equals("*")) return;
                            allSelectItems.put(alias.toUpperCase(), zSelectItem.getColumn());
                        }
                    }
                }
                for (Result result : resultMap.getResults()) {
                    String columnName = result.getColumn().getValue();
                    if (columnName == null) columnName = result.getProperty().getValue();
                    if (columnName != null) {
                        if (allSelectItems.get(columnName.toUpperCase()) == null) {
                            holder.createProblem(select, HighlightSeverity.WARNING, columnName + " column absent in SQL sentence for result map");
                            break;
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
