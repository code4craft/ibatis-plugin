package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.*;
import com.intellij.util.IncorrectOperationException;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * filed access method reference  provider
 */
public class FieldAccessMethodReferenceProvider extends BaseReferenceProvider {

    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        final XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        final XmlTag xmlTag = (XmlTag) xmlAttributeValue.getParent().getParent();   //result or parameter tag
        final PsiClass psiClass;
        XmlAttributeValuePsiReference psiReference = null;
        if (xmlTag.getName().equals("result") || xmlTag.getName().equals("parameter") || xmlTag.getName().equals("resultMap")) {
            psiClass = getPsiClassForMap(xmlTag, xmlAttributeValue);
            if (psiClass != null)
                psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
                    @Nullable public PsiElement resolve() {
                        if (!IbatisClassShortcutsReferenceProvider.isDomain(psiClass.getName())) {    //none domain class, validate is not necessary, such as hashmap
                            return null;
                        }
                        PsiClass referencedClass = psiClass;
                        String[] referencePath = getCanonicalText().split("\\.");
                        String methodName = "set" + StringUtil.capitalize(referencePath[referencePath.length - 1]);
                        for (int i = 0; i < referencePath.length - 1; i++) {
                            referencedClass = findGetterMethodReturnType(referencedClass, "get" + StringUtil.capitalize(referencePath[i]));
                            if (referencedClass == null) break;
                        }
                        if (referencedClass != null) {
                            PsiMethod[] methods = referencedClass.findMethodsByName(methodName, true);
                            if (methods.length > 0) return methods[0];
                        }
                        return null;
                    }

                    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
                        String referencePath = getCanonicalText();
                        String newFieldName = StringUtil.decapitalize(newElementName.replace("set", ""));
                        if (!referencePath.contains(".")) {     //flat field
                            ((XmlAttribute) xmlAttributeValue.getParent()).setValue(StringUtil.decapitalize(newElementName.replace("set", "")));
                        } else //deep field
                        {
                            String field1 = referencePath.substring(0, referencePath.lastIndexOf("."));
                            String newReferencePath;
                            if (psiClass.findMethodsByName("set" + StringUtil.capitalize(field1), true).length > 0) {   //field2 changed
                                newReferencePath = field1 + "." + newFieldName;
                            } else     //field1 changed
                            {
                                newReferencePath = referencePath.replace(field1, newFieldName);
                            }
                            ((XmlAttribute) xmlAttributeValue.getParent()).setValue(newReferencePath);
                        }
                        return resolve();
                    }

                    public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException {
                        return super.bindToElement(element);
                    }

                    public boolean isReferenceTo(PsiElement element) {
                        return super.isReferenceTo(element);
                    }

                    public Object[] getVariants() {
                        if ("Map".equals(psiClass.getName())) {
                            return null;
                        }
                        Map<String, String> setterMethods = getAllSetterMethods(psiClass, getCanonicalText());
                        List<Object> variants = new ArrayList<Object>();
                        for (Map.Entry<String, String> entry : setterMethods.entrySet()) {
                            variants.add(LookupValueFactory.createLookupValueWithHint(entry.getKey(), IbatisConstants.CLASS_FIELD, entry.getValue()));
                        }
                        return variants.toArray();
                    }

