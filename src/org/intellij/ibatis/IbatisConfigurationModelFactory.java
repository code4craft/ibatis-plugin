package org.intellij.ibatis;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.model.impl.DomModelFactory;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.intellij.ibatis.impl.IbatisConfigurationModelImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * iBATIS configuration model factory
 *
 * @author Jacky
 */
public class IbatisConfigurationModelFactory extends DomModelFactory<SqlMapConfig, IbatisConfigurationModel, PsiElement> {
    private Map<String, Set<XmlFile>> CONFIGURATION_FILES = new HashMap<String, Set<XmlFile>>();

    protected IbatisConfigurationModelFactory(DomManager domManager) {
        super(SqlMapConfig.class, domManager.createModelMerger(), domManager.getProject(), "iBATIS");
    }

    @Nullable
    public IbatisConfigurationModel getModel(@NotNull PsiElement context) {
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
            IbatisConfigurationModel model = new IbatisConfigurationModelImpl(createMergedModelRoot(files), files);
            models.add(model);
        }
        return models;
    }

    public Set<XmlFile> getAllSqlMapConfigurationFile(final Module module) {
        if (CONFIGURATION_FILES.containsKey(module.getName())) return CONFIGURATION_FILES.get(module.getName());
        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        PsiManager psiManager = PsiManager.getInstance(module.getProject());
        for (VirtualFile root : rootManager.getSourceRoots()) {
            PsiDirectory sourceDir = psiManager.findDirectory(root);
            if (sourceDir != null) {
                sourceDir.accept(new XmlRecursiveElementVisitor() {
                    public void visitXmlFile(XmlFile xmlFile) {
                        final DomFileElement fileElement = DomManager.getDomManager(module.getProject()).getFileElement(xmlFile, DomElement.class);
                        if (fileElement != null && fileElement.getRootElement() instanceof SqlMapConfig) {
                            if (!CONFIGURATION_FILES.containsKey(module.getName())) {   //only one configuration file supported
                                Set<XmlFile> configurationFileSet = new HashSet<XmlFile>();
                                configurationFileSet.add(xmlFile);
                                CONFIGURATION_FILES.put(module.getName(), configurationFileSet);
                            }
                        }
                    }
                });
            }
        }
        return CONFIGURATION_FILES.get(module.getName());
    }

    protected IbatisConfigurationModel createCombinedModel(Set<XmlFile> xmlFiles, DomFileElement<SqlMapConfig> sqlMapConfigDomFileElement, IbatisConfigurationModel ibatisConfigurationModel, Module module) {
        return new IbatisConfigurationModelImpl(sqlMapConfigDomFileElement, xmlFiles);
    }
}
