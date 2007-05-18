package org.intellij.ibatis.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.model.DomModelImpl;
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

    public IbatisConfigurationModelImpl(final SqlMapConfig mergedModel, final Set<XmlFile> configFiles) {
        super(mergedModel, configFiles);
    }

    @NotNull public Set<XmlFile> getSqlMapFiles() {
        Set<XmlFile> xmlFiles = new HashSet<XmlFile>();
        List<SqlMap> sqlMaps = getMergedModel().getSqlMaps();
        for (SqlMap sqlMap : sqlMaps) {
            xmlFiles.add((XmlFile) sqlMap.getResource().getValue());
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
}
