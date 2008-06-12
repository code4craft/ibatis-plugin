package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.jetbrains.annotations.NotNull;

/**
 * generate sql for select with result map
 */
public class GenerateSQLForSelectAction extends GenerateSQLBase {
	private static final String NAME = "GenerateSQLForSelectWithResultMap";
	private static final String TEXT = "Generate SQL for select with resultMap or resultClass";

	protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		if (isAvailable(project, editor, file)) {
			// this is the select xml tag
			XmlTag xmlTag = (XmlTag) element;

			// ...and resultClassElement is the resultMap as a PsiElement
			PsiElement resultMapElement = getXmlAttributeAsElement(xmlTag, "resultMap");

			if (resultMapElement != null && resultMapElement instanceof XmlAttribute) {

				// resultMapTag is the IDEA reference to the result map
				XmlTag resultMapTag = ((XmlAttribute) resultMapElement).getParent();

				// resultMap is the DOM element
				DomElement resultMap = DomManager.getDomManager(project).getDomElement(resultMapTag);

				if (resultMap != null && resultMap instanceof ResultMap) {
					buildSelectFromResultMap(element, (ResultMap) resultMap);
				}
			}

			PsiElement resultClassElement = getXmlAttributeAsElement(xmlTag, "resultClass");
			if (null != resultClassElement) {
				if (resultClassElement instanceof PsiClass) {
					buildSelect(xmlTag, (PsiClass) resultClassElement);
				} else {
					if (resultClassElement instanceof XmlTag) {
						XmlTag typeAliasTag = ((XmlTag) resultClassElement);
						DomElement typeAliasElement = DomManager.getDomManager(project).getDomElement(typeAliasTag);

						// this is the DOM element of the type alias tag
						if (typeAliasElement != null) {
							if (typeAliasElement instanceof org.intellij.ibatis.dom.sqlMap.TypeAlias) {
								org.intellij.ibatis.dom.sqlMap.TypeAlias typeAlias = (org.intellij.ibatis.dom.sqlMap.TypeAlias) typeAliasElement;
								buildSelect(element, typeAlias.getType().getValue());
							} else
							if (typeAliasElement instanceof org.intellij.ibatis.dom.configuration.TypeAlias) {
								org.intellij.ibatis.dom.configuration.TypeAlias typeAlias = (org.intellij.ibatis.dom.configuration.TypeAlias) typeAliasElement;
								buildSelect(element, typeAlias.getType().getValue());
							}
						}
					}
				}
			}
		}
	}

	protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		return checkAvailable(project, file, element, "select", Select.class, "resultMap", "resultClass");
	}

	@NotNull
	public String getText() {
		return TEXT;
	}

	@NotNull
	public String getFamilyName() {
		return NAME;
	}

}
