package org.intellij.ibatis.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * base intention action
 *
 * @author Jacky
 */
public abstract class PsiIntentionBase implements IntentionAction {

    public boolean startInWriteAction() {
        return true;
    }

    /**
     * Checks whether this intention is available at a caret offset in file.
     * If this method returns true, a light bulb for this intention is shown.
     */
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (element == null) {
            return false;
        } else {
            element = element.getParent();
            return isAvailable(project, editor, file, element);
        }
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (element != null) {
            element = element.getParent();
            invoke(project, editor, psiFile, element);
        }
    }

    protected abstract void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element);

    protected abstract boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element);
}