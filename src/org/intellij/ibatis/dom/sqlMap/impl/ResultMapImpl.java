package org.intellij.ibatis.dom.sqlMap.impl;

import com.intellij.javaee.model.xml.impl.BaseImpl;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;

import java.util.List;

/**
 * resultMap element implementation
 */
public abstract class ResultMapImpl extends BaseImpl implements ResultMap {
    public PsiClass getPsiClass() {
        String classname = getClazz().getValue();
        if (StringUtil.isNotEmpty(classname)) {
            return IbatisClassShortcutsReferenceProvider.getPsiClass(getClazz().getXmlAttribute(), classname);
        }
        return null;
    }

    public List<Result> getAllResults() {
        List<Result> results = getResults();
        String extendedResultMapName = getExtends().getValue();
        if (StringUtil.isNotEmpty(extendedResultMapName)) {
            XmlTag tag = IbatisManager.getInstance().getAllResultMap2(getXmlElement()).get(extendedResultMapName);
            if (tag != null) {
                DomElement element = getManager().getDomElement(tag);
                if (element != null && element instanceof ResultMap) {
                    ResultMap parentResultMap = (ResultMap) element;
                    results.addAll(parentResultMap.getResults());
                }
            }
        }
        return results;
    }
}
