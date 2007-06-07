package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.javadoc.PsiDocTag;

/**
 * element filter for javadoc table tag
 */
public class JavadocTableTagFilter implements ElementFilter {
    public boolean isAcceptable(Object o, PsiElement psiElement) {
        if(psiElement instanceof PsiDocTag)
        {
            PsiDocTag docTag=(PsiDocTag) psiElement;
          String name=   docTag.getName();
            if("table".equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isClassAcceptable(Class aClass) {
        return true;
    }
}
