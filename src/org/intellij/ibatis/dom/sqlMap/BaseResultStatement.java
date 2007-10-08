package org.intellij.ibatis.dom.sqlMap;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.converters.IbatisClassConverter;
import org.intellij.ibatis.dom.converters.ResultMapConverter;

/**
 * base result statement, such select, statement, procedure.
 */
public interface BaseResultStatement extends BaseStatement {
    /**
     * get result class for statement
     *
     * @return result class
     */
    @Convert(IbatisClassConverter.class)
    @Attribute("resultClass")
    public GenericAttributeValue<PsiClass> getResultClass();

    /**
     * get result map for statement
     *
     * @return result map
     */
    @Attribute("resultMap")
    @Convert(ResultMapConverter.class)
    public GenericAttributeValue<ResultMap> getResultMap();
}
