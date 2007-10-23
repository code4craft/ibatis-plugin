package org.intellij.ibatis.provider;

import com.intellij.psi.*;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.util.InheritanceUtil;

import java.util.ArrayList;
import java.util.List;

import org.intellij.ibatis.util.IbatisUtil;

/**
 * sql map client method call filter
 */
public class SqlClientElementFilter implements ElementFilter {
    public static String operationPattern = "(query[(For)|(With)]\\w*)|insert|update|delete";

    public boolean isAcceptable(Object o, PsiElement psiElement) {
        PsiLiteralExpression literalExpression = (PsiLiteralExpression) psiElement;
        PsiElement parent = literalExpression.getParent().getParent();
        if (parent != null && parent instanceof PsiMethodCallExpression) {
            //first parameter validate
            PsiElement previousElement = literalExpression.getPrevSibling();
            if (previousElement != null) {
                String text1 = previousElement.getText();
                String text2 = "";
                if (!text1.equals("(") && previousElement.getPrevSibling() != null) {
                    text2 = previousElement.getPrevSibling().getText();
                }
                if (!(text1.equals("(") || (text1.concat(text2).trim().equals("(")))) {
                    return false;
                }
            }
            //method validation
            final PsiMethodCallExpression callExpression = (PsiMethodCallExpression) parent;
            String[] path = callExpression.getMethodExpression().getText().split("\\.");
            String methodName = path[path.length - 1].trim();
            if (methodName.matches(operationPattern) && IbatisUtil.getConfig(psiElement) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isClassAcceptable(Class aClass) {
        return aClass == PsiLiteralExpression.class;
    }
}
