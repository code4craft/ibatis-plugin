package org.intellij.ibatis.provider;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionData;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.XmlCompletionData;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.TrueFilter;
import com.intellij.psi.filters.position.LeftNeighbour;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * completion data for selector symbol
 */
public class SelectorSymbolCompletionData extends XmlCompletionData {
    private CompletionData parentCompletionData;

    public SelectorSymbolCompletionData(CompletionData parentCompletionData) {
        this.parentCompletionData = parentCompletionData;
    }

    public String findPrefix(PsiElement psiElement, int offsetInFile) {
        if (SqlMapSymbolCompletionData.getXmlTagForSQLCompletion(psiElement, psiElement.getContainingFile()) != null) {
            return psiElement.getText().substring(0, offsetInFile - psiElement.getTextRange().getStartOffset());
        } else
            return super.findPrefix(psiElement, offsetInFile);
    }

    public CompletionVariant[] findVariants(PsiElement psiElement, CompletionContext completionContext) {
        XmlTag tag = SqlMapSymbolCompletionData.getXmlTagForSQLCompletion(psiElement, completionContext.file);
        if (tag != null && !completionContext.getPrefix().contains("#")) {   //not symbol
            LeftNeighbour left = new LeftNeighbour(TrueFilter.INSTANCE);
            CompletionVariant variant = new CompletionVariant(left);
            String previousText = getPreviousText(psiElement);
            if (previousText != null && (previousText.equalsIgnoreCase("from") || previousText.equalsIgnoreCase("join"))) {
                DataSource datasource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(psiElement);
                if (datasource != null) {
                    List<DatabaseTableData> tables = datasource.getTables();
                    for (DatabaseTableData table : tables) {
                        variant.addCompletion(IbatisUtil.getTableNameWithoutSchema(table.getName()));
                    }
                }
            } else {
                List<String> parameterNames = getSelectorSymbolsForXmlTag(tag);
                if (parameterNames.size() > 0) {
                    for (String parameterName : parameterNames) {
                        variant.addCompletion(parameterName);
                    }
                }
            }
            variant.includeScopeClass(PsiElement.class, true);
            variant.addCompletionFilter(TrueFilter.INSTANCE);
            variant.setInsertHandler(new SqlMapSymbolnsertHandler());
            return new CompletionVariant[]{variant};
        }
        if (parentCompletionData != null) return parentCompletionData.findVariants(psiElement, completionContext);
        return super.findVariants(psiElement, completionContext);
    }

    @Nullable
    public String getPreviousText(PsiElement psiElement) {
        PsiElement prev = psiElement.getPrevSibling();
        if (prev != null) {
            if (StringUtil.isNotEmpty(prev.getText().trim())) {
                return prev.getText();
            } else {
                return getPreviousText(prev);
            }
        }
        return null;
    }

    /**
     * get parameter name list
     *
     * @param xmlTag xmlTag
     * @return name list
     */
    public List<String> getSelectorSymbolsForXmlTag(XmlTag xmlTag) {
        List<String> nameList = new ArrayList<String>();
        String tableName = getTableName(xmlTag);
        if (tableName != null) {
            DataSource dataSource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(xmlTag);
            if (dataSource != null) {
                DatabaseTableData table = null;
                List<DatabaseTableData> tableList = dataSource.getTables();
                for (DatabaseTableData databaseTableData : tableList) {
                    if (databaseTableData.getName().replaceAll("\\w*\\.", "").equalsIgnoreCase(tableName)) {  //table name match
                        table = databaseTableData;
                        break;
                    }
                }
                if (table != null) {
                    List<DatabaseTableFieldData> fieldList = table.getFields();
                    for (DatabaseTableFieldData databaseTableFieldData : fieldList) {
                        nameList.add(databaseTableFieldData.getName());

                    }
                }
            }
        }
        return nameList;
    }

    /**
     * get table name in SQL Map tag
     *
     * @param xmlTag xml tag
     * @return table name in SQL sentence
     */
    @Nullable
    public String getTableName(XmlTag xmlTag) {
        String sql = IbatisUtil.getSQLForXmlTag(xmlTag).trim().toLowerCase();
        if (sql.startsWith("insert")) {   // insert
            String tableName = sql.substring(sql.indexOf("into") + 4).trim();
            tableName = tableName.split("\\s+")[0];
            if (tableName.contains("."))
                tableName = tableName.substring(0, tableName.indexOf("."));
            return tableName;
        } else if (sql.startsWith("update")) {     //update
            String tableName = sql.substring(sql.indexOf("update") + 6).trim();
            tableName = tableName.split("\\s+")[0];
            if (tableName.contains("."))
                tableName = tableName.substring(0, tableName.indexOf("."));
            return tableName;
        } else {  //select and delete
            String[] parts = sql.split("from\\s+");
            if (parts.length > 1) {
                String tableName = parts[1].trim();
                tableName = tableName.split("\\s+")[0];
                if (tableName.contains("."))
                    tableName = tableName.substring(0, tableName.indexOf("."));
                return tableName;
            }
        }
        return null;
    }

    /**
     * get sql setence tag for  psiElement
     *
     * @param psiElement psiElement object
     * @return XmlTag object
     */
    @Nullable
    public XmlTag getParentSentence(PsiElement psiElement) {
        XmlTag tag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
        if (tag != null) {
            if (StringUtil.isNotEmpty(tag.getAttributeValue("resultMap"))) {
                return tag;
            } else {
                return getParentSentence(tag);
            }
        }
        return null;
    }
}