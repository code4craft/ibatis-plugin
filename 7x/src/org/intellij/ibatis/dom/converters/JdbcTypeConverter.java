package org.intellij.ibatis.dom.converters;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import org.intellij.ibatis.model.JdbcType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * converter for jdbc type reference
 *
 * @author Jacky
 */
public class JdbcTypeConverter extends Converter<JdbcType> {

    @Nullable public JdbcType fromString(@Nullable @NonNls String jdbcTypeName, ConvertContext convertContext) {
        return StringUtil.isNotEmpty(jdbcTypeName) ? new JdbcType(jdbcTypeName) : null;
    }

    public String toString(@Nullable JdbcType jdbcType, ConvertContext convertContext) {
        return jdbcType != null ? jdbcType.getName() : "";
    }
}
