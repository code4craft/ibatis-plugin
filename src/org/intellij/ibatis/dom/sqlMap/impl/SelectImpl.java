package org.intellij.ibatis.dom.sqlMap.impl;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;

/**
 * select element implementatioin.
 */
public abstract class SelectImpl extends SentenceBaseImpl implements Select {
    public ResultMap getReferencedResultMap() {
        String resultMapName = getResultMap().getValue();
        if (StringUtil.isNotEmpty(resultMapName)) {
            XmlTag tag = IbatisManager.getInstance().getAllResultMap2(getXmlElement()).get(resultMapName);
            DomElement element = getManager().getDomElement(tag);
            if (element instanceof ResultMap) {
                return (ResultMap) element;
            }
        }
        return null;
    }

    public PsiClass getResultClazz() {
        String resultClassName = getResultClass().getValue();
        if (StringUtil.isNotEmpty(resultClassName)) {
            return IbatisClassShortcutsReferenceProvider.getPsiClass(getXmlElement(), resultClassName);
        }
        return null;
    }

}
