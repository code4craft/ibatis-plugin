package org.intellij.ibatis.intention;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public abstract class GenerateSQLBase extends PsiIntentionBase {
	static Map<Integer, String> jdbcTypeNameMap = new HashMap<Integer, String>();

	static {
		populateTypeMap();
	}

	protected static void populateTypeMap() {
		Field[] fields = Types.class.getFields();
		for(Field f : fields){
			try {
				jdbcTypeNameMap.put(f.getInt(null), f.getName());
			} catch (IllegalAccessException e) {
				// let's just ignore this, ok?
			}
		}
	}

	/**
	 * Common logic to check if an intention is available (use from isAvailable)
	 * @param project - pass through
	 * @param file - pass through
	 * @param element - pass through
	 * @param tagName - what tag is this intention for?
	 * @param tagClass - what is the class of this tag?
	 * @param attributes - array of attrib names, one of these must be present
	 * @return true if the tag name and class match, it's empty, and one attribute is present
	 */
	protected boolean checkAvailable(
		Project project,
		PsiFile file,
		PsiElement element,
		String tagName,
		Class tagClass,
		String... attributes
	) {

		if (file instanceof XmlFile && element instanceof XmlTag) {
			XmlTag xmlTag = (XmlTag) element;
			if (xmlTag.getName().equals(tagName) && xmlTag.getValue().getText().trim().length() == 0) {
				// we are looking at an empty tag named ${tagName}
				// examine the matching attributes
				for(String attrib : attributes){
					if (xmlTag.getAttributeValue(attrib) != null) {
						DomElement domElement = DomManager.getDomManager(project).getDomElement(xmlTag);
						if (domElement != null && tagClass.isAssignableFrom(domElement.getClass())) {
							// todo: what if the parameter or result is a Map?
							return true;
						}
					}
				}

			}
		}

		// the default answer is no
		return false;
	}
}
