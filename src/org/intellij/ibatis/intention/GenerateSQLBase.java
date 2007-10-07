package org.intellij.ibatis.intention;

import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.provider.TableColumnReferenceProvider;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.Result;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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

	protected String buildWhere(List<DatabaseTableFieldData> fieldList, PsiClass psiClass) {
		StringBuilder where = new StringBuilder("");

		for (DatabaseTableFieldData d : fieldList) {
			String propName = TableColumnReferenceProvider.getPropNameForColumn(psiClass, d);
			if(null != propName){
				if(d.isPrimary()){
					if(where.length() == 0) {
						where.append(" where ");
					}else{
						where.append(" and ");
					}
					where.append(d.getName()).append(" = #").append(propName).append(":").append(jdbcTypeNameMap.get(d.getJdbcType())).append("#");
				}
			}
		}
		return where.toString();
	}

	protected void buildSqlUpdate(PsiElement element, PsiClass parameterClass) {
		// if any parameters are null, just give up.
		DatabaseTableData tableData = org.intellij.ibatis.provider.TableColumnReferenceProvider.getDatabaseTableData(parameterClass);
		if(null == element || null == parameterClass || null == tableData) return;

		// Now we have the table meta-data and the class meta-data so
		// we can build our update statement

		List<DatabaseTableFieldData> fieldList = tableData.getFields();
		StringBuilder updateStatement = new StringBuilder("\nupdate ").append(tableData.getName()).append(" set ");
		StringBuilder fieldsToUpdate = new StringBuilder("");
		String where = buildWhere(fieldList, parameterClass);

		for (DatabaseTableFieldData field : fieldList) {
			String propName = TableColumnReferenceProvider.getPropNameForColumn(parameterClass, field);
			if(null != propName){
				if(fieldsToUpdate.length() > 0) {
					fieldsToUpdate.append(", ");
				}
				fieldsToUpdate.append(field.getName()).append(" = #").append(propName).append(":").append(jdbcTypeNameMap.get(field.getJdbcType())).append("#");
			}
		}
		if(fieldsToUpdate.length() > 0){
			// ok, build the SQL statement...
			XmlTag xmlTag = (XmlTag) element;
			xmlTag.getValue().setText(updateStatement.append(fieldsToUpdate).append(where).toString());
		}
	}

	protected void buildDelete(PsiElement element, PsiClass parameterClass) {
		if(null != parameterClass){
			DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(parameterClass);
			if(null != tableData){
				List<DatabaseTableFieldData> fieldList = tableData.getFields();

				// ok, now we have the table meta-data and the class meta-data.
				// now we can build our delete statement
				StringBuilder deleteStatement = new StringBuilder("\ndelete from ").append(tableData.getName());
				String where = buildWhere(fieldList, parameterClass);

				if(where.length() > 0){
					// ok, build the SQL statement...
					XmlTag xmlTag = (XmlTag) element;
					xmlTag.getValue().setText(deleteStatement.append(where).toString());
				}
			}
		}
	}

	protected void buildInsert(PsiElement element, PsiClass parameterClass) {
		DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(parameterClass);
		if(null != tableData){
			List<DatabaseTableFieldData> fieldList = tableData.getFields();
			// ok, now we have the table meta-data and the class meta-data.
			// now we can build our insert statement
			StringBuilder insertStatement = new StringBuilder("\ninsert into ").append(tableData.getName());
			StringBuilder insertList = new StringBuilder("");
			StringBuilder valueList = new StringBuilder("");
			for (DatabaseTableFieldData d : fieldList) {
				String propName = TableColumnReferenceProvider.getPropNameForColumn(parameterClass, d);
				if(null != propName){
					if(insertList.length() == 0) {
						insertList.append(" (");
					}else{
						insertList.append(", ");
					}
					if(valueList.length() == 0) {
						valueList.append("\nvalues (");
					}else{
						valueList.append(", ");
					}
					insertList.append(d.getName());
					valueList.append("#").append(propName).append(":").append(jdbcTypeNameMap.get(d.getJdbcType())).append("#");
				}
			}
			if(insertList.length() > 0){
				// ok, build the SQL statement...
				XmlTag xmlTag = (XmlTag) element;
				xmlTag.getValue().setText(insertStatement.append(insertList).append(") ").append(valueList).append(")").toString());
			}
		}
	}

	protected void buildSelect(PsiElement element, PsiClass resultClass) {
		XmlTag xmlTag = (XmlTag) element;
		DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(resultClass);
		if(tableData == null) return;
		List<DatabaseTableFieldData> fieldList = tableData.getFields();
		StringBuilder selectList = new StringBuilder("");
		for (DatabaseTableFieldData d : fieldList) {
			String propName = TableColumnReferenceProvider.getPropNameForColumn(resultClass, d);
			if (null != propName) {
				if (selectList.length() > 0) selectList.append(", ");
				selectList.append(d.getName()).append(" as \"").append(propName).append("\"");
			}
		}
		selectList.insert(0, "\nselect ");
		selectList.append("\nfrom ").append(tableData.getName());
		selectList.append("\n").append(buildWhere(fieldList, resultClass));
		String tmp = selectList.toString();
		xmlTag.getValue().setText(tmp);
	}

	protected PsiElement getXmlAttributeAsElement(XmlTag xmlTag, String attributeName){

		if(null == xmlTag || null == attributeName || attributeName.trim().length() == 0) {
			return null;
		}

		PsiReference reference = getReference(xmlTag, attributeName);

		if(null == reference){
			return null;
		}

		return reference.resolve();

	}

	protected PsiReference getReference(XmlTag xmlTag, String attributeName) {
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

	protected void buildSelectFromResultMap(PsiElement element, ResultMap resultMap) {
		XmlTag xmlTag = (XmlTag) element;
		String className = resultMap.getClazz().getValue();

		if (StringUtil.isNotEmpty(className)) {
			PsiClass resultClass = org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider.getPsiClass(element, resultMap.getClazz().getValue());
			List<Result> resultItemList = resultMap.getResults();
			List<String> columns = new ArrayList<String>();
			for (Result result : resultItemList) {
				String columnName = result.getColumn().getStringValue();
				if (StringUtil.isNotEmpty(columnName))
					columns.add(columnName);
			}
			String selectList = StringUtil.join(columns, ", ");

			String sql;
			String tableName;
			String sqlWhere;

			if (resultClass != null) {  //get table name
				DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(resultClass);
				if (tableData != null) {
					// table name ready
					tableName = tableData.getName();
					sql = "\nselect " + selectList + "\nfrom " + tableName + "\n";

					// Add the pk selection - the user can cut what they don't want...
					sqlWhere = buildWhere(tableData.getFields(), resultClass);
					xmlTag.getValue().setText(sql + sqlWhere);
				}
			} else {  //table name is empty
				xmlTag.getValue().setText("\nselect " + selectList + " from\n");
			}
		}
	}
}
