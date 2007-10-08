package org.intellij.ibatis.impl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import org.intellij.ibatis.IbatisConfigurationModel;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.IbatisProjectComponent;
import org.intellij.ibatis.IbatisSqlMapModel;
import org.intellij.ibatis.dom.sqlMap.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"ConstantConditions"})
public class IbatisManagerImpl extends IbatisManager {
    private Map<String, IbatisConfigurationModel> configurationModelMap = new HashMap<String, IbatisConfigurationModel>();

    public IbatisManagerImpl() {
    }

    @Nullable public IbatisConfigurationModel getConfigurationModel(@NotNull Module module) {
        IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(module.getProject());
        List<IbatisConfigurationModel> models = projectComponent.getConfigurationModelFactory().getAllModels(module);
        if (models.size() > 0) return models.get(0);
        else return null;
    }

    private String getUniqueName(DomFileElement fileElement, String id) {
        IbatisConfigurationModel configurationModel = getConfigurationModel(ModuleUtil.findModuleForPsiElement(fileElement.getRootTag()));
        if (configurationModel.isUseStatementNamespaces()) {
          String namespace = fileElement.getRootTag().getAttributeValue("namespace");
          if (namespace != null && namespace.length() > 0) {
            return namespace + "." + id;
          } else {
            return id;
          }
        } else {
            return id;
        }
    }


    @Nullable public IbatisSqlMapModel getSqlMapModel(@Nullable PsiElement psiElement) {
        if (psiElement == null) return null;
        IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(psiElement.getProject());
        return projectComponent.getSqlMapModelFactory().getModel(psiElement);
    }

    public Map<String, PsiClass> getAllTypeAlias(PsiElement psiElement) {
        Map<String, PsiClass> allAliasMap = new HashMap<String, PsiClass>();
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(module.getProject());
        List<IbatisConfigurationModel> configurationModels = projectComponent.getConfigurationModelFactory().getAllModels(module);
        for (IbatisConfigurationModel configurationModel : configurationModels) {
            allAliasMap.putAll(configurationModel.getTypeAlias());
        }
        List<IbatisSqlMapModel> sqlMapModels = projectComponent.getSqlMapModelFactory().getAllModels(module);
        for (IbatisSqlMapModel sqlMapModel : sqlMapModels) {
            allAliasMap.putAll(sqlMapModel.getTypeAlias());
        }
        return allAliasMap;
    }

  public  Map<String, XmlTag> getAllTypeAlias2(PsiElement psiElement) {
     Map<String, XmlTag> typeAlias = new HashMap<String, XmlTag>();
       Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(module.getProject());
        List<IbatisConfigurationModel> configurationModels = projectComponent.getConfigurationModelFactory().getAllModels(module);
        for (IbatisConfigurationModel configurationModel : configurationModels) {
            typeAlias.putAll(configurationModel.getTypeAlias2());
        }

      List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
      for (IbatisSqlMapModel model : models) {
          List<TypeAlias> typeAliases = model.getMergedModel().getTypeAlias();
          for (TypeAlias alias : typeAliases) {
              XmlTag xmlTag = alias.getXmlTag();
              if (xmlTag != null) {
                  typeAlias.put(alias.getAlias().getValue(), xmlTag);
              }
          }
      }
      return typeAlias;
  }

