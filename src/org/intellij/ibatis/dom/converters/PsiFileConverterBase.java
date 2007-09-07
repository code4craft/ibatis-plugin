package org.intellij.ibatis.dom.converters;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.impl.source.resolve.reference.ProcessorRegistry;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSetBase;
import com.intellij.psi.scope.PsiConflictResolver;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * resolve psi file in a module
 *
 * @author Jacky
 */
public abstract class PsiFileConverterBase extends ResolvingConverter<PsiFile> implements CustomReferenceConverter {

    public PsiFile fromString(@Nullable @NonNls String s, final ConvertContext context) {
        if (s == null) return null;
        final PsiReference[] references = createReferences((GenericDomValue) context.getInvocationElement(), context.getReferenceXmlElement(), context);
        if (references.length == 0) return null;
        final PsiElement element = references[references.length - 1].resolve();
        return element instanceof PsiFile ? (PsiFile) element : null;
    }

    public String toString(@Nullable PsiFile psiFile, final ConvertContext context) {
        if (psiFile == null) return null;
        final VirtualFile file = psiFile.getVirtualFile();
        if (file == null) return null;
        VirtualFile root = getRootForFile(file, context);
        if (root == null) return null;
        return VfsUtil.getRelativePath(file, root, '/');
    }

    @Nullable
    protected VirtualFile getRootForFile(final VirtualFile file, final ConvertContext context) {
        final ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(context.getPsiManager().getProject()).getFileIndex();
        VirtualFile root = projectFileIndex.getSourceRootForFile(file);
        if (root == null) {
            root = projectFileIndex.getContentRootForFile(file);
        }
        return root;
    }

    @NotNull
    public Collection<? extends PsiFile> getVariants(final ConvertContext context) {
        return Collections.emptyList();
    }

    public PsiElement resolve(final PsiFile o, final ConvertContext context) {
        return isFileAccepted(o) ? super.resolve(o, context) : null;
    }

    @NotNull
    public PsiReference[] createReferences(GenericDomValue genericDomValue, PsiElement element, ConvertContext context) {
        final String s = genericDomValue.getStringValue();
        if (s == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        final int offset = ReferenceProvidersRegistry.getInstance(element.getProject()).getOffsetInElement(element);
        final FileReferenceSetBase set = createReferenceSet(context, s, element, offset);
        return set.getAllReferences();
    }

    protected abstract boolean isFileAccepted(final PsiFile file);

    protected FileReferenceSetBase createReferenceSet(final ConvertContext context, final String text, final PsiElement element,
                                                      final int startInElement) {
        return new FileReferenceSetBase(text, element, startInElement, null, true) {
            protected boolean isSoft() {
                return false;
            }

            @NotNull
            public Collection<PsiFileSystemItem> computeDefaultContexts() {
                final OrderedSet<PsiFileSystemItem> result = new OrderedSet<PsiFileSystemItem>();
                return addDefaultRoots(result, context);
            }

            protected PsiScopeProcessor createProcessor(List<CandidateInfo> candidateInfos, List<Class> classes, List<PsiConflictResolver> psiConflictResolvers) throws ProcessorRegistry.IncompatibleReferenceTypeException {
                final PsiScopeProcessor baseProcessor = super.createProcessor(candidateInfos, classes, psiConflictResolvers);
                return new PsiScopeProcessor() {
                    public boolean execute(PsiElement element, PsiSubstitutor substitutor) {
                        final boolean isFile = element instanceof PsiFile;
                        return isFile && !isFileAccepted((PsiFile) element) || baseProcessor.execute(element, substitutor);
                    }

                    public <T> T getHint(Class<T> hintClass) {
                        return baseProcessor.getHint(hintClass);
                    }

                    public void handleEvent(Event event, Object associated) {
                        baseProcessor.handleEvent(event, associated);
                    }
                };
            }
        };
    }

    protected Collection<PsiFileSystemItem> addDefaultRoots(final Collection<PsiFileSystemItem> result, final ConvertContext context) {
        final Module module = context.getModule();
        if (module != null) {
            final PsiManager psiManager = context.getPsiManager();
            addModuleDefaultRoots(result, module, psiManager);
            for (Module dependency : ModuleRootManager.getInstance(module).getDependencies()) {
                addModuleDefaultRoots(result, dependency, psiManager);
            }
        }
        return result;
    }

    protected void addModuleDefaultRoots(final Collection<PsiFileSystemItem> result, final Module module, final PsiManager psiManager) {
        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        for (VirtualFile root : rootManager.getSourceRoots()) {
            ContainerUtil.addIfNotNull(psiManager.findDirectory(root), result);
        }
        for (VirtualFile root : rootManager.getContentRoots()) {
            ContainerUtil.addIfNotNull(psiManager.findDirectory(root), result);
        }
    }

    public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
        return "Resolve properties file failed!";
    }

}