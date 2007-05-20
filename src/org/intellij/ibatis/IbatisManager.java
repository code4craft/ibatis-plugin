package org.intellij.ibatis;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.intellij.ibatis.dom.sqlMap.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class IbatisManager {

    public IbatisManager() {
    }

    public static IbatisManager getInstance() {
        return ServiceManager.getService(IbatisManager.class);
    }

    /**
     * get iBATIS configuration model
     *
     * @param module module
     * @return IbatisConfigurationModel object
     */
    @Nullable
    public abstract IbatisConfigurationModel getConfigurationModel(@NotNull Module module);

    /**
     * get ibatis sql map model according to psiElement
     *
     * @param psiElement psiElement in sql map file
     * @return IbatisSqlMapModel object
     */
    @Nullable
    public abstract IbatisSqlMapModel getSqlMapModel(@Nullable PsiElement psiElement);

    /**
     * get all type alias in iBATIS
     *
     * @param psiElement current psiElement
     * @return type alias map
     */
    public abstract Map<String, PsiClass> getAllTypeAlias(PsiElement psiElement);

    /**
     * get all result map
     *
     * @param psiElement PsiElement
     * @return resultMap map
     */
    public abstract Map<String, PsiClass> getAllResultMap(PsiElement psiElement);

    /**
     * get all result map
     *
     * @param psiElement PsiElement
     * @return resultMap map
     */
    public abstract Map<String, XmlTag> getAllResultMap2(PsiElement psiElement);

    /**
     * get all parameter map
     *
     * @param psiElement PsiElement
     * @return parameterMap map
     */
    public abstract Map<String, PsiClass> getAllParameterMap(PsiElement psiElement);

    /**
     * get all parameter map
     *
     * @param psiElement PsiElement
     * @return parameterMap map
     */
    public abstract Map<String, XmlTag> getAllParameterMap2(PsiElement psiElement);

    /**
     * get all sql
     *
     * @param psiElement PsiElement
     * @return sql list
     */
    public abstract Map<String, Sql> getAllSql(PsiElement psiElement);

    /**
     * get all select
     *
     * @param psiElement PsiElement
     * @return Select list
     */
    public abstract Map<String, Select> getAllSelect(PsiElement psiElement);


    /**
     * get all insert
     *
     * @param psiElement PsiElement
     * @return Insert list
     */
    public abstract Map<String, Insert> getAllInsert(PsiElement psiElement);

    /**
     * get all update
     *
     * @param psiElement PsiElement
     * @return Update list
     */
    public abstract Map<String, Update> getAllUpdate(PsiElement psiElement);

    /**
     * get all delete
     *
     * @param psiElement PsiElement
     * @return Delete list
     */
    public abstract Map<String, Delete> getAllDelete(PsiElement psiElement);

    /**
     * get all statement
     *
     * @param psiElement PsiElement
     * @return Select list
     */
    public abstract Map<String, Statement> getAllStatement(PsiElement psiElement);

    /**
     * get all procedure
     *
     * @param psiElement PsiElement
     * @return Select list
     */
    public abstract Map<String, Procedure> getAllProcedure(PsiElement psiElement);


    /**
     * get all sql map  id reference
     *
     * @return sql map    id reference
     */
    public abstract Map<String, DomElement> getAllSqlMapReference(Module module);

    /**
     * get all  cacheModel
     *
     * @return sql map    id reference
     */
    public abstract Map<String, CacheModel> getAllCacheModel(PsiElement psiElement);
}
