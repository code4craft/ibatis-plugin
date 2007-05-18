package org.intellij.ibatis.dom.configuration;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * properties element in iBATIS configuration xml file
 */
public interface ResultObjectFactory extends CommonDomModelElement {

    @NotNull
    public GenericAttributeValue<PsiClass> getType();

}