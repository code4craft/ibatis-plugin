package org.intellij.ibatis.dom.abator;

import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.converters.abator.TableNameConverter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * table element
 */
public interface Table extends CommonDomModelElement {
    @NotNull
    @Convert(TableNameConverter.class)
    @Attribute("tableName")
    public GenericAttributeValue<DatabaseTableData> getTableName();

    @SubTagList("columnOverride")
    public List<ColumnOverride> getColumnOverrides();

    @SubTag("generatedKey")
    public GeneratedKey getGeneratedKey();

    @SubTagList("ignoreColumn")
    public List<IgnoreColumn> getIgnoreColumns();
}
