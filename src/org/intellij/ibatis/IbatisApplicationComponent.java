package org.intellij.ibatis;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.*;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.ide.IconProvider;
import com.intellij.javaee.ExternalResourceManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.meta.MetaDataRegistrar;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.facet.IbatisFacetType;
import org.intellij.ibatis.inspections.*;
import org.intellij.ibatis.usages.*;
import org.intellij.ibatis.util.IbatisBundle;
import org.intellij.ibatis.util.IbatisConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.io.InputStream;

/**
 * iBATIS application component, include inspection, IconProvider
 *
 * @authtor Jacky
 */
public class IbatisApplicationComponent implements ApplicationComponent, InspectionToolProvider, IconProvider {

    //live template related
    private static final @NonNls String NAME = "name";
    private static final @NonNls String VALUE = "value";
    private static final @NonNls String DESCRIPTION = "description";
    private static final @NonNls String TO_REFORMAT = "toReformat";
    private static final @NonNls String TO_SHORTEN_FQ_NAMES = "toShortenFQNames";
    private static final @NonNls String VARIABLE = "variable";
    private static final @NonNls String EXPRESSION = "expression";
    private static final @NonNls String DEFAULT_VALUE = "defaultValue";
    private static final @NonNls String ALWAYS_STOP_AT = "alwaysStopAt";
    private static final String CONTEXT = "context";
    private static final String IBATIS_LIVE_TEMPLATE_NAME = "ibatis";
    private static final String TEMPLATES_FILE = "/org/intellij/ibatis/livetemplates/ibatis.xml";

    /**
     * initialize component
     */
    public void initComponent() {
        registerDTDs(IbatisConstants.CONFIGURATION_DTDS);
        registerDTDs(IbatisConstants.SQLMAP_DTDS);
        registerDTDs(IbatisConstants.ABATOR_DTDS);
        FacetTypeRegistry.getInstance().registerFacetType(IbatisFacetType.INSTANCE);
        initLiveTemplates();
        initMetaData();
    }

    /**
     * initialize meta data
     */
    private void initMetaData() {
        MetaDataRegistrar.getInstance().registerMetaData(new ElementFilter() {
            public boolean isAcceptable(Object element, PsiElement context) {
                if (element instanceof XmlTag) {
                    final XmlTag tag = (XmlTag) element;
                    final DomElement domElement = DomManager.getDomManager(tag.getProject()).getDomElement(tag);
                    if (!(domElement instanceof BaseStatement)) return false;
                    BaseStatement statement = (BaseStatement) domElement;
                    if (statement.getId().getStringValue() != null)
                        return true;
                }
                return false;
            }

            public boolean isClassAcceptable(Class hintClass) {
                return XmlTag.class.isAssignableFrom(hintClass);
            }
        }, StatementMetaData.class);
        MetaDataRegistrar.getInstance().registerMetaData(new ElementFilter() {
            public boolean isAcceptable(Object element, PsiElement context) {
                if (element instanceof XmlTag) {
                    final XmlTag tag = (XmlTag) element;
                    final DomElement domElement = DomManager.getDomManager(tag.getProject()).getDomElement(tag);
                    if (!(domElement instanceof ResultMap)) return false;
                    ResultMap resultMap = (ResultMap) domElement;
                    if (resultMap.getId().getStringValue() != null)
                        return true;
                }
                return false;
            }

            public boolean isClassAcceptable(Class hintClass) {
                return XmlTag.class.isAssignableFrom(hintClass);
            }
        }, ResultMapMetaData.class);
        MetaDataRegistrar.getInstance().registerMetaData(new ElementFilter() {
            public boolean isAcceptable(Object element, PsiElement context) {
                if (element instanceof XmlTag) {
                    final XmlTag tag = (XmlTag) element;
                    final DomElement domElement = DomManager.getDomManager(tag.getProject()).getDomElement(tag);
                    if (!(domElement instanceof Sql)) return false;
                    Sql sql = (Sql) domElement;
                    if (sql.getId().getStringValue() != null)
                        return true;
                }
                return false;
            }

            public boolean isClassAcceptable(Class hintClass) {
                return XmlTag.class.isAssignableFrom(hintClass);
            }
        }, SqlMetaData.class);
        MetaDataRegistrar.getInstance().registerMetaData(new ElementFilter() {
            public boolean isAcceptable(Object element, PsiElement context) {
                if (element instanceof XmlTag) {
                    final XmlTag tag = (XmlTag) element;
                    final DomElement domElement = DomManager.getDomManager(tag.getProject()).getDomElement(tag);
                    if (!(domElement instanceof CacheModel)) return false;
                    CacheModel cacheModel = (CacheModel) domElement;
                    if (cacheModel.getId().getStringValue() != null)
                        return true;
                }
                return false;
            }

            public boolean isClassAcceptable(Class hintClass) {
                return XmlTag.class.isAssignableFrom(hintClass);
            }
        }, CacheModelMetaData.class);
    }

