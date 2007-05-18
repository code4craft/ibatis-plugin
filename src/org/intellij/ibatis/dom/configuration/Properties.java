package org.intellij.ibatis.dom.configuration;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.intellij.ibatis.dom.converters.PropertiesFileConverter;
import org.jetbrains.annotations.Nullable;

/**
 * properties element in iBATIS configuration xml file
 */
public interface Properties extends CommonDomModelElement {
    @Nullable @Convert(PropertiesFileConverter.class)
    public GenericAttributeValue<PsiFile> getResource();

    public @Nullable GenericAttributeValue<String> getUrl();
}
