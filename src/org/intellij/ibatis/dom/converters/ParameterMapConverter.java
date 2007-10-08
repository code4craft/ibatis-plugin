package org.intellij.ibatis.dom.converters;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.ParameterMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * converter for parameter map reference
 *
 * @author Jacky
 */
public class ParameterMapConverter extends Converter<ParameterMap> {
    @SuppressWarnings({"ConstantConditions"}) @Nullable
    public ParameterMap fromString(@Nullable @NonNls String parameterMapName, ConvertContext convertContext) {
        if (StringUtil.isNotEmpty(parameterMapName)) {
            Map<String, XmlTag> allParameterMap2 = IbatisManager.getInstance().getAllParameterMap2(convertContext.getXmlElement());
            XmlTag tag = allParameterMap2.get(parameterMapName);
            if (tag != null) {
                return (ParameterMap) DomManager.getDomManager(convertContext.getXmlElement().getProject()).getDomElement(tag);
            }
        }
        return null;
    }

    public String toString(@Nullable ParameterMap parameterMap, ConvertContext convertContext) {
        return parameterMap != null ? parameterMap.getId().getValue() : "";
    }
}
