package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * table column reference provider
 */
public class TableColumnReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(final PsiElement psiElement) {
        final XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        final XmlTag xmlTag = (XmlTag) xmlAttributeValue.getParent().getParent().getParent();   //result or parameter tag
        if (xmlTag.getAttributeValue("class") != null) {
            final PsiClass psiClass = IbatisClassShortcutsReferenceProvider.getPsiClass(xmlAttributeValue, xmlTag.getAttributeValue("class"));
            if (psiClass != null) {
                DatabaseTableData tableData = getDatabaseTableData(psiClass);
                if (tableData != null) {
                    return new PsiReference[]{new TableColumnReference(xmlAttributeValue, tableData)};
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }

    /**
     * get tableData in  PsiClass
     *
     * @param psiClass PsiClass
     * @return tableData object
     */
    @Nullable
    public static DatabaseTableData getDatabaseTableData(PsiClass psiClass) {
        PsiDocComment docComment = psiClass.getDocComment();
        if (docComment != null && docComment.findTagByName("table") != null) {
            PsiDocTag tableTag = docComment.findTagByName("table");
            String tableName = tableTag.getValueElement().getText().trim();
            if (StringUtil.isNotEmpty(tableName)) {
                DataSource dataSource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(psiClass);
                if (dataSource != null) {
                    List<DatabaseTableData> tables = dataSource.getTables();
                    for (DatabaseTableData table : tables) {
                        if (table.getName().equals(tableName)) {
                            return table;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * get tableFieldData in psiMethod
     *
     * @param psiMethod PsiMethod object
     * @return DatabaseTableFieldData object
     */
    @Nullable
    public static DatabaseTableFieldData getDatabaseTableFieldData(PsiMethod psiMethod) {
        PsiDocComment docComment = psiMethod.getDocComment();
        if (docComment != null && docComment.findTagByName("column") != null) {
            PsiDocTag psiDocTag = docComment.findTagByName("column");
            String columnName = psiDocTag.getValueElement().getText().trim();
            if (StringUtil.isNotEmpty(columnName)) {
                PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);
                if (psiClass != null) {
                    DatabaseTableData tableData = getDatabaseTableData(psiClass);
                    if (tableData != null)
                        return tableData.findColumnByName(columnName);
                }
            }
        }
        return null;
    }

    public class TableColumnReference extends XmlAttributeValuePsiReference {
        private DatabaseTableData databaseTableData;

        public TableColumnReference(XmlAttributeValue xmlAttributeValue, DatabaseTableData databaseTableData) {
            super(xmlAttributeValue);
            this.databaseTableData = databaseTableData;
        }

        public Object[] getVariants() {
            List<Object> fieldList = new ArrayList<Object>();
            List<DatabaseTableFieldData> fields = databaseTableData.getFields();
            for (DatabaseTableFieldData field : fields) {
                if (field.isPrimary()) {       //pk
                    fieldList.add(LookupValueFactory.createLookupValue(field.getName(), IbatisConstants.DATABASE_PK_FIELD));
                } else {   //common column
                    fieldList.add(LookupValueFactory.createLookupValue(field.getName(), IbatisConstants.DATABASE_COMMON_FIELD));
                }
            }
            return fieldList.toArray();
        }

        public boolean isSoft() {
            return true;
        }
    }
}
