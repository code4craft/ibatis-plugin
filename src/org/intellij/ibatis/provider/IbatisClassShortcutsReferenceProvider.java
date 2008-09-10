package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
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
        classShortcuts.put("xml", "com.ibatis.sqlmap.engine.type.XmlTypeMarker");
    }

    public IbatisClassShortcutsReferenceProvider(Project project) {
        super(new JavaClassReferenceProvider(project));
    }

    public static Map<String, PsiClass> getTypeAlias(PsiElement psiElement) {
        IbatisManager manager = IbatisManager.getInstance();
        return manager.getAllTypeAlias(psiElement);
    }

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        PsiReference[] references = myProvider.getReferencesByElement(psiElement, processingContext);
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
                List<Object> variants = new ArrayList<Object>();
                List<Object> classNames = Arrays.asList(super.getVariants());
                variants.addAll(classNames);
                if (!getCanonicalText().contains(".")) {  //not internal type alias
                    Set<String> shortcuts = classShortcuts.keySet();
                    Set<String> typeAliasList = getTypeAlias(getElement()).keySet();
                    for (String shortcut : shortcuts) {
                        variants.add(LookupValueFactory.createLookupValue(shortcut, IbatisConstants.INTERNAL_CLASS));
                    }
                    for (String typeAlias : typeAliasList) {
                        variants.add(LookupValueFactory.createLookupValue(typeAlias, IbatisConstants.TYPE_ALIAS));
                    }
                    //filter some unnecessary package name in root path
                    for (Object className : classNames) {
                        if (className instanceof PsiPackage) {
                            PsiPackage psiPackage = (PsiPackage) className;
                            String packageName = psiPackage.getQualifiedName();
                            if (packageName.contains(".") || packageName.equalsIgnoreCase("META-INF")) {  //META-INF  and directory with "." are not necessary
                                variants.remove(psiPackage);
                            }
                        }
                    }
                }
                return variants.toArray();
            }

            public String getCanonicalText() {
                XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) getElement();
                return xmlAttributeValue.getValue();
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
     * @param className  any name of shortcut, type alias or Java class
     * @return PsiClass object
     */
    public static PsiElement getPsiElement(PsiElement psiElement, String className) {
        Project project = psiElement.getProject();
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        //short cut
        if (classShortcuts.containsKey(className)) {
            return javaPsiFacade.findClass(classShortcuts.get(className), GlobalSearchScope.allScope(project));
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
        return javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project));
    }

    /**
     * find PsiClass according to class full name
     *
     * @param psiElement PsiElement object
     * @param className  any name of shortcut, type alias or Java class
     * @return PsiClass object
     */
    public static PsiClass getPsiClass(PsiElement psiElement, String className) {
        Project project = psiElement.getProject();
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        if (className.endsWith("[]")) {
            className = className.substring(0, className.length() - 2);
        }
        //short cut
        if (classShortcuts.containsKey(className)) {
            return javaPsiFacade.findClass(classShortcuts.get(className), GlobalSearchScope.allScope(project));
        }
        //type alias
        Map<String, PsiClass> typeAlias = getTypeAlias(psiElement);
        if (typeAlias.containsKey(className)) {
            return typeAlias.get(className);
        }
        return javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project));
    }

    /**
     * validate a psiClass is domain class
     *
     * @param className class name
     * @return domain class mark
     */
    public static boolean isDomain(String className) {
        className = className.toLowerCase();
        if (className.equals("integer")) className = "int";
        if (className.equals("BigDecimal")) className = "decimal";
        return !IbatisClassShortcutsReferenceProvider.classShortcuts.containsKey(className);
    }
}
