package org.intellij.ibatis.dom.sqlMap.impl;

import com.intellij.psi.xml.XmlTag;
import org.intellij.ibatis.dom.sqlMap.BaseStatement;
import org.intellij.ibatis.model.InlineParameter;
import org.intellij.ibatis.util.IbatisConstants;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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
        return new ArrayList<InlineParameter>();
    }

    /**
     * get icon flag
     *
     * @param flag falg
     * @return flag
     */
    public Icon getIcon(int flag) {
        XmlTag tag = getXmlTag();
        if (tag != null) {
            String tagName = tag.getName();
            if (tagName.equals("select")) {
                return IbatisConstants.SQLMAP_SELECT;
            } else if (tagName.equals("delete")) {
                return IbatisConstants.SQLMAP_DELETE;
            } else if (tagName.equals("update")) {
                return IbatisConstants.SQLMAP_UPDATE;
            } else if (tagName.equals("statement")) {
                return IbatisConstants.SQLMAP_STATEMENT;
            } else if (tagName.equals("procedure")) {
                return IbatisConstants.SQLMAP_PROCEDURE;
            } else if (tagName.equals("insert")) {
                return IbatisConstants.SQLMAP_INSERT;
            }
        }
        return null;
    }
}
