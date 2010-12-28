package org.intellij.ibatis.dom.converters;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.scope.PsiConflictResolver;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.*;

import java.util.*;

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
        final String text = genericDomValue.getStringValue();
        if (text == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        Project project = element.getProject();
        final int offset = ElementManipulators.getOffsetInElement(element);
        final FileReferenceSet referenceSet = createReferenceSet(project, text, element, offset);
        return referenceSet.getAllReferences();
    }


    /**
     * file reference set for element
     *
     * @param project        Project object
     * @param text           text
     * @param element        source element
     * @param startInElement start position in element
     * @return file reference set
     */
    protected FileReferenceSet createReferenceSet(final Project project, final String text, final PsiElement element, final int startInElement) {
        return new FileReferenceSet(text, element, startInElement, null, true) {
            protected boolean isSoft() {
                return false;
            }

            /**
             * compute default context for file reference
             * @return context  list
             */
            @NotNull
            public Collection<PsiFileSystemItem> computeDefaultContexts() {
                final OrderedSet<PsiFileSystemItem> result = new OrderedSet<PsiFileSystemItem>();
                Module module = ModuleUtil.findModuleForPsiElement(element);
                final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
                PsiManager psiManager = PsiManager.getInstance(project);
                for (VirtualFile root : rootManager.getSourceRoots()) {
                    ContainerUtil.addIfNotNull(psiManager.findDirectory(root), result);
                }
                for (VirtualFile root : rootManager.getContentRoots()) {
                    ContainerUtil.addIfNotNull(psiManager.findDirectory(root), result);
                }
                return result;
            }

            protected Condition<PsiElement> createCondition() {
                return new Condition<PsiElement>() {
                    public boolean value(PsiElement psiFile) {
                        final boolean isDirectory = psiFile instanceof PsiDirectory;
                        final boolean isFile = psiFile instanceof PsiFile;
                        return isDirectory || (isFile && isFileAccepted((PsiFile) psiFile));
                    }
                };
            }
        };
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

    protected abstract boolean isFileAccepted(final PsiFile file);


    public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
        return "Resolve properties file failed!";
    }

}