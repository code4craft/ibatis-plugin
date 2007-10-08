package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * typeAlias element in SQL map xml file.
 */
public interface ParameterMap extends CommonDomModelElement {

    @Attribute("class")
    public GenericAttributeValue<String> getClazz();

    @NotNull
    public GenericAttributeValue<String> getId();

    @Nullable
    public PsiClass getPsiClass();
}