    public void disposeComponent() {
    }

    @NotNull public String getComponentName() {
        return IbatisBundle.message("ibatis.application.component.name");
    }

    /**
     * register DTD for iBATIS
     *
     * @param dtdArray URLs
     */
    private void registerDTDs(String dtdArray[]) {
        for (String url : dtdArray) {
            if (url.startsWith("http://")) {
                int pos = url.lastIndexOf('/');
                @NonNls String file = "/org/intellij/ibatis/dtds" + url.substring(pos);
                ExternalResourceManager.getInstance().addStdResource(url, file, IbatisApplicationComponent.class);
            }
        }
    }

    /**
     * icon for special psiFile
     *
     * @param psiElement PsiFile sub class
     * @param i          i
     * @return icon for special psiFile
     */
    @Nullable public Icon getIcon(@NotNull PsiElement psiElement, int i) {
        return null;
    }

    /**
     * get all inspection class
     *
     * @return inspection class array
     */
    public Class[] getInspectionClasses() {
        return new Class[]{SqlMapFileInConfigurationInspection.class,
                NullSettedToPrimaryTypeInspection.class, ResultMapInSelectInspection.class,
                SymbolInSQLInspection.class, ParameterMapInStatementInspection.class};
    }

    /**
     * init all the live template
     */
    private void initLiveTemplates() {
        loadLiveTemplateFile(TEMPLATES_FILE, IBATIS_LIVE_TEMPLATE_NAME);
    }

    /**
     * load the template from file
     *
     * @param templatesFile files with templates included
     * @param templateName  template name
     */
    private void loadLiveTemplateFile(String templatesFile, final String templateName) {
        final InputStream is = getClass().getResourceAsStream(templatesFile);
        if (is != null) {
            loadTemplates(is, templateName);
        }
    }

    /**
     * load live templates from input stream
     *
     * @param inputStream  input stream
     * @param templateName template Name
     */
    public void loadTemplates(@NotNull final InputStream inputStream, final String templateName) {
        final SAXBuilder parser = new SAXBuilder();
        try {
            final TemplateSettings templateSettings = TemplateSettings.getInstance();
            final Document doc = parser.build(inputStream);
            final Element root = doc.getRootElement();
            for (Object element : root.getChildren()) {
                if (element instanceof Element) {
                    final Template template = readExternal((Element) element, templateName);
                    final String key = template.getKey();
                    // if template with the same key is already loaded, ignore it  TODO: rewrite, when API will be improved!
                    if (key != null && templateSettings.getTemplate(key) == null) {
                        templateSettings.addTemplate(template);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads template content from element
     *
     * @param element      Element, containing template information
     * @param templateName Template name
     * @return Template from element
     */
    @NotNull protected Template readExternal(@NotNull final Element element, final String templateName) {
        final String name = element.getAttributeValue(NAME);
        final String value = element.getAttributeValue(VALUE);
        final TemplateImpl template = new TemplateImpl(name, value, templateName);
        template.setDescription(element.getAttributeValue(DESCRIPTION));
        template.setToReformat(Boolean.valueOf(element.getAttributeValue(TO_REFORMAT)));
        template.setToShortenLongNames(Boolean.valueOf(element.getAttributeValue(TO_SHORTEN_FQ_NAMES)));
        TemplateContext context = template.getTemplateContext();
        for (final Object o : element.getChildren(VARIABLE)) {
            Element e = (Element) o;
            String variableName = e.getAttributeValue(NAME);
            String expression = e.getAttributeValue(EXPRESSION);
            String defaultValue = e.getAttributeValue(DEFAULT_VALUE);
            boolean isAlwaysStopAt = Boolean.valueOf(e.getAttributeValue(ALWAYS_STOP_AT));
            template.addVariable(variableName, expression, defaultValue, isAlwaysStopAt);
        }
        final Element contextElement = element.getChild(CONTEXT);
        if (contextElement != null) {
            try {
                DefaultJDOMExternalizer.readExternal(context, contextElement);
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        }
        return template;
    }


}
