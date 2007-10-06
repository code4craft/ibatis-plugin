/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package org.intellij.ibatis.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

/**
 * iBATIS facet configuration
 *
 * @author Jacky
 */
public class IbatisFacetConfiguration implements FacetConfiguration, ModificationTracker {
    private long myModificationCount;
    public String dataSourceName;
    public String sqlMapSuffix;
    public String sqlMapPackage;
    public String beanPackage;
	public String sqlMapTemplate;
	public String beanTemplate;

	public IbatisFacetConfiguration() {
		resetToDefaultTemplates();
	}

	public FacetEditorTab[] createEditorTabs(final FacetEditorContext editorContext, final FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{
                new IbatisConfigurationTab(editorContext, this)
        };
    }

    public void readExternal(Element element) throws InvalidDataException {
        dataSourceName = JDOMExternalizer.readString(element, "datasourceName");
        sqlMapSuffix = JDOMExternalizer.readString(element, "sqlMapSuffix");
        sqlMapPackage = JDOMExternalizer.readString(element, "sqlMapPackage");
        beanPackage = JDOMExternalizer.readString(element, "beanPackage");
	}

    public void writeExternal(Element element) throws WriteExternalException {
        JDOMExternalizer.write(element, "datasourceName", dataSourceName);
        JDOMExternalizer.write(element, "sqlMapSuffix", sqlMapSuffix);
        JDOMExternalizer.write(element, "sqlMapPackage", sqlMapPackage);
        JDOMExternalizer.write(element, "beanPackage", beanPackage);
    }

    public long getModificationCount() {
        return myModificationCount;
    }

    public void setModified() {
        myModificationCount++;
    }

	public void resetToDefaultTemplates(){
		resetSqlMapTemplate();
		resetBeanTemplate();
	}

	public void resetBeanTemplate() {
		beanTemplate = "package $package;\n" +
			"\n" +
			"/**\n" +
			" * model class generate from table $tableName\n" +
			" *\n" +
			" *@table $tableName\n" +
			" */\n" +
			"public class $name\n" +
			"{\n" +
			"#foreach( $field in $fieldList)\n" +
			"    private ${field.type} ${field.name};\n" +
			"#end\n" +
			"\n" +
			"#foreach ($field in $fieldList)\n" +
			"    /**\n" +
			"     *\n" +
			"     *@return\n" +
			"     */\n" +
			"    public  $field.type ${field.getGetterMethodName()}()\n" +
			"    {\n" +
			"        return ${field.name};\n" +
			"    }\n" +
			"\n" +
			"    /**\n" +
			"     *\n" +
			"     * @param ${field.name}\n" +
			"     * @column ${field.columnName}\n" +
			"     */\n" +
			"    public void ${field.getSetterMethodName()}(${field.type} ${field.name})\n" +
			"    {\n" +
			"         this.${field.name} = ${field.name};\n" +
			"    }\n" +
			"#end\n" +
			"}";
	}

	public void resetSqlMapTemplate() {
		sqlMapTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<!DOCTYPE sqlMap PUBLIC \"-//iBATIS.com//DTD SQL Map 2.0//EN\"\n" +
			"\t\"http://ibatis.apache.org/dtd/sql-map-2.dtd\">\n" +
			"\n" +
			"<sqlMap namespace=\"$className\">\n" +
			"\n" +
			"    <typeAlias alias=\"$className\" type=\"$FQCN\" />\n" +
			"\n" +
			"</sqlMap>";
	}
}