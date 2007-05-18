package org.intellij.ibatis.dom.sqlMap.impl;

import com.intellij.javaee.model.xml.impl.BaseImpl;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import org.intellij.ibatis.dom.sqlMap.ParameterMap;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;

/**
 * paramterMap in sql map file
 */
public abstract class ParameterMapImpl extends BaseImpl implements ParameterMap {
    public PsiClass getPsiClass() {
        String classname = getClazz().getValue();
        if (StringUtil.isNotEmpty(classname)) {
            IbatisClassShortcutsReferenceProvider.getPsiClass(getClazz().getXmlAttribute(), classname);
        }
        return null;
    }
}
