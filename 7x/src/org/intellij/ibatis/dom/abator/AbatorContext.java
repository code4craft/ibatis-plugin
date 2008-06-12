package org.intellij.ibatis.dom.abator;

import com.intellij.javaee.model.xml.CommonDomModelElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * abatorContext element
 */
public interface AbatorContext extends CommonDomModelElement {
    @SubTagList("table")
    public List<Table> getTables();
}
