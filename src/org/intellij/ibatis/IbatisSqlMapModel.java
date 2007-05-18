package org.intellij.ibatis;

import com.intellij.util.xml.model.DomModel;
import com.intellij.psi.PsiClass;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * IBATIS sql map model
 */
public interface IbatisSqlMapModel extends DomModel<SqlMap> {

    @NotNull
    public Map<String, PsiClass> getTypeAlias();

}