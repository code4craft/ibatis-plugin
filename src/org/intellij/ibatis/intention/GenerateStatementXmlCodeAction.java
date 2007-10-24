package org.intellij.ibatis.intention;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.intellij.ibatis.provider.SqlClientElementFilter;

/**
 * generate statement xml code according to sql map client invocation when statement id absent
 *
 * @author Jacky
 */
public class GenerateStatementXmlCodeAction extends PsiIntentionBase {
    private static final String NAME = "GenerateStatementXmlCode";
    private static final String TEXT = "Generate statement xml code when statement id absent";

    protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {

    }

    protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression expression = (PsiLiteralExpression) element;
            PsiElement parent = element.getParent().getParent();
            if (parent instanceof PsiMethodCallExpression) {
                //method name validation simply
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) parent).getMethodExpression();
                String[] path = methodExpression.getText().split("\\.");
                String methodName = path[path.length - 1].trim();
                if (methodName.matches(SqlClientElementFilter.operationPattern)) {
                    PsiReference psiReference = expression.getReference();
                    if (psiReference != null && psiReference.resolve() == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @NotNull public String getText() {
        return TEXT;
    }

    @NotNull public String getFamilyName() {
        return NAME;
    }
}
