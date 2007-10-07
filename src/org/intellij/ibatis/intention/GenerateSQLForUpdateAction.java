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
import org.intellij.ibatis.dom.sqlMap.Update;
import org.jetbrains.annotations.NotNull;

public class GenerateSQLForUpdateAction extends GenerateSQLBase {
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
						PsiElement psiElement = psiReference.resolve();
						if(psiElement instanceof PsiClass){
							// it's a class
							buildSqlUpdate(element, (PsiClass) psiElement);
						}else{
							// it's a type alias
							DomElement typeAliasTemp;

							if(psiElement instanceof XmlTag) {
								typeAliasTemp = DomManager.getDomManager(project).getDomElement(((XmlTag) psiElement));
								if (typeAliasTemp != null) {
									if (typeAliasTemp instanceof org.intellij.ibatis.dom.sqlMap.TypeAlias) {
										org.intellij.ibatis.dom.sqlMap.TypeAlias ta = (org.intellij.ibatis.dom.sqlMap.TypeAlias) typeAliasTemp;
										buildSqlUpdate(element, ta.getType().getValue());
									} else if (typeAliasTemp instanceof org.intellij.ibatis.dom.configuration.TypeAlias) {
										org.intellij.ibatis.dom.configuration.TypeAlias ta = (org.intellij.ibatis.dom.configuration.TypeAlias) typeAliasTemp;
										buildSqlUpdate(element, ta.getType().getValue());
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
		return checkAvailable(project, file, element, "update", Update.class, "parameterClass");
	}

	@NotNull
	public String getText() {
		return "Generate SQL for an update based on parameter class or parameter map";
	}

	@NotNull
	public String getFamilyName() {
		return "GenerateSQLForUpdate";
	}
}
