package org.intellij.ibatis.dom.sqlMap.impl;

import com.intellij.javaee.model.xml.impl.BaseImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.NotNull;

/**
 * SQL element implementation
 */
public abstract class SqlImpl extends BaseImpl implements Sql {

    /**
     * get the SQL code
     *
     * @return SQL sentence
     */
    @NotNull public String getSQL() {
        return IbatisUtil.getSQLForXmlTag(getXmlTag());
    }
}
