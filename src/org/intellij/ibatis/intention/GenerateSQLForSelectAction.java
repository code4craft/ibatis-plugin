package org.intellij.ibatis.intention;

import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.dom.sqlMap.TypeAlias;
import static org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider.getPsiClass;
import org.intellij.ibatis.provider.TableColumnReferenceProvider;
import static org.intellij.ibatis.provider.TableColumnReferenceProvider.getDatabaseTableData;
import static org.intellij.ibatis.provider.TableColumnReferenceProvider.getPrimaryKeyColumns;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
							PsiClass psiClass = getPsiClass(element, resultMap.getClazz().getValue());
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

				// todo: what if it isn't a type alias?
				XmlTag typeAliasTag; // this is the typeAlias tag
				DomElement typeAliasElement = null;
				if(psiElement instanceof XmlTag) {
					typeAliasTag = ((XmlTag) psiElement);
					typeAliasElement = DomManager.getDomManager(project).getDomElement(typeAliasTag);
				}

				// this is the DOM element of the type alias tag

				if (typeAliasElement != null && typeAliasElement instanceof TypeAlias) {
					TypeAlias typeAlias = (TypeAlias) typeAliasElement;
					PsiClass value = typeAlias.getType().getValue();
					if (null != value) {
						PsiClass psiClass = getPsiClass(psiElement, value.getQualifiedName());
						DatabaseTableData tableData = getDatabaseTableData(value);
						if (null != tableData) {

							List<DatabaseTableFieldData> fieldList = tableData.getFields();
							StringBuilder selectList = new StringBuilder("");
							for (DatabaseTableFieldData d : fieldList) {
								String propName = TableColumnReferenceProvider.getPropNameForColumn(psiClass, d);
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
				}else if (psiElement instanceof PsiClass){
					PsiClass x = (PsiClass) psiElement;
					DatabaseTableData tableData = getDatabaseTableData(x);
					if (null != tableData) {

						List<DatabaseTableFieldData> fieldList = tableData.getFields();
						StringBuilder selectList = new StringBuilder("");
						for (DatabaseTableFieldData d : fieldList) {
							String propName = TableColumnReferenceProvider.getPropNameForColumn(x, d);
							if (null != propName) {
								if (selectList.length() > 0) selectList.append(", ");
								selectList.append(d.getName()).append(" as \"").append(propName).append("\"");
							}
						}
						selectList.insert(0, "\nselect ");
						selectList.append("\nfrom ").append(tableData.getName());
						selectList.append("\n").append(getSQLWhere(x, getPrimaryKeyColumns(tableData)));
						String tmp = selectList.toString();
						xmlTag.getValue().setText(tmp);
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
			String propertyName = TableColumnReferenceProvider.getPropNameForColumn(psiClass, c);
			if (sqlWhere.length() == 0) {
				sqlWhere.append(" where ");
			} else {
				sqlWhere.append(" and ");
			}
			sqlWhere.append(c.getName()).append(" = #").append(propertyName).append(":").append(jdbcTypeNameMap.get(c.getJdbcType())).append("#");
		}
		return sqlWhere.toString();
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
