// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IbatisProjectComponent.java

package org.intellij.ibatis;

import com.intellij.javaee.dataSource.DataSourceManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;

public class IbatisProjectComponent implements ProjectComponent {
    private Project project;
    private IbatisConfigurationModelFactory configurationModelFactory;
    private IbatisSqlMapModelFactory sqlMapModelFactory;

    public IbatisProjectComponent(Project project, DomManager domManager) {
        this.project = project;
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
    }

    public void projectClosed() {
    }

    public IbatisConfigurationModelFactory getConfigurationModelFactory() {
        return configurationModelFactory;
    }

    public IbatisSqlMapModelFactory getSqlMapModelFactory() {
        return sqlMapModelFactory;
    }
}
