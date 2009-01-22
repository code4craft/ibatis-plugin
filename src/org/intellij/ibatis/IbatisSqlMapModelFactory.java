package org.intellij.ibatis;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.model.impl.DomModelFactory;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.intellij.ibatis.impl.IbatisSqlMapModelImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * iBATIS configuration model factory
 */
public class IbatisSqlMapModelFactory extends DomModelFactory<SqlMap, IbatisSqlMapModel, PsiElement> {

    protected IbatisSqlMapModelFactory(DomManager domManager) {
        super(SqlMap.class, domManager.getProject(), "spring");
    }

    @Nullable
    public IbatisSqlMapModel getModel(@NotNull PsiElement context) {
        final PsiFile psiFile = context.getContainingFile();
        if (psiFile instanceof XmlFile) {
            return getModelByConfigFile((XmlFile) psiFile);
        }
        return null;
    }

    protected List<IbatisSqlMapModel> computeAllModels(@NotNull Module module) {
        List<IbatisSqlMapModel> models = new ArrayList<IbatisSqlMapModel>();
        IbatisManager manager = IbatisManager.getInstance();
        IbatisConfigurationModel configurationModel = manager.getConfigurationModel(module);
        if (configurationModel != null) {
            Set<XmlFile> sqlMapFiles = configurationModel.getSqlMapFiles();
            if (sqlMapFiles.size() > 0) {
                IbatisSqlMapModel model = new IbatisSqlMapModelImpl(createMergedModelRoot(sqlMapFiles), sqlMapFiles);
                models.add(model);
            }
        }
        return models;
    }

    protected IbatisSqlMapModel createCombinedModel(Set<XmlFile> xmlFiles, DomFileElement<SqlMap> sqlMapDomFileElement, IbatisSqlMapModel ibatisSqlMapModel, Module module) {
        return new IbatisSqlMapModelImpl(sqlMapDomFileElement, xmlFiles);
    }

}