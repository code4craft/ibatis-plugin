package org.intellij.ibatis.provider;

import com.intellij.psi.*;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * statement id reference
 */
public class StatementIdReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(final PsiElement psiElement) {
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

        @Nullable public PsiElement resolve() {
            IbatisManager manager = IbatisManager.getInstance();
            String statementId = getCanonicalText();
            if ("insert".equals(methodPrefix)) {
                Insert insert = manager.getAllInsert(getElement()).get(statementId);
                return insert == null ? null : insert.getXmlTag();
            } else if ("update".equals(methodPrefix)) {
                Update update = manager.getAllUpdate(getElement()).get(statementId);
                return update == null ? null : update.getXmlTag();
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
            List<String> variants = new ArrayList<String>();
            IbatisManager manager = IbatisManager.getInstance();
            if ("insert".equals(methodPrefix)) {      //insert
                variants.addAll(manager.getAllInsert(getElement()).keySet());
            } else if ("update".equals(methodPrefix)) {   //update
                variants.addAll(manager.getAllUpdate(getElement()).keySet());
            } else if ("delete".equals(methodPrefix)) {    //delete
                variants.addAll(manager.getAllDelete(getElement()).keySet());
            } else {  //select and statement
                variants.addAll(manager.getAllSelect(getElement()).keySet());
                variants.addAll(manager.getAllStatement(getElement()).keySet());
                variants.addAll(manager.getAllProcedure(getElement()).keySet());
            }
            return variants.toArray();
        }

        public boolean isSoft() {
            return false;
        }
    }
}

