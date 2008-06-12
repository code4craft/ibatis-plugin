package org.intellij.ibatis.provider;

import com.intellij.openapi.module.impl.scopes.JdkScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.ibatis.model.JdbcType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * jdbc type reference provider
 *
 * @author yangsy.cq@gmail.com
 */
public class ParameterJdbcTypeReferenceProvider extends BaseReferenceProvider {

    private PsiField[] jdbcTypes;
    private PsiField oracleType;
    private PsiField ibatisType;

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {

            public boolean isSoft() {
                if ("ORACLECURSOR".equals(getCanonicalText())) {
                    PsiField oracle = getOracleCursorPsiFields(getElement());
                    if (oracle == null) {
                        return true;
                    }
                }
                return false;
            }

            @Nullable
            public PsiElement resolve() {
                Map<String, Integer> types= JdbcType.TYPES;
                String type = getCanonicalText();
                if (type.length() < 3) {
                    Integer val = types.get(type);
                    Set<String> keys = types.keySet();
                    for (String key : keys) {
                        Integer typeVal = types.get(key);
                        if (!key.equals(type) && typeVal.equals(val)) {
                            type = key;
                            break;
                        }
                    }
                }
                PsiField[] fields = getJdbcTypePsiFields(getElement()); //先找标标准库
                if (fields != null) {
                    for (PsiField psiField : fields) {
                        if (type.equals(psiField.getName())) {
                            return psiField;
                        }
                    }
                }
                if ("ORACLECURSOR".equals(type)) {
                    PsiField oracle = getOracleCursorPsiFields(getElement());
                    if (oracle != null) {
                        return oracle;
                    }
                }
                return getIbatisPsiFields(getElement());
            }

            @Nullable private PsiField[] getJdbcTypePsiFields(PsiElement psiElement) {
                if (jdbcTypes == null) {
                    Project project = psiElement.getProject();
                    JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                    PsiClass psiClass = javaPsiFacade.findClass("java.sql.Types", JdkScope.allScope(project));
                    if (psiClass != null) {
                        jdbcTypes = psiClass.getAllFields();
                        return jdbcTypes;
                    }
                } else {
                    return jdbcTypes;
                }
                return null;
            }

            @Nullable private PsiField getOracleCursorPsiFields(PsiElement psiElement) {
                if (oracleType == null) {
                    Project project = psiElement.getProject();
                    JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                    PsiClass psiClass = javaPsiFacade.findClass("oracle.jdbc.OracleTypes", GlobalSearchScope.allScope(project));
                    if (psiClass != null) {
                        PsiField[] fields = psiClass.getAllFields();
                        for (PsiField psiField : fields) {
                            if ("CURSOR".equals(psiField.getName())) {
                                oracleType = psiField;
                                return oracleType;
                            }
                        }
                    }
                } else {
                    return oracleType;
                }
                return null;
            }

            @Nullable private PsiField getIbatisPsiFields(PsiElement psiElement) {
                if (ibatisType == null) {
                    Project project = psiElement.getProject();
                    JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                    PsiClass psiClass = javaPsiFacade.findClass("com.ibatis.sqlmap.engine.type.JdbcTypeRegistry", GlobalSearchScope.allScope(project));
                    if (psiClass != null) {
                        PsiField[] psiFields = psiClass.getAllFields();
                        for (PsiField psiField : psiFields) {
                            if ("UNKNOWN_TYPE".equals(psiField.getName())) {
                                ibatisType = psiField;
                                return ibatisType;
                            }
                        }
                    }
                } else {
                    return ibatisType;
                }
                return null;
            }

            public Object[] getVariants() {
                return JdbcType.TYPES.keySet().toArray();
            }
        };
        return new PsiReference[]{psiReference};
    }

}