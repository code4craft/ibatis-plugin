package org.intellij.ibatis.dom.converters;

import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.psi.PsiFile;

/**
 * property file converter, only properties file accepted
 *
 * @author jacky
 */
public class PropertiesFileConverter extends PsiFileConverterBase {

    /**
     * validate file can be accepted
     *
     * @param psiFile psi file
     * @return accept mark
     */
    protected boolean isFileAccepted(final PsiFile psiFile) {
        return psiFile instanceof PropertiesFile;
    }

}