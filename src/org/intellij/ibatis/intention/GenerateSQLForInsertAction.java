package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.ibatis.dom.sqlMap.Insert;
import org.jetbrains.annotations.NotNull;

public class GenerateSQLForInsertAction extends GenerateSQLBase {
	public void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement insertElement) {
		if (isAvailable(project, editor, file)) {
			createInsertTagContents(project, insertElement);
		}
	}

	protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		return checkAvailable(project, file, element, "insert", Insert.class, "parameterClass");
	}

	@NotNull
	public String getText() {
		return "Generate SQL for an insert based on parameter class";
	}

	@NotNull
	public String getFamilyName() {
		return "GenerateSQLForInsert";
	}
}
