package org.intellij.ibatis;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.model.DomModelFactory;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.intellij.ibatis.impl.IbatisConfigurationModelImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * iBATIS configuration model factory
 *
 * @author Jacky                                      
 */
public class IbatisConfigurationModelFactory extends DomModelFactory<SqlMapConfig, IbatisConfigurationModel, PsiElement> {
    private  Set<XmlFile> CONFIGURATION_FILES = new HashSet<XmlFile>();

    protected IbatisConfigurationModelFactory(DomManager domManager) {
        super(SqlMapConfig.class, domManager.createModelMerger(), domManager.getProject(), "iBATIS");
    }

    @Nullable public IbatisConfigurationModel getModel(@NotNull PsiElement context) {
        final PsiFile psiFile = context.getContainingFile();
        if (psiFile instanceof XmlFile) {
            return getModelByConfigFile((XmlFile) psiFile);
        }
        return null;
    }

    protected List<IbatisConfigurationModel> computeAllModels(@NotNull Module module) {
        List<IbatisConfigurationModel> models = new ArrayList<IbatisConfigurationModel>();
        Set<XmlFile> files = getAllSqlMapConfigurationFile(module);
        if (files.size() > 0) {    //iBATIS configuration xml file found
            IbatisConfigurationModel model = new IbatisConfigurationModelImpl(createMergedModel(files), files);
            models.add(model);
        }
        return models;
    }

    public  Set<XmlFile> getAllSqlMapConfigurationFile(final Module module) {
        if (CONFIGURATION_FILES.size() > 0) return CONFIGURATION_FILES;
        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        PsiManager psiManager = PsiManager.getInstance(module.getProject());
        for (VirtualFile root : rootManager.getSourceRoots()) {
            PsiDirectory sourceDir = psiManager.findDirectory(root);
            if (sourceDir != null) {
                sourceDir.accept(new PsiRecursiveElementVisitor() {
                    public void visitXmlFile(XmlFile xmlFile) {
                        final DomFileElement fileElement = DomManager.getDomManager(module.getProject()).getFileElement(xmlFile, DomElement.class);
                        if (fileElement != null && fileElement.getRootElement() instanceof SqlMapConfig) {
                            if (CONFIGURATION_FILES.size() < 1)     //only one file accepted
                                CONFIGURATION_FILES.add(xmlFile);
                        }
                    }
                });
            }
        }
        return CONFIGURATION_FILES;
    }

    protected IbatisConfigurationModel createCombinedModel(Set<XmlFile> xmlFiles, DomFileElement<SqlMapConfig> sqlMapConfigDomFileElement, IbatisConfigurationModel ibatisConfigurationModel, Module module) {
       return new IbatisConfigurationModelImpl(sqlMapConfigDomFileElement.getRootElement(), xmlFiles);
    }
}
