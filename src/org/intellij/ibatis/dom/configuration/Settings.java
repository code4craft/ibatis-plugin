package org.intellij.ibatis.dom.configuration;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Attribute;

/**
 * properties element in iBATIS configuration xml file
 */
public interface Settings extends CommonDomModelElement {

    public GenericAttributeValue<String> getCacheModelsEnabled();

    public GenericAttributeValue<String> getEnhancementEnabled();

    public GenericAttributeValue<String> getLazyLoadingEnabled();

    public GenericAttributeValue<Integer> getMaxRequests();

    public GenericAttributeValue<Integer> getMaxSessions();

    public GenericAttributeValue<Integer> getMaxTransactions();

    @Attribute("useStatementNamespaces")
    public GenericAttributeValue<String> getUseStatementNamespaces();
    
    public GenericAttributeValue<Integer> getDefaultStatementTimeout();

    public GenericAttributeValue<Integer> getStatementCachingEnabled();

    public GenericAttributeValue<String> getClassInfoCacheEnabledd();
}