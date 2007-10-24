package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.intellij.ibatis.provider.SqlClientElementFilter;
import org.intellij.ibatis.provider.StatementIdReferenceProvider;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.NotNull;

/**
 * generate statement xml code according to sql map client invocation when statement id absent
 *
 * @author Jacky
 */
public class GenerateStatementXmlCodeAction extends PsiIntentionBase {
    private static final String NAME = "GenerateStatementXmlCode";
    private static final String TEXT = "Generate statement xml code when statement id absent";

    protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
        if (IbatisUtil.getConfig(element) != null)  //iBATIS enabled
        {
            PsiLiteralExpression expression = (PsiLiteralExpression) element;
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element.getParent().getParent();
            //todo collect the information for statement xml code creation
        }
    }

    /**
     * validate the element in caret is right for intention action
     *
     * @param project project
     * @param editor  editor
     * @param file    psi file,  java file need
     * @param element element in caret, PsiLiteralExpression need
     * @return available mark
     */
    protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
        if (file instanceof PsiJavaFile && element instanceof PsiLiteralExpression) {
            PsiLiteralExpression expression = (PsiLiteralExpression) element;
            PsiElement parent = element.getParent().getParent();
            if (parent instanceof PsiMethodCallExpression) {
                //method name validation simply
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) parent).getMethodExpression();
                String[] path = methodExpression.getText().split("\\.");
                String methodName = path[path.length - 1].trim();
                if (methodName.matches(SqlClientElementFilter.operationPattern)) {
                    PsiReference[] references = expression.getReferences();
                    for (PsiReference reference : references) {
                        if (reference instanceof StatementIdReferenceProvider.StatementIdReference) {
                            if (reference.resolve() == null) {  //the target statement resolved failed
                                return true;
                            }
                            break;
                        }
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
