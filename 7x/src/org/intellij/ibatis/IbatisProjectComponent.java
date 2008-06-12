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

  @NotNull
  public String getComponentName() {
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
    IntentionManager manager = IntentionManager.getInstance();

    manager.registerIntentionAndMetaData(new GenerateResultsForResultMapAction(), "iBATIS");
    manager.registerIntentionAndMetaData(new GenerateParametersForParameterMapAction(), "iBATIS");
    manager.registerIntentionAndMetaData(new GenerateSQLForSelectAction(), "iBATIS");
    manager.registerIntentionAndMetaData(new GenerateSQLForInsertAction(), "iBATIS");
    manager.registerIntentionAndMetaData(new GenerateSQLForUpdateAction(), "iBATIS");
    manager.registerIntentionAndMetaData(new GenerateSQLForDeleteAction(), "iBATIS");
    manager.registerIntentionAndMetaData(new GenerateSQLForCrudAction(), "iBATIS");
    manager.registerIntentionAndMetaData(new GenerateStatementXmlCodeAction(), "iBATIS");
  }
}
