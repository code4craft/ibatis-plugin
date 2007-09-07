package org.intellij.ibatis.provider;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionData;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.filters.TrueFilter;
import com.intellij.psi.filters.position.LeftNeighbour;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * completion data for selector symbol
 */
public class SelectorSymbolCompletionData extends CompletionData {

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
        PsiFile psiFile = completionContext.file;
        String prefix = completionContext.getPrefix();
        if (psiFile instanceof XmlFile) {   //xml file and prefix match
            final DomFileElement fileElement = DomManager.getDomManager(completionContext.project).getFileElement((XmlFile) completionContext.file, DomElement.class);
            if (fileElement != null && fileElement.getRootElement() instanceof SqlMap) {
                XmlTag tag = getParentSentence(psiElement);
                if (tag != null) {
                    LeftNeighbour left = new LeftNeighbour(TrueFilter.INSTANCE);
                    CompletionVariant variant = new CompletionVariant(left);
                    List<String> parameterNames = getSelectorSymbolsForXmlTag(tag);
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
        XmlAttribute attribute = xmlTag.getAttribute("resultMap");
        if (attribute != null) {
            PsiReference psiReference = attribute.getValueElement().getReference();
            if (psiReference != null) {
                PsiElement psiElement = psiReference.resolve();
                if (psiElement != null && psiElement instanceof XmlAttribute) {  //resultMap element
                    ResultMap resultMap = (ResultMap) DomManager.getDomManager(xmlTag.getProject()).getDomElement(PsiTreeUtil.getParentOfType(psiElement, XmlTag.class));
                    if (resultMap != null) {
                        for (Result result : resultMap.getAllResults()) {
                            nameList.add(result.getColumn().getValue());
                        }
                    }
                }
            }
        }
        return nameList;
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