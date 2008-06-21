package org.intellij.ibatis.provider;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ElementPatternCondition;
import com.intellij.patterns.InitialPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.ProcessingContext;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.Nullable;

/**
 * sql map client method call filter
 */
public class SqlClientElementFilter implements ElementPattern<PsiLiteralExpression> {
    public static String operationPattern = "(execute)?((query[(for)|(with)]\\w*)|insert|update|delete)";

    public boolean accepts(@Nullable Object o) {
        return false;
    }

    public boolean accepts(@Nullable Object o, ProcessingContext processingContext) {
        return false;
    }

    public ElementPatternCondition<PsiLiteralExpression> getCondition() {
        return new ElementPatternCondition<PsiLiteralExpression>(new InitialPatternCondition<PsiLiteralExpression>(PsiLiteralExpression.class) {
            @Override
            public boolean accepts(@Nullable Object o, ProcessingContext processingContext) {
                PsiLiteralExpression literalExpression = (PsiLiteralExpression) o;
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
                    String methodName = path[path.length - 1].trim().toLowerCase();
                    if (methodName.matches(operationPattern) && IbatisUtil.getConfig(literalExpression) != null) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
