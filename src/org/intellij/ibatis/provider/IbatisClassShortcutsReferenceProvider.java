package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * class reference provider  for ibatis, including internal shortcuts and typealias
 */
public class IbatisClassShortcutsReferenceProvider extends WrappedReferenceProvider {
    private static Map<String, String> classShortcuts = new HashMap<String, String>();

    static {
        classShortcuts.put("boolean", "java.lang.Boolean");
        classShortcuts.put("byte", "java.lang.Byte");
        classShortcuts.put("short", "java.lang.Short");
        classShortcuts.put("int", "java.lang.Integer");
        classShortcuts.put("long", "java.lang.Long");
        classShortcuts.put("float", "java.lang.Float");
        classShortcuts.put("double", "java.lang.Double");
        classShortcuts.put("string", "java.lang.String");
        classShortcuts.put("date", "java.util.Date");
        classShortcuts.put("decimal", "java.math.BigDecimal");
        classShortcuts.put("map", "java.util.Map");
    }

    public IbatisClassShortcutsReferenceProvider() {
        super(new JavaClassReferenceProvider());
    }

    private static Map<String, PsiClass> getTypeAlias(PsiElement psiElement) {
        IbatisManager manager = IbatisManager.getInstance();
        return manager.getAllTypeAlias(psiElement);
    }

    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        PsiReference[] references = myProvider.getReferencesByElement(psiElement);
        if (references.length < 1) return references;
        return new PsiReference[]{new WrappedPsiReference(references[references.length - 1]) {
            public PsiElement resolve() {
                String className = getCanonicalText();
                if (classShortcuts.containsKey(className) || getTypeAlias(getElement()).containsKey(className)) {
                    return getPsiElement(getElement(), className);
                }
                return super.resolve();
            }

            public Object[] getVariants() {
                List<Object> classNames = Arrays.asList(super.getVariants());
                Set<String> shortcuts = classShortcuts.keySet();
                Set<String> typeAlias = getTypeAlias(getElement()).keySet();
                List<Object> variants = new ArrayList<Object>();
                variants.addAll(classNames);
                for (String shortcut : shortcuts) {
                    variants.add(LookupValueFactory.createLookupValue(shortcut, IbatisConstants.INTERNAL_CLASS));
                }
                for (String alias : typeAlias) {
                    variants.add(LookupValueFactory.createLookupValue(alias, IbatisConstants.TYPE_ALIAS));
                }
                return variants.toArray();
            }

            public boolean isSoft() {
                return false;
            }
        }};
    }

    /**
     * find PsiClass according to class full name
     *
     * @param project   project object
     * @param className any name of shortcut, type alias or java class
     * @return PsiClass object
     */
    public static PsiElement getPsiElement(PsiElement psiElement, String className) {
        Project project = psiElement.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        //short cut
        if (classShortcuts.containsKey(className)) {
            return psiManager.findClass(classShortcuts.get(className), GlobalSearchScope.allScope(project));
        }
        //type alias
            Map<String, XmlTag> typeAlias2 = IbatisManager.getInstance().getAllTypeAlias2(psiElement);
      if (typeAlias2.containsKey(className)) {
          return typeAlias2.get(className);
      }
/*
        Map<String, PsiClass> typeAlias = getTypeAlias(psiElement);
        if (typeAlias.containsKey(className)) {
            return typeAlias.get(className);
        }
*/
        return psiManager.findClass(className, GlobalSearchScope.allScope(project));
    }
    /**
     * find PsiClass according to class full name
     *
     * @param project   project object
     * @param className any name of shortcut, type alias or java class
     * @return PsiClass object
     */
    public static PsiClass getPsiClass(PsiElement psiElement, String className) {
        Project project = psiElement.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        //short cut
        if (classShortcuts.containsKey(className)) {
            return psiManager.findClass(classShortcuts.get(className), GlobalSearchScope.allScope(project));
        }
        //type alias
        Map<String, PsiClass> typeAlias = getTypeAlias(psiElement);
        if (typeAlias.containsKey(className)) {
            return typeAlias.get(className);
        }
        return psiManager.findClass(className, GlobalSearchScope.allScope(project));
    }
}
