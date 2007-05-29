package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.psi.*;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.*;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * statement id reference
 */
public class StatementIdReferenceProvider extends BaseReferenceProvider {
  @NotNull
  public PsiReference[] getReferencesByElement(final PsiElement psiElement) {
    if (!(psiElement instanceof PsiLiteralExpression)) return PsiReference.EMPTY_ARRAY;
    PsiElement parent = psiElement.getParent().getParent();
    if (parent == null || !(parent instanceof PsiMethodCallExpression)) {
      return PsiReference.EMPTY_ARRAY;
    }
    PsiMethod psiMethod = ((PsiMethodCallExpression) parent).resolveMethod();
    if (psiMethod == null) return PsiReference.EMPTY_ARRAY;
    String methodPrefix = psiMethod.getName();
    if (methodPrefix.length() > 0) {
      methodPrefix = methodPrefix.substring(0, 6);
    }
    if (!SqlClientElementFilter.operationMethods.contains(methodPrefix)) return PsiReference.EMPTY_ARRAY;
    return new PsiReference[]{new StatementIdReference(methodPrefix, (PsiLiteralExpression) psiElement, false)};
  }

  public class StatementIdReference extends PsiReferenceBase<PsiLiteralExpression> {
    private String methodPrefix = null;

    public StatementIdReference(String methodPrefix, PsiLiteralExpression expression, boolean soft) {
      super(expression, soft);
      this.methodPrefix = methodPrefix;
    }

    @Nullable
    public PsiElement resolve() {
      IbatisManager manager = IbatisManager.getInstance();
      String statementId = getCanonicalText();
      if ("insert".equals(methodPrefix)) {
        Insert insert = manager.getAllInsert(getElement()).get(statementId);
        return insert == null ? null : insert.getXmlTag();
      } else if ("update".equals(methodPrefix)) {//updaet可以调用delete和procedure

        Update update = manager.getAllUpdate(getElement()).get(statementId);
        if (update != null) {
          return update.getXmlTag();
        }
        Delete delete = manager.getAllDelete(getElement()).get(statementId);
        if (delete != null) {
          return delete.getXmlTag();
        }
        Procedure procedure = manager.getAllProcedure(getElement()).get(statementId);
        if (procedure != null) {
          return procedure.getXmlTag();
        }
          Insert insert = manager.getAllInsert(getElement()).get(statementId);
          if(insert!=null)
          {
              return insert.getXmlTag();
          }
        return null;
//                return update == null ? null : update.getXmlTag();
      } else if ("delete".equals(methodPrefix)) {
        Delete delete = manager.getAllDelete(getElement()).get(statementId);
        return delete == null ? null : delete.getXmlTag();
      } else {
        Select select = manager.getAllSelect(getElement()).get(statementId);
        if (select != null) return select.getXmlTag();
        Statement statement = manager.getAllStatement(getElement()).get(statementId);
        if (statement != null) return statement.getXmlTag();
        Procedure procedure = manager.getAllProcedure(getElement()).get(statementId);
        return procedure == null ? null : procedure.getXmlTag();
      }
    }


    public Object[] getVariants() {
      List<Object> variants = new ArrayList<Object>();
      IbatisManager manager = IbatisManager.getInstance();
      if ("insert".equals(methodPrefix)) {      //insert
        Set<String> allInsert = manager.getAllInsert(getElement()).keySet();
        for (String insert : allInsert) {
          variants.add(LookupValueFactory.createLookupValue(insert, IbatisConstants.SQLMAP_INSERT));
        }
      } else if ("update".equals(methodPrefix)) {   //update
        Set<String> allUpate = manager.getAllUpdate(getElement()).keySet();
        for (String update : allUpate) {
          variants.add(LookupValueFactory.createLookupValue(update, IbatisConstants.SQLMAP_UPDATE));
        }
        Set<String> allDelete = manager.getAllDelete(getElement()).keySet();
        for (String delete : allDelete) {
          variants.add(LookupValueFactory.createLookupValue(delete, IbatisConstants.SQLMAP_DELETE));
        }
        Set<String> allProcedure = manager.getAllProcedure(getElement()).keySet();
        for (String procedure : allProcedure) {
          variants.add(LookupValueFactory.createLookupValue(procedure, IbatisConstants.SQLMAP_PROCEDURE));
        }
       Set<String> allInsert=manager.getAllInsert(getElement()).keySet();
          for (String insert : allInsert) {
              variants.add(LookupValueFactory.createLookupValue(insert, IbatisConstants.SQLMAP_INSERT));
          }
      } else if ("delete".equals(methodPrefix)) {    //delete
        Set<String> allDelete = manager.getAllDelete(getElement()).keySet();
        for (String delete : allDelete) {
          variants.add(LookupValueFactory.createLookupValue(delete, IbatisConstants.SQLMAP_DELETE));
        }
      } else {  //select and statement
        Set<String> allSelect = manager.getAllSelect(getElement()).keySet();
        for (String select : allSelect) {
          variants.add(LookupValueFactory.createLookupValue(select, IbatisConstants.SQLMAP_SELECT));
        }
        Set<String> allStatement = manager.getAllStatement(getElement()).keySet();
        for (String statement : allStatement) {
          variants.add(LookupValueFactory.createLookupValue(statement, IbatisConstants.SQLMAP_STATEMENT));
        }
        Set<String> allProcedure = manager.getAllProcedure(getElement()).keySet();
        for (String procedure : allProcedure) {
          variants.add(LookupValueFactory.createLookupValue(procedure, IbatisConstants.SQLMAP_PROCEDURE));
        }
      }
      return variants.toArray();
    }

    public boolean isSoft() {
      return false;
    }
  }
}

