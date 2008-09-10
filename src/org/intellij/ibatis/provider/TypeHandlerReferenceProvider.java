package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import com.intellij.openapi.project.Project;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.configuration.TypeHandler;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * typeHandler reference provider
 *
 * @author linux_china@hotmail.com
 */
public class TypeHandlerReferenceProvider extends WrappedReferenceProvider {

    public TypeHandlerReferenceProvider() {
        super(new JavaClassReferenceProvider());
    }

    public static Map<String, TypeHandler> getAllTypeHandler(PsiElement psiElement) {
        IbatisManager manager = IbatisManager.getInstance();
        return manager.getAllTypeHandlers(psiElement);
    }

    @Override
    @NotNull
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        PsiReference[] references = myProvider.getReferencesByElement(psiElement,processingContext);
        if (references.length < 1) return references;
        return new PsiReference[]{new WrappedPsiReference(references[references.length - 1]) {
            public PsiElement resolve() {
                String className = getCanonicalText();
                if (!className.contains(".")) {  // type handler javaType
                    Map<String, TypeHandler> allTypeHandler = getAllTypeHandler(psiElement);
                    if (allTypeHandler.containsKey(className)) {
                        return allTypeHandler.get(className).getXmlTag();
                    }
                }
                return super.resolve();
            }

            public Object[] getVariants() {
                List<Object> variants = new ArrayList<Object>();
                List<Object> classNames = Arrays.asList(super.getVariants());
                variants.addAll(classNames);
                if (!getCanonicalText().contains(".")) {  //not  interal type handler
                    Map<String, TypeHandler> allTypeHandler = getAllTypeHandler(psiElement);
                    for (String javaType : allTypeHandler.keySet()) {
                        variants.add(LookupValueFactory.createLookupValue(javaType, IbatisConstants.TYPE_ALIAS));
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
}
