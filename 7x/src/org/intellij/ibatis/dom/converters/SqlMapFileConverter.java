package org.intellij.ibatis.dom.converters;

import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.SqlMap;

/**
 * sqlmap configuration file converter
 *
 * @author Jacky
 */
public class SqlMapFileConverter extends PsiFileConverterBase {

    protected boolean isFileAccepted(final PsiFile file) {
        if (file instanceof XmlFile) {
            final DomFileElement fileElement = DomManager.getDomManager(file.getProject()).getFileElement((XmlFile) file, DomElement.class);
            return fileElement != null && fileElement.getRootElement() instanceof SqlMap;
        }
        return false;
    }
}