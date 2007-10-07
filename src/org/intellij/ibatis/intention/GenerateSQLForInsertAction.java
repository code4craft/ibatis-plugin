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
import org.intellij.ibatis.dom.sqlMap.Insert;
import org.intellij.ibatis.dom.sqlMap.ParameterMap;
import org.jetbrains.annotations.NotNull;

public class GenerateSQLForInsertAction extends GenerateSQLBase {
	protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement insertElement) {
		if (isAvailable(project, editor, file)) {

			// this will be the parameterClass or parameterMap attribute
			XmlAttribute attribute;

			// see if it's got a parameter map
			attribute = ((XmlTag) insertElement).getAttribute("parameterMap");
			if (null != attribute) {
				XmlAttributeValue xmlAttributeValue = attribute.getValueElement();
				if (null != xmlAttributeValue) {
					PsiReference psiReference = xmlAttributeValue.getReference();
					if (null != psiReference) {
						PsiElement psiElement = psiReference.resolve();
						if (psiElement != null && psiElement instanceof XmlAttribute) {
							XmlTag paramMapTag = ((XmlAttribute) psiElement).getParent();
							DomElement paramMapTemp = DomManager.getDomManager(project).getDomElement(paramMapTag);
							if(paramMapTemp != null && paramMapTemp instanceof ParameterMap){
								// todo: handle this
								//ParameterMap pm = (ParameterMap) paramMapTemp;
							}
						}
					}
				}
			}

			// see if it's a parameter class
			attribute = ((XmlTag) insertElement).getAttribute("parameterClass");
			if (null != attribute) {
				// it has a parameter class

				// this is the value of the attribute
				XmlAttributeValue xmlAttributeValue = attribute.getValueElement();
				if (null != xmlAttributeValue) {
					PsiReference psiReference = xmlAttributeValue.getReference();
					if (null != psiReference) {
						PsiElement psiElement = psiReference.resolve();
						if(psiElement instanceof XmlTag){
							// it's a type alias
							XmlTag typeAliasTag = (XmlTag) psiElement;
							DomElement typeAliasTemp = DomManager.getDomManager(project).getDomElement(typeAliasTag);
							if (typeAliasTemp != null && typeAliasTemp instanceof org.intellij.ibatis.dom.sqlMap.TypeAlias) {
								org.intellij.ibatis.dom.sqlMap.TypeAlias ta = (org.intellij.ibatis.dom.sqlMap.TypeAlias) typeAliasTemp;
								buildInsert(insertElement, ta.getType().getValue());
							} else if (typeAliasTemp != null && typeAliasTemp instanceof org.intellij.ibatis.dom.configuration.TypeAlias) {
								org.intellij.ibatis.dom.configuration.TypeAlias ta = (org.intellij.ibatis.dom.configuration.TypeAlias) typeAliasTemp;
								buildInsert(insertElement, ta.getType().getValue());
							}
						}else if(psiElement instanceof PsiClass){
							buildInsert(insertElement, (PsiClass) psiElement);
						}
					}
				}
			}
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
