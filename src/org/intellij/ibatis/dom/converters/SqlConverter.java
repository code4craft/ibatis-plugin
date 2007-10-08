package org.intellij.ibatis.dom.converters;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * convert for sql reference
 *
 * @author Jacky
 */
public class SqlConverter extends Converter<Sql> {
    @Nullable public Sql fromString(@Nullable @NonNls String sqlName, ConvertContext convertContext) {
        if (StringUtil.isNotEmpty(sqlName)) {
            Map<String, Sql> map = IbatisManager.getInstance().getAllSql(convertContext.getXmlElement());
            return map.get(sqlName);
        } else return null;
    }

    public String toString(@Nullable Sql sql, ConvertContext convertContext) {
        return sql != null ? sql.getId().getValue() : "";
    }
}
