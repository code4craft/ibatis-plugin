package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.Delete;
import org.jetbrains.annotations.NotNull;

public class GenerateSQLForDeleteAction extends GenerateSQLBase {

	protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		if(isAvailable(project, editor, file)){
			XmlAttribute attribute;

			// element is the insert tag

			// ok, see if it's a parameter map
			attribute = ((XmlTag) element).getAttribute("parameterMap");
			if (null != attribute) {
				XmlAttributeValue xmlAttributeValue = attribute.getValueElement();
				if (null != xmlAttributeValue) {
					PsiReference psiReference = xmlAttributeValue.getReference();
					if (null != psiReference) {
						PsiElement psiElement = psiReference.resolve();
						if (psiElement != null && psiElement instanceof XmlAttribute) {
							// todo: handle this parameter map
						}
					}
				}
			}

			// ok, then see if it's a parameter class
			attribute = ((XmlTag) element).getAttribute("parameterClass");
			if (null != attribute) {
				XmlAttributeValue xmlAttributeValue = attribute.getValueElement();
				if (null != xmlAttributeValue) {
					PsiReference psiReference = xmlAttributeValue.getReference();
					if (null != psiReference) {
						PsiElement parameterElement = psiReference.resolve();
						if (parameterElement instanceof PsiClass) {
							buildDelete(element, (PsiClass) parameterElement);
						} else {
							// it's a type alias
							if (parameterElement instanceof XmlTag) {
								DomElement typeAliasTemp = DomManager.getDomManager(project).getDomElement(((XmlTag) parameterElement));
								if (typeAliasTemp != null){
									if(typeAliasTemp instanceof org.intellij.ibatis.dom.sqlMap.TypeAlias) {
										org.intellij.ibatis.dom.sqlMap.TypeAlias ta = (org.intellij.ibatis.dom.sqlMap.TypeAlias) typeAliasTemp;
										buildDelete(element, ta.getType().getValue());
									}else if (typeAliasTemp instanceof org.intellij.ibatis.dom.configuration.TypeAlias) {
										org.intellij.ibatis.dom.configuration.TypeAlias ta = (org.intellij.ibatis.dom.configuration.TypeAlias) typeAliasTemp;
										buildDelete(element, ta.getType().getValue());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		return checkAvailable(project, file, element, "delete", Delete.class, "parameterClass");
	}

	@NotNull
	public String getText() {
		return "Generate SQL for a delete based on parameter class";
	}

	@NotNull
	public String getFamilyName() {
		return "GenerateSQLForDelete";
	}
}