                    public boolean isSoft() {
                        return "Map".equals(psiClass.getName());
                    }
                };
        } else {
            psiClass = getPsiClassForDynamicProperty(xmlTag, xmlAttributeValue);
            if (psiClass != null)
                psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
                    @Nullable public PsiElement resolve() {
                        if ("Map".equals(psiClass.getName())) {
                            return null;
                        }
                        PsiClass referencedClass = psiClass;
                        String referencePath = getCanonicalText().replace("IntellijIdeaRulezzz ", "");
                        String methodName = "get" + StringUtil.capitalize(referencePath);
                        if (referencePath.contains(".")) {
                            String fieldName = referencePath.substring(0, referencePath.lastIndexOf('.'));
                            methodName = "get" + StringUtil.capitalize(referencePath.substring(referencePath.lastIndexOf('.') + 1));
                            referencedClass = findGetterMethodReturnType(psiClass, "get" + StringUtil.capitalize(fieldName));
                        }
                        if (referencedClass != null) {
                            PsiMethod[] methods = referencedClass.findMethodsByName(methodName, true);
                            if (methods.length > 0) return methods[0];
                        }
                        return null;
                    }

                    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
                        String referencePath = getCanonicalText();
                        String newFieldName = StringUtil.decapitalize(newElementName.replace("get", ""));
                        if (!referencePath.contains(".")) {     //flat field
                            ((XmlAttribute) xmlAttributeValue.getParent()).setValue(StringUtil.decapitalize(newElementName.replace("get", "")));
                        } else //deep field
                        {
                            String field1 = referencePath.substring(0, referencePath.indexOf("."));
                            String newReferencePath;
                            if (psiClass.findMethodsByName("get" + StringUtil.capitalize(field1), true).length > 0) {   //field2 changed
                                newReferencePath = field1 + "." + newFieldName;
                            } else     //field1 changed
                            {
                                newReferencePath = referencePath.replace(field1, newFieldName);
                            }
                            ((XmlAttribute) xmlAttributeValue.getParent()).setValue(newReferencePath);
                        }
                        return resolve();
                    }

                    public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException {
                        return super.bindToElement(element);
                    }

                    public boolean isReferenceTo(PsiElement element) {
                        return super.isReferenceTo(element);
                    }

                    public Object[] getVariants() {
                        if ("Map".equals(psiClass.getName())) {
                            return null;
                        }
                        Map<String, String> setterMethods = getAllGetterMethods(psiClass, getCanonicalText().replace("IntellijIdeaRulezzz ", ""));
                        List<Object> variants = new ArrayList<Object>();
                        for (String setterMethod : setterMethods.keySet()) {
                            variants.add(LookupValueFactory.createLookupValueWithHint(setterMethod, IbatisConstants.CLASS_FIELD, setterMethods.get(setterMethod)));
                        }
                        return variants.toArray();
                    }

                    public boolean isSoft() {
                        return "Map".equals(psiClass.getName());
                    }
                };
        }
        if (psiReference == null) return PsiReference.EMPTY_ARRAY;
        return new PsiReference[]{psiReference};
    }

    /**
     * find getter method return type
     *
     * @param psiClass   PsiClass
     * @param methodName getter method name
     * @return PsiClass
     */
    @Nullable public static PsiClass findGetterMethodReturnType(PsiClass psiClass, String methodName) {
        PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
        //getter method find for current
        if (methods.length > 0) {
            PsiMethod psiGetterMethod = methods[0];
            PsiType returnType = psiGetterMethod.getReturnType();
            if (returnType != null)
                return IbatisClassShortcutsReferenceProvider.getPsiClass(psiClass, returnType.getCanonicalText());
        }
        return null;
    }

    /**
     * get all  set method for psiClass with type added
     *
     * @param psiClass          PsiClass object
     * @param currentMethodName current set method
     * @return set method list  without prefix
     */
    public static Map<String, String> getAllSetterMethods(PsiClass psiClass, String currentMethodName) {
        Map<String, String> methodNames = new HashMap<String, String>();
        PsiMethod[] psiMethods;
        String prefix = "";
        //flat field
        if (!currentMethodName.contains(".")) {
            psiMethods = psiClass.getAllMethods();
        } else {
            String[] path = (currentMethodName + " ").split("\\.");   //space added to avoid "." ended property
            PsiClass tempClass = psiClass;
            for (int i = 0; i < path.length - 1; i++) {
                String getterMethod = "get" + StringUtil.capitalize(path[i]);
                tempClass = findGetterMethodReturnType(tempClass, getterMethod);
                if (tempClass == null) break;
                prefix = prefix + path[i] + ".";
            }
            psiMethods = tempClass != null ? tempClass.getAllMethods() : null;
        }
        if (psiMethods != null && psiMethods.length > 0) {
            for (PsiMethod psiMethod : psiMethods) {
                String methodName = psiMethod.getName();
                if (methodName.startsWith("set") && psiMethod.getParameterList().getParametersCount() == 1) {
                    String name = prefix + StringUtil.decapitalize(methodName.replaceFirst("set", ""));
                    String type = psiMethod.getParameterList().getParameters()[0].getType().getPresentableText();
                    methodNames.put(name, type);
                }
            }
        }
        return methodNames;
    }

    /**
     * get all get method in psi class with return type
     *
     * @param psiClass          PsiClass object
     * @param currentMethodName current methodName for children
     * @return get method list without prefix
     */
    @SuppressWarnings({"ConstantConditions"}) @NotNull
    public static Map<String, String> getAllGetterMethods(PsiClass psiClass, String currentMethodName) {
        Map<String, String> methodNames = new HashMap<String, String>();
        PsiMethod[] psiMethods = null;
        String prefix = "";
        //flat field
        if (!currentMethodName.contains(".")) {
            psiMethods = psiClass.getAllMethods();
        } else {
            String[] path = (currentMethodName + " ").split("\\.");   //space added to avoid "." ended property
            PsiClass tempClass = psiClass;
            for (int i = 0; i < path.length - 1; i++) {
                String getterMethod = "get" + StringUtil.capitalize(path[i]);
                tempClass = findGetterMethodReturnType(tempClass, getterMethod);
                if (tempClass == null) break;
                prefix = prefix + path[i] + ".";
            }
        }
        if (psiMethods != null && psiMethods.length > 0) {
            for (PsiMethod psiMethod : psiMethods) {
                String methodName = psiMethod.getName();
                if (methodName.startsWith("get") && psiMethod.getParameterList().getParametersCount() == 0) {
                    String name = prefix + StringUtil.decapitalize(methodName.replaceFirst("get", ""));
                    String type = psiMethod.getReturnType().getPresentableText();
                    methodNames.put(name, type);
                }
                if (methodName.startsWith("is") && psiMethod.getParameterList().getParametersCount() == 0) {
                    String name = prefix + StringUtil.decapitalize(methodName.replaceFirst("is", ""));
                    String type = psiMethod.getReturnType().getPresentableText();
                    methodNames.put(name, type);
                }
            }
        }
        methodNames.remove("class");    //getClass is controled by JVM
        return methodNames;
    }

    /**
     * get the psi class for dynamic property
     *
     * @param xmlTag            xml tag
     * @param xmlAttributeValue xml attribute value
     * @return psi class
     */
    public PsiClass getPsiClassForDynamicProperty(XmlTag xmlTag, XmlAttributeValue xmlAttributeValue) {
        XmlTag parentTag = xmlTag.getParentTag();
        if (parentTag != null) {
            if (parentTag.getAttribute("parameterClass") != null) {
                String className = parentTag.getAttributeValue("parameterClass");
                return IbatisClassShortcutsReferenceProvider.getPsiClass(xmlAttributeValue, className);
            } else if (parentTag.getAttribute("parameterMap") != null) {
                String parameterMapId = parentTag.getAttributeValue("parameterMap");
                return IbatisManager.getInstance().getAllParameterMap(xmlAttributeValue).get(parameterMapId);
            } else {
                return getPsiClassForDynamicProperty(parentTag, xmlAttributeValue);
            }
        }
        return null;
    }

    /**
     * get Psi Class for resultMap for parameterMap
     *
     * @param xmlTag            tag for xml attribute
     * @param xmlAttributeValue xml attribute value
     * @return psiClass
     */
    public PsiClass getPsiClassForMap(XmlTag xmlTag, XmlAttributeValue xmlAttributeValue) {
        XmlTag parentTag = xmlTag.getParentTag();   //resultMap or parameterMap element
        if (xmlTag.getName().equals("resultMap")) parentTag = xmlTag;  //resultMap's groupBy
        if (parentTag != null && parentTag.getAttribute("class") != null) {
            String className = parentTag.getAttributeValue("class");
            return IbatisClassShortcutsReferenceProvider.getPsiClass(xmlAttributeValue, className);
        }
        return null;
    }

}
