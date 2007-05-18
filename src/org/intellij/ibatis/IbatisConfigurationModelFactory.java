package org.intellij.ibatis;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.model.DomModelFactory;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.intellij.ibatis.impl.IbatisConfigurationModelImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * iBATIS configuration model factory
 */
public class IbatisConfigurationModelFactory extends DomModelFactory<SqlMapConfig, IbatisConfigurationModel, PsiElement> {

    protected IbatisConfigurationModelFactory(DomManager domManager) {
        super(SqlMapConfig.class, domManager.createModelMerger(), domManager.getProject(), "spring");
    }

    public IbatisConfigurationModel getModel(@NotNull PsiElement context) {
        final PsiFile psiFile = context.getContainingFile();
        if (psiFile instanceof XmlFile) {
            return getModelByConfigFile((XmlFile) psiFile);
        }
        return null;
    }

    protected List<IbatisConfigurationModel> computeAllModels(@NotNull Module module) {
        List<IbatisConfigurationModel> models = new ArrayList<IbatisConfigurationModel>();
        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        PsiManager psiManager = PsiManager.getInstance(module.getProject());
        for (VirtualFile root : rootManager.getSourceRoots()) {
            Set<XmlFile> files = new ArrayListSet<XmlFile>();
            for (VirtualFile virtualFile : root.getChildren()) {
                if (virtualFile.getName().endsWith(".xml")) {
                    final PsiFile psiFile = psiManager.findFile(virtualFile);
                    if (psiFile instanceof XmlFile) {
                        final DomFileElement fileElement = DomManager.getDomManager(module.getProject()).getFileElement((XmlFile) psiFile, DomElement.class);
                        if (fileElement != null && fileElement.getRootElement()  instanceof SqlMapConfig) {
                            files.add((XmlFile) psiFile);
                        }
                    }
                }
            }
            if (files.size() > 0) {    //iBATIS configuration xml file found
                IbatisConfigurationModel model = new IbatisConfigurationModelImpl(createMergedModel(files), files);
                models.add(model);
            }
        }
        return models;
    }

    protected IbatisConfigurationModel createCombinedModel(Set<XmlFile> configFiles, SqlMapConfig mergedModel, IbatisConfigurationModel firstModel) {
        return new IbatisConfigurationModelImpl(mergedModel, configFiles);
    }
    
}
