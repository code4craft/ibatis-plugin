package org.intellij.ibatis.intention;

import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.ParameterMap;
import org.intellij.ibatis.provider.TableColumnReferenceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * intention action to create results for result map
 */
public class GenerateParametersForParameterMapAction extends PsiIntentionBase {

  private static final String NAME = "GenerateParametersForParameterMap";
  private static final String TEXT = "Generate Parameters for ParameterMap element.";

  @NotNull
  public String getFamilyName() {
    return NAME;
  }

  protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
    if (file instanceof XmlFile && element instanceof XmlTag) {
      XmlTag xmlTag = (XmlTag) element;
      if (xmlTag.getName().equals("parameterMap") && xmlTag.findSubTags("parameter").length == 0) {
        DomElement domElement = DomManager.getDomManager(project).getDomElement(xmlTag);
        if (domElement != null && domElement instanceof ParameterMap) {
          return true;
        }
      }
    }
    return false;
  }

  @NotNull
  public String getText() {
    return TEXT;
  }

  protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
    if (file instanceof XmlFile && element instanceof XmlTag) {
      XmlTag xmlTag = (XmlTag) element;
      if (xmlTag.getName().equals("parameterMap") && xmlTag.findSubTags("parameter").length == 0) {
        DomElement domElement = DomManager.getDomManager(project).getDomElement(xmlTag);
        if (domElement != null && domElement instanceof ParameterMap) {
          ParameterMap resultMap = (ParameterMap) domElement;
          PsiClass psiClass = resultMap.getClazz().getValue();
          if (psiClass != null) {
            PsiElementFactory psiElementFactory = PsiManager.getInstance(project).getElementFactory();
            PsiMethod[] psiMethods = psiClass.getMethods();
            try {
              xmlTag.getValue().setText(null);
              for (PsiMethod psiMethod : psiMethods) {
                if ((psiMethod.getName().startsWith("get") || psiMethod.getName().startsWith("get") || psiMethod.getName().startsWith("is")) && psiMethod.getParameterList().getParametersCount() == 0) {
                  DatabaseTableFieldData tableFieldData = TableColumnReferenceProvider.getDatabaseTableFieldData(psiMethod);
                  PsiType psiType = psiMethod.getReturnType();
                  String propertyName = StringUtil.decapitalize(psiMethod.getName().replace("get", "").replace("is", ""));
                  StringBuilder builder = new StringBuilder();
                  builder.append("<parameter property=\"").append(propertyName).append("\"");
                /*  if (tableFieldData != null) {   //setter method contains @column tag
                    builder.append(" column=\"").append(tableFieldData.getName()).append("\"");
                  } else {
                    builder.append(" column=\"").append(propertyName).append("\"");
                  }*/
                  if (PsiType.BOOLEAN.equals(psiType)) {
                    builder.append(" nullValue=\"false\"");
                  } else if (psiType instanceof PsiPrimitiveType) {
                    builder.append(" nullValue=\"0\"");
                  }
                  builder.append("/>");
                  xmlTag.add(psiElementFactory.createTagFromText(builder.toString()));

                }
              }
            } catch (Exception e) {
              // where do we go from here?
            }
          }
        }
      }
    }
  }
}