package org.intellij.ibatis.intention;

import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.dom.sqlMap.TypeAlias;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;
import static org.intellij.ibatis.provider.TableColumnReferenceProvider.getDatabaseTableData;
import static org.intellij.ibatis.provider.TableColumnReferenceProvider.getPrimaryKeyColumns;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * generate sql for select with result map
 */
public class GenerateSQLForSelectAction extends PsiIntentionBase {
	private static final String NAME = "GenerateSQLForSelectWithResultMap";
	private static final String TEXT = "Generate SQL for select with resultMap or resultClass";

	protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		if (isAvailable(project, editor, file)) {
			XmlTag xmlTag = (XmlTag) element;
			PsiReference psiReference = getReference(xmlTag, "resultMap");

			if (psiReference != null) {
				// we have a result map...
				PsiElement psiElement = psiReference.resolve();
				// ...and psiElement is the resultMap as a PsiElement

				if (psiElement != null && psiElement instanceof XmlAttribute) {

					// resultMapTag is the IDEA reference to the result map
					XmlTag resultMapTag = ((XmlAttribute) psiElement).getParent();

					// resultMapTemp is a test value for the DOM element
					DomElement resultMapTemp = DomManager.getDomManager(project).getDomElement(resultMapTag);

					if (resultMapTemp != null && resultMapTemp instanceof ResultMap) {

						// now we put it in the real variable
						ResultMap resultMap = (ResultMap) resultMapTemp;

						String className = resultMap.getClazz().getValue();

						if (StringUtil.isNotEmpty(className)) {
							PsiClass psiClass = IbatisClassShortcutsReferenceProvider.getPsiClass(element, resultMap.getClazz().getValue());
							List<Result> list = resultMap.getResults();
							List<String> columns = new ArrayList<String>();
							for (Result result : list) {
								String columnName = result.getColumn().getStringValue();
								if (StringUtil.isNotEmpty(columnName))
									columns.add(columnName);
							}
							String selectList = StringUtil.join(columns, ", ");

							String sql;
							String tableName;
							String sqlWhere;

							if (psiClass != null) {  //get table name
								DatabaseTableData tableData = getDatabaseTableData(psiClass);
								if (tableData != null) {
									// table name ready
									tableName = tableData.getName();
									sql = "\nselect " + selectList + "\nfrom " + tableName + "\n";

									// Add the pk selection - the user can cut what they don't want...
									List<DatabaseTableFieldData> keyCols = getPrimaryKeyColumns(tableData);
									sqlWhere = getSQLWhere(psiClass, keyCols);
									xmlTag.getValue().setText(sql + sqlWhere);
								}
							} else {  //table name is empty
								xmlTag.getValue().setText("\nselect " + selectList + " from\n");
							}
						}
					}
				}
			}

			psiReference = getReference(xmlTag, "resultClass");
			if (null != psiReference) {
				PsiElement psiElement = psiReference.resolve();
				if (null == psiElement) {
					return;
				}
				XmlTag TAxmlTag = ((XmlTag) psiElement); // this is the typeAlias tag
				DomElement select = DomManager.getDomManager(project).getDomElement(TAxmlTag); // this is the DOM element of the select tag
				PsiClass value;
				if (select != null && select instanceof TypeAlias) {
					TypeAlias ta = (TypeAlias) select;
					value = ta.getType().getValue();
					String className;
					if (null != value) {
						className = value.getQualifiedName();
						PsiClass psiClass = IbatisClassShortcutsReferenceProvider.getPsiClass(psiElement, className);

						// we have the setters, now get the columns and match them up
						DatabaseTableData tableData = getDatabaseTableData(value);
						if (null != tableData) {

							List<DatabaseTableFieldData> fieldList = tableData.getFields();
							StringBuilder selectList = new StringBuilder("");
							for (DatabaseTableFieldData d : fieldList) {
								String propName = getPropNameForColumn(psiClass, d);
								if (null != propName) {
									if (selectList.length() > 0) selectList.append(", ");
									selectList.append(d.getName()).append(" as \"").append(propName).append("\"");
								}
							}
							selectList.insert(0, "\nselect ");
							selectList.append("\nfrom ").append(tableData.getName());
							selectList.append("\n").append(getSQLWhere(psiClass, getPrimaryKeyColumns(tableData)));
							String tmp = selectList.toString();
							xmlTag.getValue().setText(tmp);
						}
					}
				}


			}
		}
	}

	private PsiReference getReference(XmlTag xmlTag, String attributeName) {
		XmlAttribute attribute = xmlTag.getAttribute(attributeName);
		XmlAttributeValue element = null;
		if (attribute != null) {
			element = attribute.getValueElement();
		}
		PsiReference psiReference = null;
		if (element != null) {
			psiReference = element.getReference();
		}
		return psiReference;
	}

	private String getSQLWhere(PsiClass psiClass, List<DatabaseTableFieldData> keyCols) {
		StringBuilder sqlWhere = new StringBuilder("");
		for (DatabaseTableFieldData c : keyCols) {
			String propertyName = getPropNameForColumn(psiClass, c);
			if (sqlWhere.length() == 0) {
				sqlWhere.append(" where ");
			} else {
				sqlWhere.append(" and ");
			}
			sqlWhere.append(c.getName()).append(" = #").append(propertyName).append("#");
		}
		return sqlWhere.toString();
	}

	private String getPropNameForColumn(PsiClass psiClass, DatabaseTableFieldData c) {

		// look for a @column on a getter
		for (PsiMethod m : psiClass.getMethods()) {
			if (m.getName().startsWith("get")) {
				PsiDocComment docComment = m.getDocComment();
				if (docComment != null) {
					PsiDocTag psiDocTag = docComment.findTagByName("column");
					if (null != psiDocTag) {
						if (psiDocTag.getValueElement().getText().trim().equalsIgnoreCase(c.getName())) {
							return methodNameToPropertyName(m.getName());
						}
					}
				}
			}
		}

		// look for a matching name
		for (PsiMethod m : psiClass.getMethods()) {
			if (m.getName().startsWith("get")) {
				if (m.getName().substring(3).equalsIgnoreCase(c.getName())) {
					return methodNameToPropertyName(m.getName());
				}
			}
		}
		return null;
	}

	private String methodNameToPropertyName(String methodName) {
		String returnValue = methodName.substring(3);

		// if the second character is upper case, we just return the name unscathed
		if (Character.isUpperCase(returnValue.charAt(1))) return returnValue;

		// make char #1 lower case, and attach the rest
		return returnValue.substring(0, 1).toLowerCase() + returnValue.substring(1);

	}

	protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
		if (file instanceof XmlFile && element instanceof XmlTag) {
			XmlTag xmlTag = (XmlTag) element;
			if (xmlTag.getName().equals("select") && xmlTag.getValue().getText().trim().length() == 0) {   // empty select
				if (xmlTag.getAttributeValue("resultMap") != null) {	  //resultMap included
					DomElement domElement = DomManager.getDomManager(project).getDomElement(xmlTag);   //Sql Map file
					if (domElement != null && domElement instanceof Select) {
						return true;
					}
				}
				if (xmlTag.getAttributeValue("resultClass") != null) {
					return true;
				}

			}
		}
		return false;
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
