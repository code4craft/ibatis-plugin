package org.intellij.ibatis.provider;

import com.intellij.openapi.module.impl.scopes.JdkScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * jdbc type reference provider
 *
 * @author yangsy.cq@gmail.com
 */
public class ParameterJdbcTypeReferenceProvider extends BaseReferenceProvider {

    private Map<String, Integer> types;
    private PsiField[] jdbcTypes;
    private PsiField oracleType;
    private PsiField ibatisType;

    public ParameterJdbcTypeReferenceProvider() {
        init();
    }

    private void init() {
        types = new HashMap<String, Integer>();
        types.put("ARRAY", Types.ARRAY);
        types.put("BIGINT", Types.BIGINT);
        types.put("BINARY", Types.BINARY);
        types.put("BIT", Types.BIT);
        types.put("BLOB", Types.BLOB);
        types.put("BOOLEAN", Types.BOOLEAN);
        types.put("CHAR", Types.CHAR);
        types.put("CLOB", Types.CLOB);
        types.put("DATALINK", Types.DATALINK);
        types.put("DATE", Types.DATE);
        types.put("DECIMAL", Types.DECIMAL);
        types.put("DISTINCT", Types.DISTINCT);
        types.put("DOUBLE", Types.DOUBLE);
        types.put("FLOAT", Types.FLOAT);
        types.put("INTEGER", Types.INTEGER);
        types.put("JAVA_OBJECT", Types.JAVA_OBJECT);
        types.put("LONGVARBINARY", Types.LONGVARBINARY);
        types.put("LONGVARCHAR", Types.LONGVARCHAR);
        types.put("NULL", Types.NULL);
        types.put("NUMERIC", Types.NUMERIC);
        types.put("OTHER", Types.OTHER);
        types.put("REAL", Types.REAL);
        types.put("REF", Types.REF);
        types.put("SMALLINT", Types.SMALLINT);
        types.put("STRUCT", Types.STRUCT);
        types.put("TIME", Types.TIME);
        types.put("TIMESTAMP", Types.TIMESTAMP);
        types.put("TINYINT", Types.TINYINT);
        types.put("VARBINARY", Types.VARBINARY);
        types.put("VARCHAR", Types.VARCHAR);

        types.put("CH", Types.CHAR);
        types.put("VC", Types.VARCHAR);

        types.put("DT", Types.DATE);
        types.put("TM", Types.TIME);
        types.put("TS", Types.TIMESTAMP);

        types.put("NM", Types.NUMERIC);
        types.put("II", Types.INTEGER);
        types.put("BI", Types.BIGINT);
        types.put("SI", Types.SMALLINT);
        types.put("TI", Types.TINYINT);

        types.put("DC", Types.DECIMAL);
        types.put("DB", Types.DOUBLE);
        types.put("FL", Types.FLOAT);

        types.put("ORACLECURSOR", -10);
    }

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
                    PsiManager psiManager = PsiManager.getInstance(project);
                    PsiClass psiClass = psiManager.findClass("java.sql.Types", JdkScope.allScope(project));
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
                    PsiManager psiManager = PsiManager.getInstance(project);
                    PsiClass psiClass = psiManager.findClass("oracle.jdbc.OracleTypes", GlobalSearchScope.allScope(project));
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
                    PsiManager psiManager = PsiManager.getInstance(project);
                    PsiClass psiClass = psiManager.findClass("com.ibatis.sqlmap.engine.type.JdbcTypeRegistry", GlobalSearchScope.allScope(project));
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
                return types.keySet().toArray();
            }
        };
        return new PsiReference[]{psiReference};
    }

}