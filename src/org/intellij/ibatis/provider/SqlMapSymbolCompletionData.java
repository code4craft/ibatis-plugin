package org.intellij.ibatis.provider;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionData;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiReference;
import com.intellij.psi.filters.TextFilter;
import com.intellij.psi.filters.TrueFilter;
import com.intellij.psi.filters.position.LeftNeighbour;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * completion data for sql map symbol
 */
public class SqlMapSymbolCompletionData extends CompletionData {
    public static String OPEN_TAG = "#";
    public static String CLOSE_TAG = "#";
    private List<String> sentenceNames = new ArrayList<String>();

    public SqlMapSymbolCompletionData() {
        sentenceNames.add("select");
        sentenceNames.add("insert");
        sentenceNames.add("update");
        sentenceNames.add("delete");
        sentenceNames.add("procedue");
        sentenceNames.add("statement");
    }


    public String findPrefix(PsiElement psiElement, int offsetInFile) {
        return psiElement.getText().substring(0, offsetInFile - psiElement.getTextRange().getStartOffset());
    }

    public CompletionVariant[] findVariants(PsiElement psiElement, CompletionContext completionContext) {
        PsiFile psiFile = completionContext.file;
        if (psiFile instanceof XmlFile && "#".equals(completionContext.getPrefix())) {
            final DomFileElement fileElement = DomManager.getDomManager(completionContext.project).getFileElement((XmlFile) completionContext.file, DomElement.class);
            if (fileElement != null && fileElement.getRootElement() instanceof SqlMap) {
                XmlTag tag = getParentSentence(psiElement);
                if (tag != null) {
                    LeftNeighbour left = new LeftNeighbour(new TextFilter(OPEN_TAG));
                    CompletionVariant variant = new CompletionVariant(left);
                    List<String> parameterNames = getParameterNamesForXmlTag(tag, "#");
                    for (String parameterName : parameterNames) {

                        variant.addCompletion(parameterName);
                    }
                    variant.includeScopeClass(PsiElement.class, true);
                    variant.addCompletionFilter(TrueFilter.INSTANCE);
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
     * @param prefix prefix for name
     * @return name list
     */
    public List<String> getParameterNamesForXmlTag(XmlTag xmlTag, String prefix) {
        List<String> nameList = new ArrayList<String>();
        String parameterClass = xmlTag.getAttributeValue("parameterClass");
        if (StringUtil.isNotEmpty(parameterClass)) {
            PsiClass psiClass = IbatisClassShortcutsReferenceProvider.getPsiClass(xmlTag, parameterClass);
            if (psiClass != null) {
                List<String> methodNames = FieldAccessMethodReferenceProvider.getAllGetterMethods(psiClass, "");
                for (String methodName : methodNames) {
                    nameList.add(prefix + methodName + "#");
                }
            }
        }
        XmlAttribute parameterMap = xmlTag.getAttribute("parameterMap");
        if (parameterMap != null && StringUtil.isNotEmpty(parameterMap.getValue())) {
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
                                nameList.add(prefix + property + "#");
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
            if (sentenceNames.contains(tag.getName())) {
                return tag;
            } else {
                return getParentSentence(tag);
            }
        }
        return null;
    }
}
