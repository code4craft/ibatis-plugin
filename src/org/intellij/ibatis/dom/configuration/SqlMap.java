package org.intellij.ibatis.dom.configuration;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.intellij.ibatis.dom.converters.SqlMapFileConverter;
import org.jetbrains.annotations.NotNull;

/**
 * properties element in iBATIS configuration xml file
 */
public interface SqlMap extends CommonDomModelElement {

    @Convert(SqlMapFileConverter.class) @NotNull
    public GenericAttributeValue<PsiFile> getResource();

}