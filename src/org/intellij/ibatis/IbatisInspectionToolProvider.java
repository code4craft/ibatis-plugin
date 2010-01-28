package org.intellij.ibatis;

import com.intellij.codeInspection.InspectionToolProvider;
import org.intellij.ibatis.inspections.*;

/**
 * iBATIS inspection tool provider
 *
 * @author linux_china@hotmail.com
 */
public class IbatisInspectionToolProvider implements InspectionToolProvider {
    /**
     * get all inspection class
     *
     * @return inspection class array
     */
    public Class[] getInspectionClasses() {
        return new Class[]{
                SqlMapFileInConfigurationInspection.class,
                NullSettedToPrimaryTypeInspection.class,
                ResultMapInSelectInspection.class,
                SymbolInSQLInspection.class,
                ParameterMapInStatementInspection.class,
                SelectResultClassAbsentInspection.class,
                SemicolonEndInspection.class};
    }
}
