package org.intellij.ibatis.dom.sqlMap.impl;

import com.intellij.javaee.model.xml.impl.BaseImpl;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;

/**
 * sentence base implement in sql map file
 */
public abstract class SentenceBaseImpl extends BaseImpl {

    /**
     * got SQL code in sentence
     *
     * @return  SQL code in sentence
     */
    @SuppressWarnings({ "StringConcatenationInsideStringBufferAppend"}) public String getSQL() {
        StringBuilder sql = new StringBuilder();
        PsiElement[] children = getXmlTag().getChildren();
        for (PsiElement child : children) {
            if (child instanceof XmlTag) {
                XmlTag tag = (XmlTag) child;
                if (tag.getName().equals("include")) {
                    XmlAttribute refid = tag.getAttribute("refid");
                    if (refid != null && StringUtil.isNotEmpty(refid.getText())) {
                        PsiElement psiElement = refid.getValueElement().getReference().resolve();
                        if (psiElement instanceof XmlAttribute) {
                            XmlAttribute idAttribute = (XmlAttribute) psiElement;
                            sql.append(" " + idAttribute.getParent().getValue().getText());
                        }
                    }
                }
            } else if (child instanceof XmlText) {
                sql.append(" " + ((XmlText) child).getValue());
            }
        }
        return sql.toString();
    }
}
