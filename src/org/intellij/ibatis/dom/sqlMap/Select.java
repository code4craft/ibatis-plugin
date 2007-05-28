package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.TagValue;

/**
 * select element
 */
public interface Select extends CommonDomModelElement {

    @TagValue
    public String getValue();

    public GenericAttributeValue<String> getId();

    @Attribute("parameterClass")
    public GenericAttributeValue<String> getParameterClass();

    @Attribute("parameterMap")
    public GenericAttributeValue<String> getParameterMap();

    @Attribute("resultClass")
    public GenericAttributeValue<String> getResultClass();

    @Attribute("resultMap")
    public GenericAttributeValue<String> getResultMap();

    public Include getInclude();

    public ResultMap getReferencedResultMap();

    public PsiClass getResultClazz();

    public String getSQL();
}
