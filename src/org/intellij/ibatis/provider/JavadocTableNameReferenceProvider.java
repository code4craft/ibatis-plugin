package org.intellij.ibatis.provider;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.javaee.dataSource.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.util.IncorrectOperationException;
import org.intellij.ibatis.facet.IbatisFacet;
import org.intellij.ibatis.util.IbatisConstants;
import org.intellij.ibatis.util.IbatisUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * reference provider for table name
 */
public class JavadocTableNameReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        final PsiDocTag docTag = (PsiDocTag) psiElement;
        final Project project = docTag.getProject();
        return new PsiReference[]{new PsiReference() {
            public PsiElement getElement() {
                return docTag;
            }

            public TextRange getRangeInElement() {
                int offset = PsiManager.getInstance(project).getElementManipulatorsRegistry().getOffsetInElement(docTag);
                return new TextRange(offset, docTag.getTextLength());
            }

            @Nullable public PsiElement resolve() {
                return null;
            }

            public String getCanonicalText() {
                return docTag.getText().trim();
            }
            @Nullable
            public PsiElement handleElementRename(String s) throws IncorrectOperationException {
                return null;
            }
            @Nullable
            public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
                return null;
            }

            public boolean isReferenceTo(PsiElement psiElement) {
                return false;
            }

            public Object[] getVariants() {
                DataSource dataSource = getDataSourceForIbatis(docTag);
                List<Object> variants = new ArrayList<Object>();
                if (dataSource != null) {
                    List<DatabaseTableData> tables = dataSource.getTables();
                    for (DatabaseTableData table : tables) {
                        variants.add(LookupValueFactory.createLookupValue(IbatisUtil.getTableNameWithoutSchema(table.getName()), IbatisConstants.DATABASE_TABLE));
                    }
                }
                return variants.toArray();
            }

            public boolean isSoft() {
                return true;
            }
        }};
    }

    /**
     * get the data source for iBATIS
     *
     * @param psiElement psiElement
     * @return DataSource Object
     */
    @Nullable
    public static DataSource getDataSourceForIbatis(PsiElement psiElement) {
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        return getDataSourceForIbatis(module);
    }

    /**
     * get the data source for iBATIS in module
     *
     * @param module Module object
     * @return DataSource object
     */
    @Nullable public static DataSource getDataSourceForIbatis(Module module) {
        IbatisFacet ibatisFacet = IbatisFacet.getInstance(module);
        if (ibatisFacet == null) return null;
        String selectedDataSourceName = ibatisFacet.getConfiguration().dataSourceName;
        if(StringUtil.isEmpty(selectedDataSourceName))  return null;
        DataSourceManager dataSourceManager = DataSourceManager.getInstance(module.getProject());
        return dataSourceManager.getDataSourceByName(selectedDataSourceName);
    }

}
