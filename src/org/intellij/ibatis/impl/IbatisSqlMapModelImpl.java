package org.intellij.ibatis.impl;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.model.DomModelImpl;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.intellij.ibatis.dom.sqlMap.TypeAlias;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * iBATIS sql map model implementation.
 */
public class IbatisSqlMapModelImpl extends DomModelImpl<SqlMap> implements IbatisSqlMapModel {
    public IbatisSqlMapModelImpl(final SqlMap mergedModel, final Set<XmlFile> configFiles) {
        super(mergedModel, configFiles);
    }

    @NotNull
    public Map<String, PsiClass> getTypeAlias() {
        Map<String, PsiClass> aliasMap = new HashMap<String, PsiClass>();
        List<TypeAlias> aliasList = getMergedModel().getTypeAlias();
        if (aliasList != null && aliasList.size()>0) {
            for (TypeAlias typeAlias : aliasList) {
                aliasMap.put(typeAlias.getAlias().getValue(), typeAlias.getType().getValue());
            }
        }
        return aliasMap;
    }
    
}
