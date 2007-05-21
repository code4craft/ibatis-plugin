package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Delete;
import org.intellij.ibatis.dom.sqlMap.Insert;
import org.intellij.ibatis.dom.sqlMap.Procedure;
import org.intellij.ibatis.dom.sqlMap.Update;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * cache model reference provider
 */
public class CacheModelStatementReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
            public boolean isSoft() {
                return false;
            }

            @Nullable public PsiElement resolve() {
               String statementId = getNameSpaceCanonicalText();

              Map<String, Delete> allDelete = IbatisManager.getInstance().getAllDelete(getElement());
              Delete delete = allDelete.get(statementId);
              if(delete != null) {
                return delete.getId().getXmlAttribute();
              }

              Map<String, Update> allUpdate = IbatisManager.getInstance().getAllUpdate(getElement());
              Update update = allUpdate.get(statementId);
              if(update != null) {
                return update.getId().getXmlAttribute();
              }

              Map<String, Insert> allInsert = IbatisManager.getInstance().getAllInsert(getElement());
              Insert insert = allInsert.get(statementId);
              if(insert != null) {
                return insert.getId().getXmlAttribute();
              }

              Map<String, Procedure> allProcedure = IbatisManager.getInstance().getAllProcedure(getElement());
              Procedure procedure = allProcedure.get(statementId);
              if(procedure != null) {
                return procedure.getId().getXmlAttribute();
              }


                return null;
            }

            public Object[] getVariants() {
              List<String> values = new ArrayList<String>();

              Map<String, Delete> allDelete = IbatisManager.getInstance().getAllDelete(getElement());
              values.addAll(allDelete.keySet());
              Map<String, Insert> allInsert = IbatisManager.getInstance().getAllInsert(getElement());
              values.addAll(allInsert.keySet());
              Map<String, Update> allUpdate = IbatisManager.getInstance().getAllUpdate(getElement());
              values.addAll(allUpdate.keySet());
              Map<String, Procedure> allProcedure = IbatisManager.getInstance().getAllProcedure(getElement());
              values.addAll(allProcedure.keySet());

              return values.toArray();
            }
        };
        return new PsiReference[]{psiReference};
    }

}