  public Map<String, PsiClass> getAllResultMap(PsiElement psiElement) {
        Map<String, PsiClass> resultMap = new HashMap<String, PsiClass>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<ResultMap> resultMapList = model.getMergedModel().getResultMaps();
            for (ResultMap map : resultMapList) {
                PsiClass psiClass = map.getClazz().getValue();
                if (psiClass != null) {
                    resultMap.put(getUniqueName(map.getRoot(), map.getId().getValue()), psiClass);
                }
            }
        }
        return resultMap;
    }

    public Map<String, XmlTag> getAllResultMap2(PsiElement psiElement) {
        Map<String, XmlTag> resultMap = new HashMap<String, XmlTag>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<ResultMap> resultMapList = model.getMergedModel().getResultMaps();
            for (ResultMap map : resultMapList) {
                XmlTag xmlTag = map.getClazz().getXmlTag();
                if (xmlTag != null) {
                    resultMap.put(getUniqueName(map.getRoot(), map.getId().getValue()), xmlTag);
                }
            }
        }
        return resultMap;
    }

    public Map<String, PsiClass> getAllParameterMap(PsiElement psiElement) {
        Map<String, PsiClass> parameterMap = new HashMap<String, PsiClass>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<ParameterMap> parameterMapList = model.getMergedModel().getParameterMap();
            for (ParameterMap map : parameterMapList) {
                PsiClass psiClass = map.getClazz().getValue();
                if (psiClass != null) {
                    parameterMap.put(getUniqueName(map.getRoot(), map.getId().getValue()), psiClass);
                }
            }
        }
        return parameterMap;
    }

    public Map<String, XmlTag> getAllParameterMap2(PsiElement psiElement) {
        Map<String, XmlTag> parameterMap = new HashMap<String, XmlTag>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<ParameterMap> parameterMapList = model.getMergedModel().getParameterMap();
            for (ParameterMap map : parameterMapList) {
                XmlTag xmlTag = map.getClazz().getXmlTag();
                if (xmlTag != null) {
                    parameterMap.put(getUniqueName(map.getRoot(), map.getId().getValue()), xmlTag);
                }
            }
        }
        return parameterMap;
    }

    public Map<String, Select> getAllSelect(PsiElement psiElement) {
        Map<String, Select> selectList = new HashMap<String, Select>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<Select> selects = model.getMergedModel().getSelects();
            for (Select select : selects) {
                selectList.put(getUniqueName(select.getRoot(), select.getId().getStringValue()), select);
            }
        }
        return selectList;
    }

    public Map<String, Sql> getAllSql(PsiElement psiElement) {
        Map<String, Sql> allSql = new HashMap<String, Sql>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<Sql> sqls = model.getMergedModel().getSqls();
            for (Sql sql : sqls) {
                allSql.put(getUniqueName(sql.getRoot(), sql.getId().getValue()), sql);
            }
        }
        return allSql;
    }

    private List<IbatisSqlMapModel> getAllSqlMapModel(PsiElement psiElement) {
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(module.getProject());
        return projectComponent.getSqlMapModelFactory().getAllModels(module);
    }

    private List<IbatisSqlMapModel> getAllSqlMapModel(Module module) {
        IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(module.getProject());
        return projectComponent.getSqlMapModelFactory().getAllModels(module);
    }

    public Map<String, Insert> getAllInsert(PsiElement psiElement) {
        Map<String, Insert> allInsert = new HashMap<String, Insert>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<Insert> inserts = model.getMergedModel().getInserts();
            for (Insert insert : inserts) {
                allInsert.put(getUniqueName(insert.getRoot(), insert.getId().getValue()), insert);
            }
        }
        return allInsert;
    }

    public Map<String, Update> getAllUpdate(PsiElement psiElement) {
        Map<String, Update> allUpdate = new HashMap<String, Update>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<Update> updates = model.getMergedModel().getUpdates();
            for (Update update : updates) {
                allUpdate.put(getUniqueName(update.getRoot(), update.getId().getValue()), update);
            }
        }
        return allUpdate;
    }

    public Map<String, Delete> getAllDelete(PsiElement psiElement) {
        Map<String, Delete> allDelete = new HashMap<String, Delete>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<Delete> deletes = model.getMergedModel().getDeletes();
            for (Delete delete : deletes) {
                allDelete.put(getUniqueName(delete.getRoot(), delete.getId().getValue()), delete);
            }
        }
        return allDelete;
    }

    public Map<String, Statement> getAllStatement(PsiElement psiElement) {
        Map<String, Statement> allStatement = new HashMap<String, Statement>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<Statement> statements = model.getMergedModel().getStatements();
            for (Statement statement : statements) {
                allStatement.put(getUniqueName(statement.getRoot(), statement.getId().getValue()), statement);
            }
        }
        return allStatement;
    }

    public Map<String, Procedure> getAllProcedure(PsiElement psiElement) {
        Map<String, Procedure> allStatement = new HashMap<String, Procedure>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<Procedure> procedures = model.getMergedModel().getProcedures();
            for (Procedure procedure : procedures) {
                allStatement.put(getUniqueName(procedure.getRoot(), procedure.getId().getValue()), procedure);
            }
        }
        return allStatement;
    }

    public Map<String, DomElement> getAllSqlMapReference(Module module) {
        Map<String, DomElement> allReference = new HashMap<String, DomElement>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(module);
        for (IbatisSqlMapModel model : models) {
            List<DomElement> references = model.getMergedModel().getAllReference();
            for (DomElement reference : references) {
                allReference.put(getUniqueName(reference.getRoot(), reference.getXmlTag().getAttributeValue("id")), reference);
            }
        }
        return allReference;
    }

    public Map<String, CacheModel> getAllCacheModel(PsiElement psiElement) {
        Map<String, CacheModel> allCacheModel = new HashMap<String, CacheModel>();
        List<IbatisSqlMapModel> models = getAllSqlMapModel(psiElement);
        for (IbatisSqlMapModel model : models) {
            List<CacheModel> cacheModels = model.getMergedModel().getCacheModels();
            for (CacheModel cacheModel : cacheModels) {
                allCacheModel.put(getUniqueName(cacheModel.getRoot(), cacheModel.getId().getValue()), cacheModel);
            }
        }
        return allCacheModel;
    }

}
