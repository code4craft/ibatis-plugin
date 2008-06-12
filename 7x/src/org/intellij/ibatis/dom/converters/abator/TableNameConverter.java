package org.intellij.ibatis.dom.converters.abator;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.intellij.ibatis.provider.JavadocTableNameReferenceProvider;
import org.intellij.ibatis.provider.XmlAttributeValuePsiReference;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * table name converter
 */
public class TableNameConverter extends Converter<DatabaseTableData> implements CustomReferenceConverter<DatabaseTableData> {

    public DatabaseTableData fromString(@Nullable @NonNls String s, ConvertContext convertContext) {
        DataSource dataSource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(convertContext.getReferenceXmlElement());
        for (DatabaseTableData databaseTableData : dataSource.getTables()) {
            if (databaseTableData.getName().equals(s))
                return databaseTableData;
        }
        return null;
    }

    public String toString(@Nullable DatabaseTableData databaseTableData, ConvertContext convertContext) {
        if (databaseTableData != null)
            return databaseTableData.getName();
        else return null;
    }

    @NotNull
    public PsiReference[] createReferences(GenericDomValue genericDomValue, final PsiElement psiElement, ConvertContext convertContext) {
        return new PsiReference[]{new XmlAttributeValuePsiReference((XmlAttributeValue) psiElement) {
            public Object[] getVariants() {
                DataSource dataSource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(psiElement);
                List<Object> variants = new ArrayList<Object>();
                if (dataSource != null) {
                    List<DatabaseTableData> tables = dataSource.getTables();
                    for (DatabaseTableData table : tables) {
                        variants.add(LookupValueFactory.createLookupValue(table.getName(), IbatisConstants.DATABASE_TABLE));
                    }
                }
                return variants.toArray();
            }
        }};
    }

}
