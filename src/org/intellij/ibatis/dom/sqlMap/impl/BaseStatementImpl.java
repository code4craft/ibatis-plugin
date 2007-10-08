package org.intellij.ibatis.dom.sqlMap.impl;

import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.util.IbatisUtil;
import org.intellij.ibatis.model.InlineParameter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * base statement implementation
 */
public abstract class BaseStatementImpl implements BaseStatement {
    /**
     * get SQL code in statement
     *
     * @return SQL sentence
     */
    @NotNull public String getSQL() {
        return IbatisUtil.getSQLForXmlTag(getXmlTag());
    }

    /**
     * get the in line parameter for sentence
     *
     * @return in line parameter list
     */
    @NotNull public List<InlineParameter> getInlineParameters() {
        //todo  parameter
        return new ArrayList<InlineParameter>();
    }
}
