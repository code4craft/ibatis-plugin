package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

/**
 * typeAlias element in sql map xml file.
 */
public interface ParameterMap extends CommonDomModelElement {

    @Attribute("class")
    public GenericAttributeValue<String> getClazz();

    @NotNull
    public GenericAttributeValue<String> getId();

    public PsiClass getPsiClass();
}