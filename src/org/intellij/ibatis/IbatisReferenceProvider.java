package org.intellij.ibatis;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.codeInsight.completion.CompletionUtil;
import org.intellij.ibatis.provider.*;
import org.intellij.ibatis.util.IbatisBundle;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;

/**
 * project component to register all reference provider
 *
 * @author jacky
 */
public class IbatisReferenceProvider implements ProjectComponent {
    private ReferenceProvidersRegistry registry;
    private NamespaceFilter ibatisSqlMapConfigNamespaceFilter;
    private NamespaceFilter ibatisSqlMapNamespaceFilter;
    private NamespaceFilter ibatisAbatorNamespaceFilter;

    public IbatisReferenceProvider(Project project) {
        ibatisSqlMapConfigNamespaceFilter = new NamespaceFilter(IbatisConstants.CONFIGURATION_DTDS);
        ibatisSqlMapNamespaceFilter = new NamespaceFilter(IbatisConstants.SQLMAP_DTDS);
        ibatisAbatorNamespaceFilter = new NamespaceFilter(IbatisConstants.ABATOR_DTDS);
        registry = ReferenceProvidersRegistry.getInstance(project);
    }

    public void initComponent() {
        //statement id reference
        registry.registerReferenceProvider(new SqlClientElementFilter(), PsiLiteralExpression.class, new StatementIdReferenceProvider());
        registry.registerDocTagReferenceProvider(new String[]{"table"}, new JavadocTagFilter("table"), true, new JavadocTableNameReferenceProvider());
        registry.registerDocTagReferenceProvider(new String[]{"column"}, new JavadocTagFilter("column"), true, new JavadocTableColumnReferenceProvider());
//        registry.registerReferenceProvider(TrueFilter.INSTANCE, XmlTag.class, new InlineParameterReferenceProvider());
        JavaClassReferenceProvider classReferenceProvider = new JavaClassReferenceProvider();
        IbatisClassShortcutsReferenceProvider classShortcutsReferenceProvider = new IbatisClassShortcutsReferenceProvider();
        FieldAccessMethodReferenceProvider fieldAccessMethodReferenceProvider = new FieldAccessMethodReferenceProvider();
        ResultMapReferenceProvider resultMapReferenceProvider = new ResultMapReferenceProvider();
        ParameterMapReferenceProvider parameterMapReferenceProvider = new ParameterMapReferenceProvider();
        SqlReferenceProvider sqlReferenceProvider = new SqlReferenceProvider();
        TableColumnReferenceProvider tableColumnReferenceProvider = new TableColumnReferenceProvider();
        CacheModelReferenceProvider cacheModelReferenceProvider = new CacheModelReferenceProvider();
        CacheModelTypeReferenceProvider cacheModelTypeReferenceProvider = new CacheModelTypeReferenceProvider();
        CacheModelStatementReferenceProvider modelStatementReferenceProvider = new CacheModelStatementReferenceProvider();
        CacheModelPropertyReferenceProvider modelPropertyReferenceProvider = new CacheModelPropertyReferenceProvider();
        CacheModelMemoryTypeReferenceProvider cacheModelMemoryTypeReferenceProvider = new CacheModelMemoryTypeReferenceProvider();

        ParameterJdbcTypeReferenceProvider jdbcTypeReferenceProvider = new ParameterJdbcTypeReferenceProvider();
        //Java class
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapConfigNamespaceFilter, "typeAlias", new String[]{"type"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapConfigNamespaceFilter, "parameter", new String[]{"javaType"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "typeAlias", new String[]{"type"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameter", new String[]{"typeHandler"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"typeHandler"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "discriminator", new String[]{"typeHandler"}, classReferenceProvider);
        //iBATIS class with shortcuts and type alias
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameterMap", new String[]{"class"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameter", new String[]{"javaType"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "resultMap", new String[]{"class"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "statement", new String[]{"parameterClass", "resultClass"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "insert", new String[]{"parameterClass"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "update", new String[]{"parameterClass"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "delete", new String[]{"parameterClass"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "select", new String[]{"parameterClass", "resultClass"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "procedure", new String[]{"parameterClass", "resultClass"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "selectKey", new String[]{"resultClass"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "paramter", new String[]{"javaType"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"javaType"}, classShortcutsReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "discriminator", new String[]{"javaType"}, classShortcutsReferenceProvider);
        //field access method reference
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "resultMap", new String[]{"groupBy"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "discriminator", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameter", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isEqual", new String[]{"property", "compareProperty"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isNotEqual", new String[]{"property", "compareProperty"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isGreaterThan", new String[]{"property", "compareProperty"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isGreaterEqual", new String[]{"property", "compareProperty"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isLessThan", new String[]{"property", "compareProperty"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isLessEuqal", new String[]{"property", "compareProperty"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isPropertyAvailable", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isNull", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isNotNull", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isEmpty", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "isNotEmpty", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "iterate", new String[]{"property"}, fieldAccessMethodReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "selectKey", new String[]{"keyProperty"}, fieldAccessMethodReferenceProvider);
        //result map reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "statement", new String[]{"resultMap"}, resultMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "select", new String[]{"resultMap"}, resultMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "procedure", new String[]{"resultMap"}, resultMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"resultMap"}, resultMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameter", new String[]{"resultMap"}, resultMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "resultMap", new String[]{"extends"}, resultMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "subMap", new String[]{"resultMap"}, resultMapReferenceProvider);
        //parameter map reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "statement", new String[]{"parameterMap"}, parameterMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "insert", new String[]{"parameterMap"}, parameterMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "update", new String[]{"parameterMap"}, parameterMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "delete", new String[]{"parameterMap"}, parameterMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "select", new String[]{"parameterMap"}, parameterMapReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "procedure", new String[]{"parameterMap"}, parameterMapReferenceProvider);
        //SQL reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "include", new String[]{"refid"}, sqlReferenceProvider);
        //cache model reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "select", new String[]{"cacheModel"}, cacheModelReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "statement", new String[]{"cacheModel"}, cacheModelReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "procedure", new String[]{"cacheModel"}, cacheModelReferenceProvider);
        //cache model type reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "cacheModel", new String[]{"type"}, cacheModelTypeReferenceProvider);
        //cache model statement reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "flushOnExecute", new String[]{"statement"}, modelStatementReferenceProvider);
        //cache model property reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "property", new String[]{"name"}, modelPropertyReferenceProvider);
        //cache model memory type reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "property", new String[]{"value"}, cacheModelMemoryTypeReferenceProvider);
        //table column reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"column"}, tableColumnReferenceProvider);
        //parameter jdbc type reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameter", new String[]{"jdbcType"}, jdbcTypeReferenceProvider);
        // jdbcType reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"jdbcType"}, jdbcTypeReferenceProvider);
        //Abator
        registerXmlAttributeValueReferenceProvider(ibatisAbatorNamespaceFilter, "columnOverride", new String[]{"jdbcType"}, jdbcTypeReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisAbatorNamespaceFilter, "columnOverride", new String[]{"typeHandler"}, jdbcTypeReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisAbatorNamespaceFilter, "columnOverride", new String[]{"javaType"}, classShortcutsReferenceProvider);
        // CompletionData registration
//        SelectorSymbolCompletionData selectorSymbolCompletionData = new SelectorSymbolCompletionData(null);
//        CompletionUtil.registerCompletionData(StdFileTypes.XML, selectorSymbolCompletionData);
    }

    public void disposeComponent() {
    }

    @NotNull public String getComponentName() {
        return IbatisBundle.message("ibatis.referenceprovider.project.component.name");
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    private void registerXmlAttributeValueReferenceProvider(NamespaceFilter namespaceFilter, String tagName, String attributeNames[], PsiReferenceProvider referenceProvider) {
        registry.registerXmlAttributeValueReferenceProvider(attributeNames, new ScopeFilter(new ParentElementFilter(new AndFilter(new ClassFilter(XmlTag.class), new AndFilter(new OrFilter(new TextFilter(tagName)), namespaceFilter)), 2)), referenceProvider);
    }


}
