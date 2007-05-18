package org.intellij.ibatis.insepections;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.intellij.ibatis.IbatisConfigurationModel;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.intellij.ibatis.dom.sqlMap.SqlMap;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * inspection the sql map file in configuration, if not in configuration file, use quick fix
 * @author Jacky
 */
public class SqlMapFileInConfigurationInspection extends SqlMapInspection {
    @Nls @NotNull public String getDisplayName() {
        return "sql map file not in configuration inspection";
    }

    @NonNls @NotNull public String getShortName() {
        return "sqlmap_not_in_configuration_inspection";
    }

    @SuppressWarnings({"ConstantConditions", "SuspiciousMethodCalls"})
    public void checkFileElement(DomFileElement<SqlMap> fileElement, DomElementAnnotationHolder holder) {
        SqlMap sqlMap = fileElement.getRootElement();
        Module module = ModuleUtil.findModuleForPsiElement(fileElement.getXmlElement());
        IbatisConfigurationModel configurationModel = IbatisManager.getInstance().getConfigurationModel(module);
        Set<XmlFile> sqlMapFiles = configurationModel.getSqlMapFiles();
        if (!sqlMapFiles.contains(sqlMap.getContainingFile())) {
            SqlMapConfig sqlMapConfig = configurationModel.getMergedModel();
            holder.createProblem(sqlMap, HighlightSeverity.WARNING, "Current sql map file not in iBATIS configuration", new InsertSqlMapIntoConfigurationQuickFix(sqlMapConfig, sqlMap));
        }
    }

    public class InsertSqlMapIntoConfigurationQuickFix implements LocalQuickFix {
        private SqlMapConfig config;
        private SqlMap sqlMap;

        public InsertSqlMapIntoConfigurationQuickFix(SqlMapConfig config, SqlMap sqlMap) {
            this.config = config;
            this.sqlMap = sqlMap;
        }

        @NotNull public String getName() {
            return "insert into configuration";
        }

        @NotNull public String getFamilyName() {
            return "sql map model quick fixes";
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
            new WriteCommandAction(project) {
                protected void run(final Result result) throws Throwable {
                    config.addSqlMap().getResource().setValue(sqlMap.getContainingFile());
                }
            }.execute();
        }
    }
}

