package org.intellij.ibatis;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.util.XmlUtil;
import com.intellij.openapi.project.Project;
import org.intellij.ibatis.provider.*;
import org.intellij.ibatis.util.IbatisBundle;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * project component to register all reference provider
 *
 * @author jacky
 */
public class IbatisReferenceProvider extends PsiReferenceContributor {
    private NamespaceFilter ibatisSqlMapConfigNamespaceFilter;
    private NamespaceFilter ibatisSqlMapNamespaceFilter;
    private NamespaceFilter ibatisAbatorNamespaceFilter;
    private PsiReferenceRegistrar registrary;

    public IbatisReferenceProvider() {
        ibatisSqlMapConfigNamespaceFilter = new NamespaceFilter(IbatisConstants.CONFIGURATION_DTDS);
        ibatisSqlMapNamespaceFilter = new NamespaceFilter(IbatisConstants.SQLMAP_DTDS);
        ibatisAbatorNamespaceFilter = new NamespaceFilter(IbatisConstants.ABATOR_DTDS);
    }

    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {
        this.registrary = psiReferenceRegistrar;
        registerProvider();
    }

    public void registerProvider() {
        Project project = registrary.getProject();
        //statement id reference
        registrary.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new SqlClientElementFilter()), new StatementIdReferenceProvider());
//        registry.registerDocTagReferenceProvider(new String[]{"table"}, new JavadocTagFilter("table"), true, new JavadocTableNameReferenceProvider());
//        registry.registerDocTagReferenceProvider(new String[]{"column"}, new JavadocTagFilter("column"), true, new JavadocTableColumnReferenceProvider());
        //ference provider declaration
        JavaClassReferenceProvider classReferenceProvider = new JavaClassReferenceProvider(project);
        IbatisClassShortcutsReferenceProvider classShortcutsReferenceProvider = new IbatisClassShortcutsReferenceProvider(project);
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
        StatementSelfReferenceProvider statementSelfReferenceProvider = new StatementSelfReferenceProvider();
        TypeHandlerReferenceProvider typeHandlerReferenceProvider = new TypeHandlerReferenceProvider(project);
        //Java class
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapConfigNamespaceFilter, "typeAlias", new String[]{"type"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapConfigNamespaceFilter, "parameter", new String[]{"javaType"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "typeAlias", new String[]{"type"}, classReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameter", new String[]{"typeHandler"}, typeHandlerReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"typeHandler"}, typeHandlerReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "discriminator", new String[]{"typeHandler"}, typeHandlerReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapConfigNamespaceFilter, "typeHandler", new String[]{"callback"}, classReferenceProvider);
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
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameter", new String[]{"javaType"}, classShortcutsReferenceProvider);
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
        //statement self reference for id
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "sql", new String[]{"id"}, statementSelfReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "resultMap", new String[]{"id"}, statementSelfReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "parameterMap", new String[]{"id"}, statementSelfReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "cacheModel", new String[]{"id"}, statementSelfReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "typeAlias", new String[]{"alias"}, statementSelfReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapConfigNamespaceFilter, "typeHandler", new String[]{"javaType"}, statementSelfReferenceProvider);
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
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapConfigNamespaceFilter, "typeHandler", new String[]{"jdbcType"}, jdbcTypeReferenceProvider);
        // jdbcType reference provider
        registerXmlAttributeValueReferenceProvider(ibatisSqlMapNamespaceFilter, "result", new String[]{"jdbcType"}, jdbcTypeReferenceProvider);
        //Abator
        registerXmlAttributeValueReferenceProvider(ibatisAbatorNamespaceFilter, "columnOverride", new String[]{"jdbcType"}, jdbcTypeReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisAbatorNamespaceFilter, "columnOverride", new String[]{"typeHandler"}, typeHandlerReferenceProvider);
        registerXmlAttributeValueReferenceProvider(ibatisAbatorNamespaceFilter, "columnOverride", new String[]{"javaType"}, classShortcutsReferenceProvider);
        // CompletionData registration
        //todo jacky completion
//        SqlMapSymbolCompletionData selectorSymbolCompletionData = new SqlMapSymbolCompletionData(null);
//        CompletionUtil.registerCompletionData(FileTypeManager.getInstance().getFileTypeByExtension("sql"), selectorSymbolCompletionData);
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return IbatisBundle.message("ibatis.referenceprovider.project.component.name");
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    /*  private void registerXmlAttributeValueReferenceProvider(NamespaceFilter namespaceFilter, String tagName, String attributeNames[], PsiReferenceProvider referenceProvider) {
        XmlUtil.registerXmlAttributeValueReferenceProvider(registry, attributeNames, new ScopeFilter(new ParentElementFilter(new AndFilter(new ClassFilter(XmlTag.class), new AndFilter(new OrFilter(new TextFilter(tagName)), namespaceFilter)), 2)), referenceProvider);
    }*/

    /**
     * Register the given provider on the given XmlAttribute/Namespace/XmlTag(s) combination.
     *
     * @param provider        Provider to install.
     * @param attributeNames  Attribute names.
     * @param namespaceFilter Namespace for tag(s).
     * @param tagName         tag name
     */
    private void registerXmlAttributeValueReferenceProvider(final NamespaceFilter namespaceFilter, String tagName, final @NonNls String[] attributeNames, final PsiReferenceProvider provider) {
        XmlUtil.registerXmlAttributeValueReferenceProvider(registrary, attributeNames, andTagNames(namespaceFilter, tagName), provider);
    }

    public final static ClassFilter TAG_CLASS_FILTER = new ClassFilter(XmlTag.class);

    public static ScopeFilter andTagNames(final ElementFilter namespace, final String... tagNames) {
        return new ScopeFilter(new ParentElementFilter(new AndFilter(namespace, TAG_CLASS_FILTER, new TextFilter(tagNames)), 2));
    }
}
