package org.intellij.ibatis.dom.sqlMap.impl;

import com.intellij.javaee.model.xml.impl.BaseImpl;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;

/**
 * resultMap element implementation
 */
public abstract class ResultMapImpl extends BaseImpl implements ResultMap {
    public PsiClass getPsiClass() {
        String classname = getClazz().getValue();
        if (StringUtil.isNotEmpty(classname)) {
          return  IbatisClassShortcutsReferenceProvider.getPsiClass(getClazz().getXmlAttribute(), classname);
        }
        return null;
    }
}
