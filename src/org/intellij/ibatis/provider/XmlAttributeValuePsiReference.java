package org.intellij.ibatis.provider;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
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

  public String getNameSpaceCanonicalText() {
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
            if (namespace != null) {
              return namespace + "." + id;
            }
          }
        }
      }
    }
    return id;
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
}
