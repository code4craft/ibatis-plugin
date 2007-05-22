package org.intellij.ibatis.provider;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.intellij.ibatis.IbatisConfigurationModel;
import org.intellij.ibatis.IbatisManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * xml attribute value psi reference, a XmlAttributeValue object should be supplied
 */
public class XmlAttributeValuePsiReference implements PsiReference {
  private XmlAttributeValue xmlAttributeValue;

  public XmlAttributeValuePsiReference(XmlAttributeValue xmlAttributeValue) {
    this.xmlAttributeValue = xmlAttributeValue;
  }

  public PsiElement getElement() {
    return this.xmlAttributeValue;
  }

  public TextRange getRangeInElement() {
    return new TextRange(1, xmlAttributeValue.getValue().length() + 1);
  }

  @Nullable
  public PsiElement resolve() {
    return null;
  }

  public String getCanonicalText() {
    return xmlAttributeValue.getValue();
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return null;
  }

  public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException {
    return null;
  }

  public boolean isReferenceTo(PsiElement element) {
    return element == resolve();
  }

  public Object[] getVariants() {
    return new Object[0];
  }

  public boolean isSoft() {
    return true;
  }

  /**
   * 取得带有名字空间的引用ID号。
   *
   * @param psiElement 当前节点
   * @return 引用ID
   */
  @NotNull
  public String getReferenceId(PsiElement psiElement) {
    Module module = ModuleUtil.findModuleForPsiElement(psiElement.getContainingFile());
    if (module != null) {
      IbatisConfigurationModel model = IbatisManager.getInstance().getConfigurationModel(module);
      if (model != null) {
        if (model.isUseStatementNamespaces()) {
          return getCanonicalTextWithNameSpace();
        }
      }
    }
    return getCanonicalText();
  }

  /**
   * 取得带有名称空间的引用ID。如果id已经有名称空间，则直接返回该ID，否则，就会加上名称空间
   * @return 引用ID
   */
  @NotNull
  public String getCanonicalTextWithNameSpace() {
    String id = getCanonicalText();
    if (id.indexOf('.') == -1) {
      XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) getElement();
      XmlFile psiFile = (XmlFile) xmlAttributeValue.getContainingFile();
      if (psiFile != null) {
        XmlDocument document = psiFile.getDocument();
        if (document != null) {
          XmlTag rootTag = document.getRootTag();
          if (rootTag != null) {
            String namespace = rootTag.getAttributeValue("namespace");
            if (namespace != null && namespace.length() > 0) {
              return namespace + "." + id;
            }
          }
        }
      }
    }
    return id;
  }

}
