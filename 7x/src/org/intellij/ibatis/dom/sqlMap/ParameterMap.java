package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.converters.IbatisClassConverter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * parameter map element in SQL map file
 *
 * @author Jacky
 */
public interface ParameterMap extends CommonDomModelElement {

    @Attribute("class")
    @Convert(IbatisClassConverter.class)
    public GenericAttributeValue<PsiClass> getClazz();

    @NotNull
    public GenericAttributeValue<String> getId();

    @SubTagList("parameter")
    public List<Parameter> getParameters();
}