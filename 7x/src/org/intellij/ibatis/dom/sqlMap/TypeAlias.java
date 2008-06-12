package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * typeAlias element in sql map xml file.
 */
public interface TypeAlias extends CommonDomModelElement {
    @NotNull
    public GenericAttributeValue<PsiClass> getType();

    @NotNull
    public GenericAttributeValue<String> getAlias();

}
