package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;

/**
 * class reference provider  for iBATIS  including internal shortcuts and type alias
 *
 * @author Jacky
 */
public class IbatisClassShortcutsReferenceProvider extends WrappedReferenceProvider {
    public static Map<String, String> classShortcuts = new HashMap<String, String>();

    static {
		classShortcuts.put("arraylist", ArrayList.class.getName());
		classShortcuts.put("boolean", Boolean.class.getName());
		classShortcuts.put("byte", Byte.class.getName());
		classShortcuts.put("collection", Collection.class.getName());
		classShortcuts.put("cursor", java.sql.ResultSet.class.getName());
		classShortcuts.put("date", Date.class.getName());
		classShortcuts.put("decimal", BigDecimal.class.getName());
		classShortcuts.put("double", Double.class.getName());
		classShortcuts.put("float", Float.class.getName());
		classShortcuts.put("hashmap", HashMap.class.getName());
		classShortcuts.put("int", Integer.class.getName());
		classShortcuts.put("integer", Integer.class.getName());
		classShortcuts.put("iterator", Iterator.class.getName());
		classShortcuts.put("list", List.class.getName());
		classShortcuts.put("long", Long.class.getName());
		classShortcuts.put("map", Map.class.getName());
		classShortcuts.put("object", Object.class.getName());
		classShortcuts.put("short", Short.class.getName());
		classShortcuts.put("string", String.class.getName());
		
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
     * @param psiElement psi element
     * @param className any name of shortcut, type alias or Java class
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
     * @param psiElement PsiElement object
     * @param className any name of shortcut, type alias or Java class
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

    /**
     * validate a psiClass is domain class
     *
     * @param className class name
     * @return domain class mark
     */
    public static boolean isDomain(String className) {
        className=className.toLowerCase();
        if (className.equals("integer")) className = "int";
        if (className.equals("BigDecimal")) className = "decimal";
        return !IbatisClassShortcutsReferenceProvider.classShortcuts.containsKey(className);
    }
}
