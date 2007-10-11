package org.intellij.ibatis.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.filters.TextFilter;
import com.intellij.psi.filters.TrueFilter;
import com.intellij.psi.filters.position.LeftNeighbour;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.intellij.ibatis.model.JdbcType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * completion data for SQL map symbol
 */
public class SqlMapSymbolCompletionData extends XmlCompletionData {
    public static String OPEN_TAG = "#";
    public static String CLOSE_TAG = "#";
    private static List<String> sentenceNames = new ArrayList<String>();
    private CompletionData systemCompletionData;
    private XmlCompletionData parentCompletionData;

    static {
        sentenceNames.add("select");
        sentenceNames.add("insert");
        sentenceNames.add("update");
        sentenceNames.add("delete");
        sentenceNames.add("procedue");
        sentenceNames.add("statement");
        sentenceNames.add("sql");
    }

    public SqlMapSymbolCompletionData(XmlCompletionData parentCompletionData) {
        this.parentCompletionData = parentCompletionData;
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
            if (psiFile instanceof XmlFile && prefix.startsWith(OPEN_TAG)) {   //xml file and prefix match
                final DomFileElement fileElement = DomManager.getDomManager(completionContext.project).getFileElement((XmlFile) completionContext.file, DomElement.class);
                if (fileElement != null && fileElement.getRootElement() instanceof SqlMap) {
                    XmlTag tag = getParentSentence(psiElement);
                    if (tag != null) {
                        LeftNeighbour left = new LeftNeighbour(new TextFilter(OPEN_TAG));
                        CompletionVariant variant = new CompletionVariant(left);
                        variant.includeScopeClass(PsiElement.class, true);
                        variant.addCompletionFilter(TrueFilter.INSTANCE);
                        variant.setInsertHandler(new SqlMapSymbolnsertHandler());
                        if (!prefix.contains(":")) {   //just clear in line parameter name
                            List<String> parameterNames = getParameterNamesForXmlTag(tag, OPEN_TAG);
                            for (String parameterName : parameterNames) {
                                variant.addCompletion(parameterName);
                            }
                        } else //jdbc type will be added
                        {
                            prefix = prefix.substring(0, prefix.indexOf(':'));
                            for (String typeName : JdbcType.TYPES.keySet()) {
                                variant.addCompletion(prefix + ":" + typeName);
                            }
                        }
                        return new CompletionVariant[]{variant};
                    }
                }
            }
        }
        if(parentCompletionData!=null) return parentCompletionData.findVariants(psiElement, completionContext);
        return super.findVariants(psiElement, completionContext);
    }

    /**
     * get parameter name list
     *
     * @param xmlTag xmlTag
     * @param prefix prefix for name
     * @return name list
     */
    public List<String> getParameterNamesForXmlTag(XmlTag xmlTag, String prefix) {
        List<String> nameList = new ArrayList<String>();
        String parameterClass = xmlTag.getAttributeValue("parameterClass");
        XmlAttribute parameterMap = xmlTag.getAttribute("parameterMap");
        List<String> symbolNames = getAllSymbolsInXmlTag(xmlTag);
        if (parameterClass == null && parameterMap == null)  //if parameterClass and parameterMap absent, use #value# as default
        {
            symbolNames.add("value");
        }
        for (String symbolName : symbolNames) {
            nameList.add(OPEN_TAG + symbolName + CLOSE_TAG);
        }
        return nameList;
    }

    /**
     * get all symbols for xml tag
     *
     * @param xmlTag Xml Tag
     * @return symbol name list
     */
    public static List<String> getAllSymbolsInXmlTag(XmlTag xmlTag) {
        String parameterClass = xmlTag.getAttributeValue("parameterClass");
        List<String> nameList = new ArrayList<String>();
        if (StringUtil.isNotEmpty(parameterClass)) {
            PsiClass psiClass = IbatisClassShortcutsReferenceProvider.getPsiClass(xmlTag, parameterClass);
            if (psiClass != null && !"Map".equals(psiClass.getName())) {
                if (IbatisClassShortcutsReferenceProvider.isDomain(psiClass.getName())) {   //domain class
                    Set<String> methodNames = FieldAccessMethodReferenceProvider.getAllGetterMethods(psiClass, "").keySet();
                    for (String methodName : methodNames) {
                        nameList.add(methodName);
                    }
                } else  //internal class
                {
                    nameList.add("value");
                }
            }
        }
        XmlAttribute parameterMap = xmlTag.getAttribute("parameterMap");
        if (parameterMap != null && StringUtil.isNotEmpty(parameterMap.getValue())) {
            //noinspection ConstantConditions
            PsiReference psiReference = parameterMap.getValueElement().getReference();
            if (psiReference != null) {
                PsiElement psiElement = psiReference.resolve();
                if (psiElement != null && psiElement instanceof XmlAttribute) {
                    XmlTag parameterMapTag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
                    if (parameterMapTag != null) {
                        XmlTag[] parameterTags = parameterMapTag.findSubTags("parameter");
                        for (XmlTag parameterTag : parameterTags) {
                            String property = parameterTag.getAttributeValue("property");
                            if (StringUtil.isNotEmpty(property))
                                nameList.add(property);
                        }
                    }
                }
            }
        }
        return nameList;
    }
    
    /**
     * add default symbol
     *
     * @param nameList name list
     * @param prefix   prefix
     */
    public void addDefaultSymbol(List<String> nameList, String prefix) {
        nameList.add(prefix + "value" + CLOSE_TAG);
    }

    /**
     * get SQL sentence tag for  psiElement
     *
     * @param psiElement psiElement object
     * @return XmlTag object
     */
    @Nullable
    public static XmlTag getParentSentence(PsiElement psiElement) {
        XmlTag tag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
        if (tag != null) {
            if (sentenceNames.contains(tag.getName())) {
                return tag;
            } else {
                return getParentSentence(tag);
            }
        }
        return null;
    }
}
