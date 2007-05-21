package org.intellij.ibatis.provider;

import com.intellij.openapi.module.impl.scopes.JdkScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangsy.cq@gmail.com
 *         <p/>
 *         cache model reference provider
 */
public class ParameterJdbcTypeReferenceProvider extends BaseReferenceProvider {

  private Object[] types;


  public ParameterJdbcTypeReferenceProvider(Project project) {
    init();
  }

  private void init() {
    List<String> types = new ArrayList<String>();
    types.add("ORACLECURSOR");//support oracle cursor

    Field[] declaredFields = java.sql.Types.class.getDeclaredFields();
    for (Field declaredField : declaredFields) {
      int modifiers = declaredField.getModifiers();
      if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
        if (declaredField.getType().equals(Integer.TYPE)) {
          types.add(declaredField.getName());
        }
      }
    }
    this.types = types.toArray();


  }

  @NotNull
  public PsiReference[] getReferencesByElement(PsiElement psiElement) {
    XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
    XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {

      public boolean isSoft() {
        String val = getCanonicalText();
        if ("ORACLECURSOR".equals(val)) {
          PsiField[] fields = getOraclePsiFields(getElement());
          if (fields != null) {
            return true;
          }
        }
        return false;
      }

      @Nullable
      public PsiElement resolve() {
        String val = getCanonicalText();

        PsiField[] fields = getJdbcTypePsiFields(getElement()); //先找标标准库
        if (fields != null) {
          for (PsiField type : fields) {
            if (val.equals(type.getName())) {
              return type;
            }
          }
        }

        if ("ORACLECURSOR".equals(val)) {
          fields = getOraclePsiFields(getElement());
          if (fields != null) {
            for (PsiField type : fields) {
              if ("CURSOR".equals(type.getName())) {
                return type;
              }
            }
          }
        }
        return super.resolve();
      }

      private PsiField[] getJdbcTypePsiFields(PsiElement psiElement) {
        Project project = psiElement.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiClass psiClass = psiManager.findClass("java.sql.Types", JdkScope.allScope(project));
        if (psiClass != null) {
          return psiClass.getAllFields();
        }
        return null;
      }

      private PsiField[] getOraclePsiFields(PsiElement psiElement) {
        Project project = psiElement.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiClass psiClass = psiManager.findClass("oracle.jdbc.OracleTypes", JdkScope.allScope(project));
        if (psiClass != null) {
          return psiClass.getAllFields();
        }
        return null;
      }

      public Object[] getVariants() {
        return types;
      }
    };
    return new PsiReference[]{psiReference};
  }

}