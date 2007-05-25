package org.intellij.ibatis;

import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.model.DomModel;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * IBATIS configuration model
 */
public interface IbatisConfigurationModel extends DomModel<SqlMapConfig> {

  /**
   * get all  the sql map files
   *
   * @return xml files for sql map
   */
  @NotNull
  public Set<XmlFile> getSqlMapFiles();

  /**
   * validate  useStatementNamespaces
   *
   * @return useStatementNamespaces mark
   */
  public boolean isUseStatementNamespaces();

  @NotNull
  public abstract Map<String, PsiClass> getTypeAlias();

  @NotNull
  public abstract Map<String, XmlTag> getTypeAlias2();
}
