package org.intellij.ibatis;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.facet.FacetManager;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ElementPresentationManager;
import org.intellij.ibatis.facet.IbatisFacet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

/**
 * Base class for "Go To Symbol" contributors.
 *
 * @author Jacky
 */
abstract class GoToSymbolProvider implements ChooseByNameContributor {

    protected abstract void getNames(@NotNull Module module, Set<String> result);

    protected abstract void getItems(@NotNull Module module, String name, List<NavigationItem> result);

    protected static void addNames(@NotNull final Collection<? extends DomElement> elements, final Set<String> existingNames) {
        for (DomElement name : elements) {
            XmlTag tag = name.getXmlTag();
            if (tag != null)
                existingNames.add(tag.getAttributeValue("id"));
        }
    }

    public String[] getNames(final Project project, boolean includeNonProjectItems) {
        Set<String> result = new HashSet<String>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            if (IbatisFacet.getInstance(module) != null) {
                getNames(module, result);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public NavigationItem[] getItemsByName(String name, String s1, Project project, boolean includeNonProjectItems) {
        List<NavigationItem> result = new ArrayList<NavigationItem>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            if (IbatisFacet.getInstance(module) != null) {
                getItems(module, name, result);
            }
        }
        return result.toArray(new NavigationItem[result.size()]);
    }


    @Nullable
    protected static NavigationItem createNavigationItem(final DomElement domElement) {
        XmlTag xmlTag = domElement.getXmlTag();
        if (xmlTag == null) return null;
        final String value = xmlTag.getAttributeValue("id");
        if (value == null) return null;
        final Icon icon = ElementPresentationManager.getIcon(domElement);
        return createNavigationItem(xmlTag.getAttribute("id"), value, icon);
    }

    @NotNull
    protected static NavigationItem createNavigationItem(@NotNull final PsiElement element,
                                                         @NotNull @NonNls final String text,
                                                         @Nullable final Icon icon) {
        return new BaseNavigationItem(element, text, icon);
    }


    /**
     * Wraps one entry to display in "Go To Symbol" dialog.
     */
    protected static class BaseNavigationItem extends ASTWrapperPsiElement {

        private final PsiElement myPsiElement;
        private final String myText;
        private final Icon myIcon;

        /**
         * Creates a new display item.
         *
         * @param psiElement The PsiElement to navigate to.
         * @param text       Text to show for this element.
         * @param icon       Icon to show for this element.
         */
        protected BaseNavigationItem(@NotNull PsiElement psiElement, @NotNull @NonNls String text, @Nullable Icon icon) {
            super(psiElement.getNode());
            myPsiElement = psiElement;
            myText = text;
            myIcon = icon;
        }

        public Icon getIcon(int flags) {
            return myIcon;
        }

        public ItemPresentation getPresentation() {
            return new ItemPresentation() {

                public String getPresentableText() {
                    return myText;
                }

                @Nullable
                public String getLocationString() {
                    return '(' + myPsiElement.getContainingFile().getName() + ')';
                }

                @Nullable
                public Icon getIcon(boolean open) {
                    return myIcon;
                }

                @Nullable
                public TextAttributesKey getTextAttributesKey() {
                    return null;
                }
            };
        }
    }

    public boolean isIbatisModule(Module module) {
        return FacetManager.getInstance(module).getFacetsByType(IbatisFacet.FACET_TYPE_ID).size() > 0;
    }

}