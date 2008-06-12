package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.javadoc.PsiDocTag;

/**
 * element filter for javadoc table tag
 */
public class JavadocTagFilter implements ElementFilter {
    private String tagName;

    public JavadocTagFilter(String tagName) {
        this.tagName = tagName;
    }

    public boolean isAcceptable(Object o, PsiElement psiElement) {
        if (psiElement instanceof PsiDocTag) {
            PsiDocTag docTag = (PsiDocTag) psiElement;
            String name = docTag.getName();
            if (tagName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isClassAcceptable(Class aClass) {
        return true;
    }
}