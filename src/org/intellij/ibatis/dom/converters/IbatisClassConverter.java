package org.intellij.ibatis.dom.converters;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * converter for parameter class reference
 *
 * @author Jacky
 */
public class IbatisClassConverter extends Converter<PsiClass> {
    @Nullable public PsiClass fromString(@Nullable @NonNls String className, ConvertContext convertContext) {
        return StringUtil.isNotEmpty(className) ? IbatisClassShortcutsReferenceProvider.getPsiClass(convertContext.getXmlElement(), className) : null;
    }

    public String toString(@Nullable PsiClass psiClass, ConvertContext convertContext) {
        return psiClass != null ? psiClass.getName() : "";
    }
}
