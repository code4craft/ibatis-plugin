package org.intellij.ibatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.intellij.javaee.dataSource.DatabaseTableData;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.dom.sqlMap.Select;
import org.intellij.ibatis.provider.IbatisClassShortcutsReferenceProvider;
import org.intellij.ibatis.provider.TableColumnReferenceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * generate sql for select with result map
 */
public class GenerateSQLForSelectAction extends PsiIntentionBase {
    private static final String NAME = "GenerateSQLForSelectWithResultMap";
    private static final String TEXT = "Generate SQL for select with resultMap.";

    protected void invoke(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
        if (file instanceof XmlFile && element instanceof XmlTag) {
            XmlTag xmlTag = (XmlTag) element;
            if (xmlTag.getName().equals("select") && xmlTag.getValue().getText().trim().length() == 0) {   // empty select
                if (xmlTag.getAttributeValue("resultMap") != null) {      //resultMap included
                    DomElement domElement = DomManager.getDomManager(project).getDomElement(xmlTag);   //Sql Map file
                    if (domElement != null && domElement instanceof Select) {
                        PsiReference psiReference = xmlTag.getAttribute("resultMap").getValueElement().getReference();
                        if (psiReference != null) {
                            PsiElement psiElement = psiReference.resolve();
                            if (psiElement != null && psiElement instanceof XmlAttribute) {
                                XmlTag resultMapTag = ((XmlAttribute) psiElement).getParent();
                                DomElement resultMapTemp = DomManager.getDomManager(project).getDomElement(resultMapTag);
                                if (resultMapTemp != null && resultMapTemp instanceof ResultMap) {
                                    ResultMap resultMap = (ResultMap) resultMapTemp;
                                    String className = resultMap.getClazz().getValue();
                                    if (StringUtil.isNotEmpty(className)) {
                                        PsiClass psiClass = IbatisClassShortcutsReferenceProvider.getPsiClass(element, resultMap.getClazz().getValue());
                                        List<Result> list = resultMap.getResults();
                                        List<String> columns = new ArrayList<String>();
                                        for (Result result : list) {
                                            String columnName = result.getColumn().getStringValue();
                                            if (StringUtil.isNotEmpty(columnName))
                                                columns.add(columnName);
                                        }
                                        String selectList = StringUtil.join(columns, ", ");
                                        if (psiClass != null) {  //get table name
                                            DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(psiClass);
                                            if (tableData != null)   //表名不为空
                                            {
                                                xmlTag.getValue().setText("select " + selectList + " from " + tableData.getName());
                                            }
                                        } else {
                                            xmlTag.getValue().setText("select " + selectList + " from");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean isAvailable(Project project, Editor editor, PsiFile file, @NotNull PsiElement element) {
        if (file instanceof XmlFile && element instanceof XmlTag) {
            XmlTag xmlTag = (XmlTag) element;
            if (xmlTag.getName().equals("select") && xmlTag.getValue().getText().trim().length() == 0) {   // empty select
                if (xmlTag.getAttributeValue("resultMap") != null) {      //resultMap included
                    DomElement domElement = DomManager.getDomManager(project).getDomElement(xmlTag);   //Sql Map file
                    if (domElement != null && domElement instanceof Select) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @NotNull public String getText() {
        return TEXT;
    }

    @NotNull public String getFamilyName() {
        return NAME;
    }
}
