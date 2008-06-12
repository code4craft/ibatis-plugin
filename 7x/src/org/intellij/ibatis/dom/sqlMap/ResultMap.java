package org.intellij.ibatis.dom.sqlMap;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import org.intellij.ibatis.dom.converters.IbatisClassConverter;
import org.intellij.ibatis.dom.converters.ResultMapConverter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * result map element in SQL map file.
 *
 * @author Jacky
 */
public interface ResultMap extends CommonDomModelElement {

    @Attribute("class")
    @Convert(IbatisClassConverter.class)
    public GenericAttributeValue<PsiClass> getClazz();

    @NotNull
    public GenericAttributeValue<String> getId();

    @Attribute("extends")
    @Convert(ResultMapConverter.class)
    public GenericAttributeValue<ResultMap> getExtends();

    @SubTagList("result")
    public List<Result> getResults();

    /**
     * get all results included extended result
     *
     * @return Result List
     */
    @NotNull
    public List<Result> getAllResults();
}