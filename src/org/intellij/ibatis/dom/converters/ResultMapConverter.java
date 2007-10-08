package org.intellij.ibatis.dom.converters;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * converter for result map reference
 *
 * @author Jacky.
 */
public class ResultMapConverter extends Converter<ResultMap> {
    @SuppressWarnings({"ConstantConditions"}) @Nullable
    public ResultMap fromString(@Nullable @NonNls String resultMapName, ConvertContext convertContext) {
        if (StringUtil.isNotEmpty(resultMapName)) {
            Map<String, XmlTag> xmlTagMap = IbatisManager.getInstance().getAllResultMap2(convertContext.getXmlElement());
            XmlTag tag = xmlTagMap.get(resultMapName);
            if (tag != null) {
                return (ResultMap) DomManager.getDomManager(convertContext.getXmlElement().getProject()).getDomElement(tag);
            }
        }
        return null;
    }

    public String toString(@Nullable ResultMap resultMap, ConvertContext convertContext) {
        return resultMap != null ? resultMap.getId().getValue() : "";
    }
}
