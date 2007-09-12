package org.intellij.ibatis.provider;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionData;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.filters.TrueFilter;
import com.intellij.psi.filters.position.LeftNeighbour;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * completion data for selector symbol
 */
public class SelectorSymbolCompletionData extends CompletionData {
    private CompletionData systemCompletionData;

    public SelectorSymbolCompletionData(CompletionData completionData) {
        this.systemCompletionData = completionData;
    }

    /**
     * get prefix for psiElement
     *
     * @param psiElement   PsiElement object
     * @param offsetInFile offset in file
     * @return prefix
     */
    public String findPrefix(PsiElement psiElement, int offsetInFile) {
        return psiElement.getText().substring(0, offsetInFile - psiElement.getTextRange().getStartOffset());
    }

    public CompletionVariant[] findVariants(PsiElement psiElement, CompletionContext completionContext) {
        if (psiElement.getParent() instanceof XmlText) {   // text only
            PsiFile psiFile = completionContext.file;
            String prefix = completionContext.getPrefix();
            if (psiFile instanceof XmlFile) {   //xml file and prefix match
                final DomFileElement fileElement = DomManager.getDomManager(completionContext.project).getFileElement((XmlFile) completionContext.file, DomElement.class);
                if (fileElement != null && fileElement.getRootElement() instanceof SqlMap) {
                    XmlTag tag = SqlMapSymbolCompletionData.getParentSentence(psiElement);
                    if (tag != null && !prefix.contains("#")) {   //not symbol
                        LeftNeighbour left = new LeftNeighbour(TrueFilter.INSTANCE);
                        CompletionVariant variant = new CompletionVariant(left);
                        List<String> parameterNames = getSelectorSymbolsForXmlTag(tag);
                        if (parameterNames.size() > 0) {
                            for (String parameterName : parameterNames) {
                                variant.addCompletion(parameterName);
                            }
                            variant.includeScopeClass(PsiElement.class, true);
                            variant.addCompletionFilter(TrueFilter.INSTANCE);
                            variant.setInsertHandler(new SqlMapSymbolnsertHandler());
                            return new CompletionVariant[]{variant};
                        }
                    }
                }
            }
        }
        if (systemCompletionData != null) return systemCompletionData.findVariants(psiElement, completionContext);
        return super.findVariants(psiElement, completionContext);
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
                    if (databaseTableData.getName().replaceAll("\\w*\\.","" ).equalsIgnoreCase(tableName)) {  //table name match
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
     * @param xmlTag  xml tag
     * @return  table name in SQL sentence
     */
    @Nullable
    public String getTableName(XmlTag xmlTag) {
        String sql = getSQL(xmlTag).trim().toLowerCase();
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
     * got SQL code in sentence
     *
     * @param xmlTag xml tag
     * @return SQL code in sentence
     */
    @SuppressWarnings({"StringConcatenationInsideStringBufferAppend"}) public String getSQL(XmlTag xmlTag) {
        StringBuilder sql = new StringBuilder();
        PsiElement[] children = xmlTag.getChildren();
        for (PsiElement child : children) {
            if (child instanceof XmlTag) {
                XmlTag tag = (XmlTag) child;
                if (tag.getName().equals("include")) {
                    XmlAttribute refid = tag.getAttribute("refid");
                    if (refid != null && StringUtil.isNotEmpty(refid.getText())) {
                        PsiElement psiElement = refid.getValueElement().getReference().resolve();
                        if (psiElement instanceof XmlAttribute) {
                            XmlAttribute idAttribute = (XmlAttribute) psiElement;
                            sql.append(" " + idAttribute.getParent().getValue().getText());
                        }
                    }
                }
            } else if (child instanceof XmlText) {
                sql.append(" " + ((XmlText) child).getValue());
            }
        }
        return sql.toString();
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