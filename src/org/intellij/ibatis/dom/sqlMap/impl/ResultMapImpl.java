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
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * resultMap element implementation
 */
public abstract class ResultMapImpl extends BaseImpl implements ResultMap {

    /**
     * get all results included extended result
     *
     * @return Result List
     */
    @NotNull public List<Result> getAllResults() {
        List<Result> results = getResults();
        ResultMap extendedMap = getExtends().getValue();
        if (extendedMap != null) {
            results.addAll(extendedMap.getAllResults());
        }
        return results;
    }
}
