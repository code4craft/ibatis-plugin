package org.intellij.ibatis.actions;

import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.sqlMap.Result;
import org.intellij.ibatis.dom.sqlMap.ResultMap;
import org.intellij.ibatis.provider.TableColumnReferenceProvider;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

/**
 * generate select sentence SQL for resultMap
 *
 * @author jacky
 */
public class GenerateSQLForResultMapAction extends AnAction {
    /**
     * execute sql generation and copy task
     *
     * @param e action event
     */
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
        Editor editor = e.getData(DataKeys.EDITOR);
        if ((psiFile != null && psiFile instanceof XmlFile) && editor != null) {
            PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
            if (psiElement != null) {
                XmlTag xmlTag = getResultMapTag(psiElement);
                if (xmlTag != null) {
                    DomElement domElement = DomManager.getDomManager(psiFile.getProject()).getDomElement(xmlTag);
                    if (domElement != null && domElement instanceof ResultMap) {
                        List<String> columns = new ArrayList<String>();
                        ResultMap resultMap = (ResultMap) domElement;
                        List<Result> allResults = resultMap.getAllResults();
                        for (Result result : allResults) {
                            String columnName = result.getColumn().getValue();
                            if (columnName != null && columnName.length() > 0 && !columns.contains(columnName)) {
                                columns.add(columnName);
                            }
                        }
                        PsiClass psiClass = resultMap.getClazz().getValue();
                        if (psiClass != null) {
                            String tableName = "table_name_here";
                            String select = "*";
                            DatabaseTableData tableData = TableColumnReferenceProvider.getDatabaseTableData(psiClass);
                            if (tableData != null) {
                                tableName = tableData.getName();
                            }
                            if (columns.size() > 0) {
                                select = StringUtil.join(columns, ", ");
                            }
                            String sentenceSQL = "select " + select + " from " + tableName;
                            CopyPasteManager copyPasteManager = CopyPasteManager.getInstance();
                            copyPasteManager.setContents(new StringSelection(sentenceSQL));
                        }
                    }
                }
            }
        }
    }

    /**
     * validate operation available
     *
     * @param e action event
     */
    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
        Editor editor = e.getData(DataKeys.EDITOR);
        if ((psiFile != null && psiFile instanceof XmlFile) && editor != null) {
            PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
            if (psiElement != null) {
                XmlTag tag = getResultMapTag(psiElement);
                if (tag != null) {
                    e.getPresentation().setEnabled(true);
                    e.getPresentation().setVisible(true);
                    return;
                }
            }
        }
        e.getPresentation().setEnabled(false);
        e.getPresentation().setVisible(false);
    }

    /**
     * get resultMap tag
     *
     * @param psiElement xml tag
     * @return resultMap tag
     */
    private XmlTag getResultMapTag(PsiElement psiElement) {
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
        if (xmlTag != null) {
            if ("resultMap".equals(xmlTag.getName())) {
                return xmlTag;
            } else {
                return getResultMapTag(xmlTag);
            }
        }
        return null;
    }
}