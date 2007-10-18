package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;
import org.intellij.ibatis.facet.IbatisFacetConfiguration;
import org.intellij.ibatis.util.IbatisUtil;

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
			IbatisFacetConfiguration conf = IbatisUtil.getConfig(element, file);
			generateCrudOperations(element, conf);
		}
	}

	public static void generateCrudOperations(PsiElement element, IbatisFacetConfiguration conf, PsiClass c) {
		final XmlTag sqlMapTag = (XmlTag) element;
		String typeAlias = getTypeAlias(sqlMapTag);
		final XmlTag insert = sqlMapTag.createChildTag("insert", "", "", true);
		final XmlTag select = sqlMapTag.createChildTag("select", "", "", true);
		final XmlTag update = sqlMapTag.createChildTag("update", "", "", true);
		final XmlTag delete = sqlMapTag.createChildTag("delete", "", "", true);
		try {
			if(null!= typeAlias){

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
				buildInsert(insert, c, conf);
				buildSelect(select, c);
				buildSqlUpdate(update, c);
				buildDelete(delete, c);
				CommandProcessor.getInstance().executeCommand(c.getProject(), new Runnable() {
					public void run() {
						try {
							sqlMapTag.add(insert);
							sqlMapTag.add(select);
							sqlMapTag.add(update);
							sqlMapTag.add(delete);
						} catch (IncorrectOperationException e) {
							e.printStackTrace();
						}
					}
				}, "", null, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);

			}
		} catch (IncorrectOperationException e) {
			e.printStackTrace();
		}
	}

	public static void generateCrudOperations(PsiElement element, IbatisFacetConfiguration conf) {
		XmlTag sqlMapTag = (XmlTag) element;
		String typeAlias = getTypeAlias(sqlMapTag);
		PsiClass c = IbatisClassShortcutsReferenceProvider.getPsiClass(element, typeAlias);
		generateCrudOperations(element, conf, c);
	}

	private static String getTypeAlias(XmlTag sqlMapTag) {
		for(PsiElement e : sqlMapTag.getChildren()){
			if(e instanceof XmlTag){
				XmlTag t = (XmlTag) e;
				if(t.getName().equals("typeAlias")){
					XmlAttribute aliasAttrib = t.getAttribute("alias");
					if (aliasAttrib != null) {
						return aliasAttrib.getValue();
					}
				}
			}
		}
		return null;
	}

	private static void addAttribute(XmlTag tag, String name, String value) throws IncorrectOperationException {
		tag.setAttribute(name, value);
	}

	private static void removeNamespace(XmlTag tag) throws IncorrectOperationException {
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
