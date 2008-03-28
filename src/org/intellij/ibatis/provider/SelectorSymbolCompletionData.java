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
            String prefix = completionContext.getPrefix();
            String previousText = getPreviousText(psiElement);
            //table name completion
            if (previousText != null && !prefix.contains("(") && (previousText.equalsIgnoreCase("from") || previousText.equalsIgnoreCase("join")
                    || previousText.equalsIgnoreCase("into") || previousText.equalsIgnoreCase("update"))) {
                DataSource datasource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(psiElement);
                if (datasource != null) {
                    List<DatabaseTableData> tables = datasource.getTables();
                    for (DatabaseTableData table : tables) {
                        variant.addCompletion(IbatisUtil.getTableNameWithoutSchema(table.getName()).toLowerCase());
                    }
                }
            } else { //selector completion
                String tableAlias = prefix.contains(".") ? prefix.substring(0, prefix.indexOf(".")) : null;
                List<String> parameterNames = getSelectorSymbolsForXmlTag(tag, tableAlias);   //table alias used
                String bracket = "";
                if (prefix.contains("(")) {
                    bracket = prefix.substring(0, prefix.indexOf("(") + 1);
                }
                if (parameterNames.size() > 0) {
                    for (String parameterName : parameterNames) {
                        variant.addCompletion(bracket + (tableAlias == null ? "" : tableAlias + ".") + parameterName.toLowerCase());
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
     * @param xmlTag     xmlTag
     * @param tableAlias table alias
     * @return name list
     */
    public List<String> getSelectorSymbolsForXmlTag(XmlTag xmlTag, String tableAlias) {
        List<String> nameList = new ArrayList<String>();
        String tableName = getTableName(xmlTag, tableAlias);
        if (tableName != null) {
            if (tableName.contains("(")) {
                tableName = tableName.substring(0, tableName.indexOf("("));
            }
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
     * @param xmlTag     xml tag
     * @param tableAlias table alias
     * @return table name in SQL sentence
     */
    @Nullable
    public String getTableName(XmlTag xmlTag, String tableAlias) {
        String sql = IbatisUtil.getSQLForXmlTag(xmlTag).trim().toLowerCase();
        if (StringUtil.isNotEmpty(tableAlias)) {
            String pattern = "\\b\\w+\\s+(as\\s+)?" + tableAlias + "(,)?\\s+";
            List<String> items = IbatisUtil.grep(sql + " ", pattern); //to make \W work in anywhere
            if (items.size() > 0) {  //find table alias
                for (String item : items) {
                    if (!item.contains("select ") && !item.contains("delete ") && !item.contains("update ")) {
                        return item.trim().split("\\s+")[0];
                    }
                }
            } else //set table alias as name
            {
                return tableAlias;
            }
        }
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
            String pattern = "from\\s+\\w+";
            List<String> items = IbatisUtil.grep(sql, pattern);
            if (items.size() > 0) {
                return items.get(0).split("\\s+")[1];
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