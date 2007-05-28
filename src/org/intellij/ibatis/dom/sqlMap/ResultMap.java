package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
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

    @Attribute("extends")
    public GenericAttributeValue<String> getExtends();

    @SubTagList("result")
    public List<Result> getResults();

    public PsiClass getPsiClass();

    /**
     * get all results included extended result
     * @return
     */
   public List<Result> getAllResults();
}