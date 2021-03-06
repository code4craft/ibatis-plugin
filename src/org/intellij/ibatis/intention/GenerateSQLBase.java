package org.intellij.ibatis.intention;

import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.*;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.apache.velocity.VelocityContext;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.ParameterMap;
import org.intellij.ibatis.facet.IbatisFacetConfiguration;
import org.intellij.ibatis.facet.SelectKeyType;
import org.intellij.ibatis.provider.TableColumnReferenceProvider;
import org.intellij.ibatis.util.IbatisUtil;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenerateSQLBase extends PsiIntentionBase {
    static Map<Integer, String> jdbcTypeNameMap = new HashMap<Integer, String>();

	static {
        populateTypeMap();
    }

    protected static void populateTypeMap() {
        Field[] fields = Types.class.getFields();
        for (Field f : fields) {
            try {
                jdbcTypeNameMap.put(f.getInt(null), f.getName());
            } catch (IllegalAccessException e) {
                // let's just ignore this, OK?
            }
        }
    }

    /**
     * Common logic to check if an intention is available (use from isAvailable)
     *
     * @param project    - pass through
     * @param file       - pass through
     * @param element    - pass through
     * @param tagName    - what tag is this intention for?
     * @param tagClass   - what is the class of this tag?
     * @param attributes - array of attribute names, one of these must be present
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
                for (String attrib : attributes) {
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

    protected static String buildWhere(List<DatabaseTableFieldData> fieldList, PsiClass psiClass) {
        StringBuilder where = new StringBuilder("");
       
 
        for (DatabaseTableFieldData d : fieldList) {
            String propName = TableColumnReferenceProvider.getPropNameForColumn(psiClass, d);
            if (null != propName) {
                if (d.isPrimary()) {
                    if (where.length() > 0) {
                        where.append(" and ");
                    }
                    where.append(d.getName()).append(" = #").append(propName).append(":").append(jdbcTypeNameMap.get(d.getJdbcType())).append("#");
                }
            }
        }
        return where.toString();
    }

    protected static void buildSqlUpdate(PsiElement element, PsiClass parameterClass) {
        // if any parameters are null, just give up.
        DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(parameterClass);
        if (null == element || null == parameterClass || null == tableData) return;

        // Now we have the table meta-data and the class meta-data so
        // we can build our update statement
		IbatisFacetConfiguration conf = IbatisUtil.getConfig(element);
		if(conf == null){
			conf = IbatisUtil.getConfig(parameterClass);
		}
		VelocityContext context = new VelocityContext();
		context.put("tableData", tableData);

		List<DatabaseTableFieldData> fieldList = tableData.getColumns();
		context.put("fieldList", fieldList);
		context.put("tableName", tableData.getName());
//		StringBuilder updateStatement = new StringBuilder("\nupdate ").append(tableData.getName()).append(" set ");
        StringBuilder fieldsToUpdate = new StringBuilder("");
		context.put("fieldsToUpdate", fieldsToUpdate);
		
		String where = buildWhere(fieldList, parameterClass);
		context.put("where", where);

		for (DatabaseTableFieldData field : fieldList) {
            String propName = TableColumnReferenceProvider.getPropNameForColumn(parameterClass, field);
            if (null != propName) {
                if (fieldsToUpdate.length() > 0) {
                    fieldsToUpdate.append(", ");
                }
                fieldsToUpdate.append(field.getName()).append(" = #").append(propName).append(":").append(jdbcTypeNameMap.get(field.getJdbcType())).append("#");
            }
        }
        if (fieldsToUpdate.length() > 0) {
            // OK, build the SQL statement...
            XmlTag xmlTag = (XmlTag) element;
			try {
				xmlTag.getValue().setText(IbatisUtil.evaluateVelocityTemplate(context, conf.updateTemplate));
			} catch (Exception e) {
				// bummer. bad template == no statement.
			}
		}
    }

    protected static void buildDelete(PsiElement element, PsiClass parameterClass) {
        if (null != parameterClass) {
			IbatisFacetConfiguration conf = IbatisUtil.getConfig(element);
			if(conf == null){
				conf = IbatisUtil.getConfig(parameterClass);
			}
			VelocityContext context = new VelocityContext();
			DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(parameterClass);
			context.put("tableData", tableData);
			if (null != tableData) {
                List<DatabaseTableFieldData> fieldList = tableData.getColumns();
				context.put("fieldList", fieldList);

				// OK, now we have the table meta-data and the class meta-data.
                // now we can build our delete statement
				String tableName = tableData.getName();
				context.put("tableName", tableName);

                String where = buildWhere(fieldList, parameterClass);
				context.put("where", where);

				if (where.length() > 0) {
                    // OK, build the SQL statement...
                    XmlTag xmlTag = (XmlTag) element;
					try {
						xmlTag.getValue().setText(IbatisUtil.evaluateVelocityTemplate(context, conf.deleteTemplate));
					} catch (Exception e) {
						// bad template? nevermind.
					}
				}
            }
        }
    }

	protected static String getSelectKey(
		PsiClass parameterClass,
		IbatisFacetConfiguration conf,
		List<DatabaseTableFieldData> fieldList
	) {

		if(!isSelectKeyNeeded(conf)) return "";

		VelocityContext context = new VelocityContext();
		List<String> keyFieldList = new ArrayList<String>();
		for(DatabaseTableFieldData field : fieldList){
			if(field.isPrimary()){
				String propName = TableColumnReferenceProvider.getPropNameForColumn(parameterClass, field);
				keyFieldList.add(propName);
				context.put("keyFieldProp", propName);
				context.put("keyField", field.getName());
				System.out.println("keyFieldProp = " + propName);
			}
		}

		context.put("keyFieldList", keyFieldList);
		context.put("fieldList", fieldList);
		context.put("fullparamClass", parameterClass.getQualifiedName());
		context.put("paramClass", parameterClass.getName());
		try {
			return IbatisUtil.evaluateVelocityTemplate(context, conf.selectKeyTemplate);
		} catch (Exception e) {
			Messages.showErrorDialog(
				"Encountered a problem creating the selectKey element.\n" +
				"The error message is " + e.getLocalizedMessage() + "\n" +
				"The select key element will be empty.",
				"Error"
			);
			// hm, crap.
			return "";
		}
	}

	protected static boolean isSelectKeyNeeded(IbatisFacetConfiguration conf){
		return isPostInsertSelectKey(conf) || isPreInsertSelectKey(conf);
	}

	protected static boolean isPostInsertSelectKey(IbatisFacetConfiguration conf){
		return !(null == conf || null == conf.selectKeyType) && conf.selectKeyType == SelectKeyType.postInsert;
	}

	protected static boolean isPreInsertSelectKey(IbatisFacetConfiguration conf){
		return !(null == conf || null == conf.selectKeyType) && conf.selectKeyType == SelectKeyType.preInsert;
	}

	protected static void buildInsert(PsiElement element, PsiClass parameterClass, IbatisFacetConfiguration conf) {
		VelocityContext context = new VelocityContext();

		DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(parameterClass);
		context.put("tableData", tableData);

		if (null != tableData) {
            List<DatabaseTableFieldData> fieldList = tableData.getColumns();
            // OK, now we have the table meta-data and the class meta-data.
            // now we can build our insert statement
			String tableName = tableData.getName();
			context.put("tableName", tableName);
            StringBuilder insertList = new StringBuilder("");
            StringBuilder valueList = new StringBuilder("");
			String keyProperty = null;
			String keyResultType = null;
			boolean insertKeyField = isPreInsertSelectKey(conf);

			context.put("insertKeyField", insertKeyField);

			for (DatabaseTableFieldData d : fieldList) {
				String propName = TableColumnReferenceProvider.getPropNameForColumn(parameterClass, d);
				if(d.isPrimary()){
					keyProperty = propName;
					keyResultType = d.getType();
					if(insertKeyField){
						if (null != propName) {
							if (insertList.length() > 0) {
								insertList.append(", ");
							}
							if (valueList.length() > 0) {
								valueList.append(", ");
							}
							insertList.append(d.getName());
							valueList.append("#").append(propName).append(":").append(jdbcTypeNameMap.get(d.getJdbcType())).append("#");
						}
					}
				}else{
					if (null != propName) {
						if (insertList.length() > 0) {
							insertList.append(", ");
						}
						if (valueList.length() > 0) {
							valueList.append(", ");
						}
						insertList.append(d.getName());
						valueList.append("#").append(propName).append(":").append(jdbcTypeNameMap.get(d.getJdbcType())).append("#");
					}
				}
			}

			context.put("valueList", valueList);
			context.put("insertList", insertList);

			if (insertList.length() > 0) {
                // OK, build the SQL statement...
                XmlTag xmlTag = (XmlTag) element;
				String insertString;
				try {
					insertString = IbatisUtil.evaluateVelocityTemplate(context, conf.insertTemplate);
				} catch (Exception e) {
					// dang, kaboom
					return;
				}

				if(isPreInsertSelectKey(conf)){
					PsiElement selectKey = xmlTag.createChildTag("selectKey", "", "\n" + getSelectKey(parameterClass, conf, fieldList), false).copy();
					try {
						XmlTag selectKeyTag = (XmlTag) selectKey;
						XmlAttribute attribute = selectKeyTag.getAttribute("xmlns");
						if (attribute != null) {
							attribute.delete();
						}
						selectKeyTag.setAttribute("keyProperty", keyProperty);
						selectKeyTag.setAttribute("type", "pre");
						selectKeyTag.setAttribute("resultClass", keyResultType);
						xmlTag.getValue().setText(insertString);
						xmlTag.add(selectKey);
						PsiElement[] psiElements = xmlTag.getChildren();
						PsiElement textElement = null;
						PsiElement selectKeyElement = null;
						for (int i=0; i < psiElements.length; i++){
							PsiElement pe = psiElements[i];
							if(pe instanceof XmlTag){
								XmlTag xe = (XmlTag) pe;
								if("selectKey".equals(xe.getName())){
									selectKeyElement = psiElements[i];
									System.out.println("selectKey is " + i);
								}
							}
							if(pe instanceof XmlText){
								textElement = psiElements[i];
								System.out.println("text is " + i);
							}
						}
						PsiElement temp;
						if (textElement != null && selectKeyElement != null) {
							temp = textElement.copy();
							textElement.replace(selectKeyElement);
							selectKeyElement.replace(temp);
						}
					} catch (IncorrectOperationException e) {
						System.out.println("bang");
					}

				}else if (isPostInsertSelectKey(conf)){
					PsiElement selectKey = xmlTag.createChildTag("selectKey", "", "\n" + getSelectKey(parameterClass, conf, fieldList), false).copy();
					try {
						XmlTag selectKeyTag = (XmlTag) selectKey;
						XmlAttribute attribute = selectKeyTag.getAttribute("xmlns");
						if (attribute != null) {
							attribute.delete();
						}
						selectKeyTag.setAttribute("keyProperty", keyProperty);
						selectKeyTag.setAttribute("type", "post");
						selectKeyTag.setAttribute("resultClass", keyResultType);
						xmlTag.getValue().setText(insertString);
						xmlTag.add(selectKey);
					} catch (IncorrectOperationException e) {
						e.printStackTrace();
					}
				}else{
					xmlTag.getValue().setText(insertString);
				}
            }
        }
	}

	protected static void buildInsert(PsiElement element, PsiClass parameterClass) {
		IbatisFacetConfiguration conf = IbatisUtil.getConfig(element);

		if(null == conf){
			conf = IbatisUtil.getConfig(parameterClass);
		}

		if(null != conf) buildInsert(element, parameterClass, conf);

	}

    protected static void buildSelect(PsiElement element, PsiClass resultClass) {
		IbatisFacetConfiguration conf = IbatisUtil.getConfig(element);
		if(null == conf){
			conf = IbatisUtil.getConfig(resultClass);
		}
        XmlTag xmlTag = (XmlTag) element;

		VelocityContext context = new VelocityContext();

		DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(resultClass);
		context.put("tableData", tableData);

		if (tableData == null) return;

        List<DatabaseTableFieldData> fieldList = tableData.getColumns();
		context.put("fieldList", fieldList);

		StringBuilder selectList = new StringBuilder("");
        for (DatabaseTableFieldData d : fieldList) {
            String propName = TableColumnReferenceProvider.getPropNameForColumn(resultClass, d);
            if (null != propName) {
                if (selectList.length() > 0) selectList.append(", ");
                selectList.append(d.getName()).append(" as \"").append(propName).append("\"");
            }
        }
		context.put("selectList", selectList);

		String tableName = tableData.getName();
		context.put("tableName", tableName);

		String where = buildWhere(fieldList, resultClass);
		context.put("where", where);

		String selectStatement;

		String template = conf.selectTemplate;

		try {
			selectStatement = IbatisUtil.evaluateVelocityTemplate(context, template);
		} catch (Exception e) {
			// hm, pooched it, punt
			return;
		}

		xmlTag.getValue().setText(selectStatement);

	}

    @SuppressWarnings({"ReturnOfNull"})
    protected PsiElement getXmlAttributeAsElement(XmlTag xmlTag, String attributeName) {

        if (null == xmlTag || null == attributeName || attributeName.trim().length() == 0) {
            return null;
        }

        PsiReference reference = getReference(xmlTag, attributeName);

        if (null == reference) {
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
        PsiClass resultClass = resultMap.getClazz().getValue();
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

                // Add the primary key selection - the user can cut what they don't want...
                sqlWhere = buildWhere(tableData.getColumns(), resultClass);
                xmlTag.getValue().setText(sql + sqlWhere);
            }
        } else {  //table name is empty
            xmlTag.getValue().setText("\nselect " + selectList + " from\n");
			}
	}

	protected void createInsertTagContents(Project project, PsiElement insertElement) {
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
