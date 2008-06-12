package org.intellij.ibatis.provider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.codeInsight.lookup.LookupValueFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.intellij.ibatis.util.IbatisConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * reference provider for table field name
 */
public class JavadocTableColumnReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        final PsiDocTag docTag = (PsiDocTag) psiElement;
        final Project project = docTag.getProject();
        return new PsiReference[]{new PsiReference() {
            public PsiElement getElement() {
                return docTag;
            }

            public TextRange getRangeInElement() {
                int offset = ElementManipulators.getOffsetInElement(docTag);
                return new TextRange(offset, docTag.getTextLength());
            }

            @Nullable public PsiElement resolve() {
                return null;
            }

            public String getCanonicalText() {
                return docTag.getText().trim();
            }

            public PsiElement handleElementRename(String s) throws IncorrectOperationException {
                return null;
            }

            public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
                return null;
            }

            public boolean isReferenceTo(PsiElement psiElement) {
                return false;
            }

            public Object[] getVariants() {
                List<Object> variants = new ArrayList<Object>();
                PsiClass psiClass = PsiTreeUtil.getParentOfType(docTag, PsiClass.class);
                if (psiClass != null) {
                    DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(psiClass);
                    if (tableData != null) {
                        for (DatabaseTableFieldData field : tableData.getFields()) {
                            String fieldName = field.getName().toLowerCase();
                            if (field.isPrimary()) {       //pk
                                variants.add(LookupValueFactory.createLookupValueWithHint(fieldName, IbatisConstants.DATABASE_PK_FIELD, TableColumnReferenceProvider.getJdbcTypeName(field.getJdbcType())));
                            } else {   //common column
                                variants.add(LookupValueFactory.createLookupValueWithHint(fieldName, IbatisConstants.DATABASE_COMMON_FIELD, TableColumnReferenceProvider.getJdbcTypeName(field.getJdbcType())));
                            }
                        }
                    }
                }
                return variants.toArray();
            }

            public boolean isSoft() {
                return true;
            }
        }};
    }


}