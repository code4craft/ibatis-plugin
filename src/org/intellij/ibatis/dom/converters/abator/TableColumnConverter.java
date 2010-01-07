package org.intellij.ibatis.dom.converters.abator;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.intellij.ibatis.dom.abator.Table;
import org.intellij.ibatis.provider.XmlAttributeValuePsiReference;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * table column converter
 */
public class TableColumnConverter extends Converter<DatabaseTableFieldData> implements CustomReferenceConverter<DatabaseTableFieldData> {
    public DatabaseTableFieldData fromString(@Nullable @NonNls String s, ConvertContext convertContext) {
        Table tableElement = convertContext.getInvocationElement().getParentOfType(Table.class, false);
        if (tableElement != null) {
            DatabaseTableData databaseTable = tableElement.getTableName().getValue();
            if (databaseTable != null) {
                return databaseTable.findColumnByName(s);
            }
        }
        return null;
    }

    public String toString(@Nullable DatabaseTableFieldData databaseTableFieldData, ConvertContext convertContext) {
        if (databaseTableFieldData != null)
            return databaseTableFieldData.getName();
        return null;
    }

    @NotNull
    public PsiReference[] createReferences(GenericDomValue<DatabaseTableFieldData> genericDomValue, final PsiElement psiElement, ConvertContext convertContext) {
        final Table tableElement = convertContext.getInvocationElement().getParentOfType(Table.class, false);
        if (tableElement != null) {
            final DatabaseTableData databaseTable = tableElement.getTableName().getValue();
            if (databaseTable != null) {
                return new PsiReference[]{new XmlAttributeValuePsiReference((XmlAttributeValue) psiElement) {
                    public Object[] getVariants() {
                        List<Object> variants = new ArrayList<Object>();
                        for (DatabaseTableFieldData field : databaseTable.getColumns()) {
                            if (field.isPrimary()) {       //pk
                                variants.add(LookupValueFactory.createLookupValue(field.getName().toUpperCase(), IbatisConstants.DATABASE_PK_FIELD));
                            } else {   //common column
                                variants.add(LookupValueFactory.createLookupValue(field.getName().toUpperCase(), IbatisConstants.DATABASE_COMMON_FIELD));
                            }
                        }
                        return variants.toArray();
                    }

                    @Nullable
                    public PsiElement resolve() {
                        String fieldName = getCanonicalText();
                        if (databaseTable.findColumnByName(fieldName) != null)
                            return psiElement.getParent();
                        return null;
                    }

                    public boolean isSoft() {
                        return false;
                    }
                }};
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}