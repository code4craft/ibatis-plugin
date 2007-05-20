package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
                        PsiClass referencedClass = psiClass;
                        String referencePath = getCanonicalText();
                        String methodName = "set" + StringUtil.capitalize(referencePath);
                        if (referencePath.contains(".")) {
                            String fieldName = referencePath.substring(0, referencePath.lastIndexOf('.'));
                            methodName = "set" + StringUtil.capitalize(referencePath.substring(referencePath.lastIndexOf('.') + 1));
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
                        String newFieldName = StringUtil.decapitalize(newElementName.replace("set", ""));
                        if (!referencePath.contains(".")) {     //flat field
                            ((XmlAttribute) xmlAttributeValue.getParent()).setValue(StringUtil.decapitalize(newElementName.replace("set", "")));
                        } else //deep field
                        {
                            String field1 = referencePath.substring(0, referencePath.indexOf("."));
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
                        List<String> setterMethods = getAllSetterMethods(psiClass, getCanonicalText());
                        List<Object> variants = new ArrayList<Object>();
                        for (String setterMethod : setterMethods) {
                            variants.add(LookupValueFactory.createLookupValue(setterMethod, IbatisConstants.CLASS_FIELD));
                        }
                        return variants.toArray();
                    }

                    public boolean isSoft() {
                        return false;
                    }
                };
        } else {
            psiClass = getPsiClassForDynamicProperty(xmlTag, xmlAttributeValue);
            if (psiClass != null)
                psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
                    @Nullable public PsiElement resolve() {
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
                        List<String> setterMethods = getAllGetterMethods(psiClass, getCanonicalText().replace("IntellijIdeaRulezzz ", ""));
                        List<Object> variants = new ArrayList<Object>();
                        for (String setterMethod : setterMethods) {
                            variants.add(LookupValueFactory.createLookupValue(setterMethod, IbatisConstants.CLASS_FIELD));
                        }
                        return variants.toArray();
                    }

                    public boolean isSoft() {
                        return false;
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
    private PsiClass findGetterMethodReturnType(PsiClass psiClass, String methodName) {
        PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
        //getter method find for current
        if (methods.length > 0) {
            PsiMethod psiGetterMethod = methods[0];
            PsiType returnType = psiGetterMethod.getReturnType();
            if (returnType instanceof PsiClassType) {
                PsiClass psiFieldClass = ((PsiClassType) returnType).resolve();
                if (psiFieldClass != null)
                    return psiFieldClass;
            }
        }
        return null;
    }

    public List<String> getAllSetterMethods(PsiClass psiClass, String currentMethodName) {
        List<String> methodNames = new ArrayList<String>();
        PsiMethod[] psiMethods = null;
        String prefix = "";
        //flat field
        if (!currentMethodName.contains(".")) {
            psiMethods = psiClass.getAllMethods();
        } else {
            prefix = currentMethodName.substring(0, currentMethodName.lastIndexOf('.'));
            String getterMethod = "get" + StringUtil.capitalize(prefix);
            PsiClass psiFieldClass = findGetterMethodReturnType(psiClass, getterMethod);
            if (psiFieldClass != null) {
                psiMethods = psiFieldClass.getAllMethods();
                prefix = prefix + ".";
            }
        }
        if (psiMethods != null && psiMethods.length > 0) {
            for (PsiMethod psiMethod : psiMethods) {
                String methodName = psiMethod.getName();
                if (methodName.startsWith("set") && psiMethod.getParameterList().getParametersCount() == 1) {
                    methodNames.add(prefix + StringUtil.decapitalize(methodName.replaceFirst("set", "")));
                }
            }
        }
        return methodNames;
    }

    public List<String> getAllGetterMethods(PsiClass psiClass, String currentMethodName) {
        List<String> methodNames = new ArrayList<String>();
        PsiMethod[] psiMethods = null;
        String prefix = "";
        //flat field
        if (!currentMethodName.contains(".")) {
            psiMethods = psiClass.getAllMethods();
        } else {
            prefix = currentMethodName.substring(0, currentMethodName.lastIndexOf('.'));
            String getterMethod = "get" + StringUtil.capitalize(prefix);
            PsiClass psiFieldClass = findGetterMethodReturnType(psiClass, getterMethod);
            if (psiFieldClass != null) {
                psiMethods = psiFieldClass.getAllMethods();
                prefix = prefix + ".";
            }
        }
        if (psiMethods != null && psiMethods.length > 0) {
            for (PsiMethod psiMethod : psiMethods) {
                String methodName = psiMethod.getName();
                if (methodName.startsWith("get") && psiMethod.getParameterList().getParametersCount() == 0) {
                    methodNames.add(prefix + StringUtil.decapitalize(methodName.replaceFirst("get", "")));
                }
            }
        }
        return methodNames;
    }

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
     * @return
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
