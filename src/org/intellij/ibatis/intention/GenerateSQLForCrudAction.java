package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;

/**
 * Created by IntelliJ IDEA.
 * User: lmeadors
 * Date: Oct 17, 2007
 * Time: 10:13:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateSQLForCrudAction extends GenerateSQLBase{
	protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		if(isAvailable(project, editor, file)){
			XmlTag sqlMapTag = (XmlTag) element;
			XmlTag insert = sqlMapTag.createChildTag("insert", "", "", true);
			XmlTag select = sqlMapTag.createChildTag("select", "", "", true);
			XmlTag update = sqlMapTag.createChildTag("update", "", "", true);
			XmlTag delete = sqlMapTag.createChildTag("delete", "", "", true);
			try {
				String typeAlias = getTypeAlias(sqlMapTag);
				PsiClass c = IbatisClassShortcutsReferenceProvider.getPsiClass(element, typeAlias);

				removeNamespace(insert);
				removeNamespace(select);
				removeNamespace(update);
				removeNamespace(delete);
				addAttribute(insert, "id", "insert");
				addAttribute(select, "id", "select");
				addAttribute(update, "id", "update");
				addAttribute(delete, "id", "delete");
				addAttribute(insert, "parameterClass", typeAlias);
				addAttribute(select, "resultClass", typeAlias);
				addAttribute(update, "parameterClass", typeAlias);
				addAttribute(delete, "parameterClass", typeAlias);
				buildInsert(insert, c);
				buildSelect(select, c);
				buildSqlUpdate(update, c);
				buildDelete(delete, c);
				sqlMapTag.add(insert);
				sqlMapTag.add(select);
				sqlMapTag.add(update);
				sqlMapTag.add(delete);
			} catch (IncorrectOperationException e) {
				e.printStackTrace();
			}
		}
	}

	private String getTypeAlias(XmlTag sqlMapTag) {
		return "Project";
	}

	private void addAttribute(XmlTag tag, String name, String value) throws IncorrectOperationException {
		tag.setAttribute(name, value);
	}

	private void removeNamespace(XmlTag tag) throws IncorrectOperationException {
		XmlAttribute attribute = tag.getAttribute("xmlns");
		if (attribute != null) {
			attribute.delete();
		}
	}

	protected boolean isAvailable(
		Project project,
		Editor editor,
		PsiFile file,
		@NotNull PsiElement element
	) {
		if(!(element instanceof XmlTag)){
			// gotta be an xml tag
			return false;
		}
		XmlTag tag = (XmlTag) element;
		if(!tag.getName().equals("sqlMap")){
			// it isn't a SQL map tag
			return false;
		}

		for(PsiElement pe : tag.getChildren()){
			if(pe instanceof XmlTag){
				// any potential crud operation?
				if(((XmlTag)pe).getName().equals("insert")) return false;
				if(((XmlTag)pe).getName().equals("select")) return false;
				if(((XmlTag)pe).getName().equals("update")) return false;
				if(((XmlTag)pe).getName().equals("delete")) return false;
			}
		}

		// well, if we're still here, it must be OK.
		return true;
	}

	@NotNull
	public String getText() {
		return "Generate SQL for all crud operations";
	}

	@NotNull
	public String getFamilyName() {
		return "GenerateSQLForCRUD";
	}
}
