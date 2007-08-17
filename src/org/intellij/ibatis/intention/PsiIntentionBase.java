/*
 * Copyright 2000-2006 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.ibatis.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author davdeev
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
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
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