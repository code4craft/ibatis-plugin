package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * typeAlias element in sql map xml file.
 */
public interface ResultMap extends CommonDomModelElement {

    @Attribute("class")
    public GenericAttributeValue<String> getClazz();

    @NotNull
    public GenericAttributeValue<String> getId();

    @SubTagList("result")
    public List<Result> getResults();

    public PsiClass getPsiClass();
}