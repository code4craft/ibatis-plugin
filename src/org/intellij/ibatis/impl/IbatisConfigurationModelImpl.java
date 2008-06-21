package org.intellij.ibatis.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.model.impl.DomModelImpl;
import com.intellij.util.xml.model.impl.DomModelImpl;
import org.intellij.ibatis.IbatisConfigurationModel;
import org.intellij.ibatis.dom.configuration.SqlMap;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.intellij.ibatis.dom.configuration.TypeAlias;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * iBATIS configuration model implementation
 */
public class IbatisConfigurationModelImpl extends DomModelImpl<SqlMapConfig> implements IbatisConfigurationModel {

    public IbatisConfigurationModelImpl(final DomFileElement<SqlMapConfig> domFileElement, final Set<XmlFile> configFiles) {
        super(domFileElement, configFiles);
    }

    @NotNull
    public Set<XmlFile> getSqlMapFiles() {
        Set<XmlFile> xmlFiles = new HashSet<XmlFile>();
        List<SqlMap> sqlMaps = getMergedModel().getSqlMaps();
        for (SqlMap sqlMap : sqlMaps) {
            PsiFile psiFile = sqlMap.getResource().getValue();
            if (psiFile != null)
                xmlFiles.add((XmlFile) psiFile);
        }
        return xmlFiles;
    }

    public boolean isUseStatementNamespaces() {
        GenericAttributeValue<String> attributeValue = getMergedModel().getSettings().getUseStatementNamespaces();
        if (attributeValue != null) {
            String usedMark = attributeValue.getValue();
            return usedMark != null && usedMark.equals("true");
        }
        return false;
    }

    @NotNull
    public Map<String, PsiClass> getTypeAlias() {
        Map<String, PsiClass> aliasMap = new HashMap<String, PsiClass>();
        List<TypeAlias> globalTypeAlias = getMergedModel().getTypeAlias();
        if (globalTypeAlias != null && globalTypeAlias.size() > 0) {
            for (TypeAlias typeAlias : globalTypeAlias) {
                aliasMap.put(typeAlias.getAlias().getValue(), typeAlias.getType().getValue());
            }
        }
        return aliasMap;
    }

    @NotNull
    public Map<String, XmlTag> getTypeAlias2() {
        Map<String, XmlTag> aliasMap = new HashMap<String, XmlTag>();
        List<TypeAlias> globalTypeAlias = getMergedModel().getTypeAlias();
        if (globalTypeAlias != null && globalTypeAlias.size() > 0) {
            for (TypeAlias typeAlias : globalTypeAlias) {
                aliasMap.put(typeAlias.getAlias().getValue(), typeAlias.getType().getXmlTag());
            }
        }
        return aliasMap;
    }
}
