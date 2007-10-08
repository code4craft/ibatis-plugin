package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.TagValue;

/**
 * select element
 */
public interface Select extends BaseResultStatement {

    @TagValue
    public String getValue();



    public Include getInclude();

    public ResultMap getReferencedResultMap();

    public PsiClass getResultClazz();

}
