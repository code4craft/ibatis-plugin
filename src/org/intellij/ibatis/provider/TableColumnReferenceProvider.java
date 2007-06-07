package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;

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
                PsiDocComment docComment = psiClass.getDocComment();
                if (docComment != null) {
                    PsiDocTag tableTag = docComment.findTagByName("table");
                    if (tableTag != null) {
                        String tableName = tableTag.getValueElement().getText().trim();
                        if (StringUtil.isNotEmpty(tableName)) {
                            DataSource dataSource = DatabaseTableReferenceProvider.getDataSourceForIbatis(psiElement);
                            if (dataSource != null) {
                                DatabaseTableData tableData = null;
                                List<DatabaseTableData> tables = dataSource.getTables();
                                for (DatabaseTableData table : tables) {
                                    if (table.getName().equals(tableName)) {                                        
                                        tableData = table;
                                        break;
                                    }
                                }
                                if (tableData != null) {
                                    return new PsiReference[]{new TableColumnReference(xmlAttributeValue, tableData)};
                                }
                            }
                        }
                    }
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
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
                    fieldList.add(LookupValueFactory.createLookupValue(field.getName().toUpperCase(), IbatisConstants.DATABASE_PK_FIELD));
                } else {   //common column
                    fieldList.add(LookupValueFactory.createLookupValue(field.getName().toUpperCase(), IbatisConstants.DATABASE_COMMON_FIELD));
                }
            }
            return fieldList.toArray();
        }

        public boolean isSoft() {
            return true;
        }
    }
}
