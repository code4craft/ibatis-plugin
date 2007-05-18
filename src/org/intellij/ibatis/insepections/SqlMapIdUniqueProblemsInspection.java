package org.intellij.ibatis.insepections;

import com.intellij.codeInspection.*;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * sql map id unique inspection
 */
public class SqlMapIdUniqueProblemsInspection extends LocalInspectionTool {
    @Nls @NotNull public String getGroupDisplayName() {
        return "iBATIS Plugin";
    }

    @Nls @NotNull public String getDisplayName() {
        return "sql map id unique inspection";
    }

    @NonNls @NotNull public String getShortName() {
        return "sqlmap_id_unique_problems";
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile psiFile, @NotNull InspectionManager inspectionManager, boolean b) {
        if (psiFile instanceof XmlFile) {
            final DomFileElement fileElement = DomManager.getDomManager(psiFile.getProject()).getFileElement((XmlFile) psiFile, DomElement.class);
            if (fileElement != null && fileElement.getRootElement() instanceof SqlMap) {
                SqlMap sqlMap = (SqlMap) fileElement.getRootElement();
                IbatisManager manager = IbatisManager.getInstance();
                for (DomElement domElement : sqlMap.getAllReference()) {
                    String referenceId = domElement.getXmlTag().getAttributeValue("id");
                    if (StringUtil.isNotEmpty(referenceId) && referenceId.equals("select1")) {
                        return new ProblemDescriptor[]{inspectionManager.createProblemDescriptor(domElement.getXmlTag(), "error", new LocalQuickFix[]{}, ProblemHighlightType.valueOf("error"))};
                    }
                }
            }
        }
        return new ProblemDescriptor[]{};
    }
}
