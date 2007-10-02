package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * table column reference provider
 */
public class TableColumnReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(final PsiElement psiElement) {
        final XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        final XmlTag xmlTag = (XmlTag) xmlAttributeValue.getParent().getParent().getParent();   //result or parameter tag
        if (xmlTag.getAttributeValue("class") != null) {
            final PsiClass psiClass = IbatisClassShortcutsReferenceProvider.getPsiClass(xmlAttributeValue, xmlTag.getAttributeValue("class"));
            if (psiClass != null) {
                DatabaseTableData tableData = getDatabaseTableData(psiClass);
                if (tableData != null) {
                    return new PsiReference[]{new TableColumnReference(xmlAttributeValue, tableData)};
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }

    /**
     * Get jdbc data for a table that the class maps is mapped to
     *
	 * If the class has a javadoc @table tag, we use that.
	 *
	 * If not, we'll look in the datasource for a table that matches the class name
	 *
	 * If we still don't find a match, we'll look in the datasource for a table that matches the
	 * class name with an "s" appended to it.
	 *
	 * If we *still* don't find a match, we give up and return null.
	 *
     * @param psiClass PsiClass
     * @return tableData object
     */
    @Nullable
    public static DatabaseTableData getDatabaseTableData(PsiClass psiClass) {
		DataSource dataSource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(psiClass);
		List<DatabaseTableData> tables = dataSource.getTables();
		String name = psiClass.getName();

		PsiDocComment docComment = psiClass.getDocComment();
		if (docComment != null && docComment.findTagByName("table") != null) {
			// there is a @table tag, look for it and return what you find.
			PsiDocTag tableTag = docComment.findTagByName("table");
			String tableName = tableTag.getValueElement().getText().trim();
			if (StringUtil.isNotEmpty(tableName)) {
				for (DatabaseTableData table : tables) {
					if (table.getName().replaceAll("\\w*\\.","" ).equalsIgnoreCase(tableName)) {
						return table;
					}
				}
			}
        }

		// OK, if we got here, we need to look for the class name
		for (DatabaseTableData table : tables) {
			if (table.getName().replaceAll("\\w*\\.","" ).equalsIgnoreCase(name)) {
				return table;
			}
		}

		// OK, if we got here, we need to look for the class name pluralized
		if(null != name){
			String tmp = StringUtil.pluralize(name);
			for (DatabaseTableData table : tables) {
				if (table.getName().replaceAll("\\w*\\.","" ).equalsIgnoreCase(tmp)) {
					return table;
				}
			}
		}

		// by the time we get here...I give up
		return null;

	}

    /**
     * get tableFieldData in psiMethod
     *
     * @param psiMethod PsiMethod object
     * @return DatabaseTableFieldData object
     */
    @Nullable
    public static DatabaseTableFieldData getDatabaseTableFieldData(PsiMethod psiMethod) {
        PsiDocComment docComment = psiMethod.getDocComment();
        if (docComment != null && docComment.findTagByName("column") != null) {
            PsiDocTag psiDocTag = docComment.findTagByName("column");
            String columnName = psiDocTag.getValueElement().getText().trim();
            if (StringUtil.isNotEmpty(columnName)) {
                PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);
                if (psiClass != null) {
                    DatabaseTableData tableData = getDatabaseTableData(psiClass);
                    if (tableData != null)
                        return tableData.findColumnByName(columnName);
                }
            }
        }
        return null;
    }

    public class TableColumnReference extends XmlAttributeValuePsiReference {
        private DatabaseTableData databaseTableData;

        public TableColumnReference(XmlAttributeValue xmlAttributeValue, DatabaseTableData databaseTableData) {
            super(xmlAttributeValue);
            this.databaseTableData = databaseTableData;
        }

        public Object[] getVariants() {
            List<Object> fieldList = new ArrayList<Object>();
            List<DatabaseTableFieldData> fields = databaseTableData.getFields();
            for (DatabaseTableFieldData field : fields) {
                if (field.isPrimary()) {       //primary key
                    fieldList.add(LookupValueFactory.createLookupValueWithHint(field.getName(), IbatisConstants.DATABASE_PK_FIELD, getJdbcTypeName(field.getJdbcType())));
                } else {   //common column
                    fieldList.add(LookupValueFactory.createLookupValueWithHint(field.getName(), IbatisConstants.DATABASE_COMMON_FIELD, getJdbcTypeName(field.getJdbcType())));
                }
            }
            return fieldList.toArray();
        }

        public boolean isSoft() {
            return true;
        }
    }

    /**
     * get jdbc type name according type id
     *
     * @param jdbcType jdbc type id
     * @return type name
     */
    public static String getJdbcTypeName(int jdbcType) {
        String name = "";
        switch (jdbcType) {
            case Types.BIT:
                name = "BIT";
                break;
            case Types.TINYINT:
                name = "TINYINT";
                break;
            case Types.SMALLINT:
                name = "SMALLINT";
                break;
            case Types.INTEGER:
                name = "INTEGER";
                break;
            case Types.BIGINT:
                name = "BIGINT";
                break;
            case Types.FLOAT:
                name = "FLOAT";
                break;
            case Types.REAL:
                name = "REAL";
                break;
            case Types.DOUBLE:
                name = "DOUBLE";
                break;
            case Types.NUMERIC:
                name = "NUMERIC";
                break;
            case Types.DECIMAL:
                name = "DECIMAL";
                break;
            case Types.CHAR:
                name = "CHAR";
                break;
            case Types.VARCHAR:
                name = "VARCHAR";
                break;
            case Types.LONGVARCHAR:
                name = "LONGVARCHAR";
                break;
            case Types.DATE:
                name = "DATE";
                break;
            case Types.TIME:
                name = "TIME";
                break;
            case Types.TIMESTAMP:
                name = "TIMESTAMP";
                break;
            case Types.BINARY:
                name = "BINARY";
                break;
            case Types.VARBINARY:
                name = "VARBINARY";
                break;
            case Types.LONGVARBINARY:
                name = "LONGVARBINARY";
                break;
            case Types.NULL:
                name = "NULL";
                break;
            case Types.OTHER:
                name = "OTHER";
                break;
            case Types.JAVA_OBJECT:
                name = "JAVA_OBJECT";
                break;
            case Types.DISTINCT:
                name = "DISTINCT";
                break;
            case Types.STRUCT:
                name = "STRUCT";
                break;
            case Types.ARRAY:
                name = "ARRAY";
                break;
            case Types.BLOB:
                name = "BLOB";
                break;
            case Types.CLOB:
                name = "CLOB";
                break;
            case Types.REF:
                name = "REF";
                break;
            case Types.DATALINK:
                name = "DATALINK";
                break;
        }
        return name;
    }

	public static List<DatabaseTableFieldData> getPrimaryKeyColumns(DatabaseTableData tableData){
		List<DatabaseTableFieldData> l = new ArrayList<DatabaseTableFieldData>();
		for(DatabaseTableFieldData f : tableData.getFields()){
			if(f.isPrimary()) l.add(f);
		}
		return l;
	}

	private static String methodNameToPropertyName(String methodName) {
		String returnValue = methodName.substring(3);

		// if the second character is upper case, we just return the name unscathed
		if (Character.isUpperCase(returnValue.charAt(1))) return returnValue;

		// make char #1 lower case, and attach the rest
		return returnValue.substring(0, 1).toLowerCase() + returnValue.substring(1);

	}

	public static String getPropNameForColumn(PsiClass psiClass, DatabaseTableFieldData c) {

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

		// look for a "mangled name"
		// this maps columns NAMED_LIKE_THIS to fields namedLikeThis
		String mangledColumnName;
		mangledColumnName = mangleUnderscore(c.getName(), false);
		for (PsiMethod m : psiClass.getMethods()) {
			if (m.getName().startsWith("get")) {
				if (m.getName().substring(3).equalsIgnoreCase(mangledColumnName)) {
					return methodNameToPropertyName(m.getName());
				}
			}
		}

		mangledColumnName = mangleUnderscore(c.getName(), true);
		for (PsiMethod m : psiClass.getMethods()) {
			if (m.getName().startsWith("get")) {
				if (m.getName().substring(3).equalsIgnoreCase(mangledColumnName)) {
					return methodNameToPropertyName(m.getName());
				}
			}
		}
		return null;
	}

	private static String mangleUnderscore(String testColumnName, boolean allowLowerCaseSingle) {
		String[] strings = testColumnName.toLowerCase().split("_");
		if(!allowLowerCaseSingle){
			if(strings[0].length() == 1){
				strings[0] = strings[0].toUpperCase();
			}
		}
		for (int i = 1; i < strings.length; i++) {
			strings[i] = StringUtil.firstLetterToUpperCase(strings[i]);
		}
		return StringUtil.join(strings, "");
	}

}
