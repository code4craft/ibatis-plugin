package org.intellij.ibatis.dom.configuration.impl;

import com.intellij.javaee.model.xml.impl.RootBaseImpl;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.lang.properties.psi.Property;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.GenericAttributeValue;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;

import java.util.Collections;
import java.util.List;

/**
 * iBATIS configuration  xml file model
 */
public abstract class SqlMapConfigImpl extends RootBaseImpl implements SqlMapConfig {

    /**
     * get properties in file
     *
     * @return properties
     */
    public List<Property> getPropertiesInFile() {
        GenericAttributeValue<PsiFile> resource = getProperties().getResource();
        if (resource != null) {
            PsiFile psiFile = resource.getValue();
            if (psiFile instanceof PropertiesFile) {
                PropertiesFile propertiesFile = (PropertiesFile) psiFile;
                return propertiesFile.getProperties();
            }
        }
        return Collections.emptyList();
    }
}
