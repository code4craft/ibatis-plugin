// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IbatisProjectComponent.java

package org.intellij.ibatis;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.intention.*;
import org.jetbrains.annotations.NotNull;

/**
 * iBATIS Plugin project component
 *
 * @author Jacky
 */
public class IbatisProjectComponent implements ProjectComponent {
    private IbatisConfigurationModelFactory configurationModelFactory;
    private IbatisSqlMapModelFactory sqlMapModelFactory;

    public IbatisProjectComponent(DomManager domManager) {
        configurationModelFactory = new IbatisConfigurationModelFactory(domManager);
        sqlMapModelFactory = new IbatisSqlMapModelFactory(domManager);
    }

    public static IbatisProjectComponent getInstance(Project project) {
        return project.getComponent(IbatisProjectComponent.class);
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull public String getComponentName() {
        return "iBATIS Project Component";
    }

    public void projectOpened() {
        registerIntentionActions();
    }

    public void projectClosed() {
    }

    public IbatisConfigurationModelFactory getConfigurationModelFactory() {
        return configurationModelFactory;
    }

    public IbatisSqlMapModelFactory getSqlMapModelFactory() {
        return sqlMapModelFactory;
    }

    /**
     * register intention actions for project
     */
    private void registerIntentionActions() {
        IntentionManager.getInstance().registerIntentionAndMetaData(new GenerateResultsForResultMapAction(), "iBATIS");
        IntentionManager.getInstance().registerIntentionAndMetaData(new GenerateSQLForSelectAction(), "iBATIS");
        IntentionManager.getInstance().registerIntentionAndMetaData(new GenerateSQLForInsertAction(), "iBATIS");
        IntentionManager.getInstance().registerIntentionAndMetaData(new GenerateSQLForUpdateAction(), "iBATIS");
        IntentionManager.getInstance().registerIntentionAndMetaData(new GenerateSQLForDeleteAction(), "iBATIS");
        IntentionManager.getInstance().registerIntentionAndMetaData(new GenerateSQLForCrudAction(), "iBATIS");
        IntentionManager.getInstance().registerIntentionAndMetaData(new GenerateStatementXmlCodeAction(), "iBATIS");
    }
}
