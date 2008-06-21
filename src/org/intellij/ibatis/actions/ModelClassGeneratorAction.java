package org.intellij.ibatis.actions;

import com.intellij.ide.util.PackageUtil;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DatabaseTableData;
import com.intellij.javaee.dataSource.DatabaseTableFieldData;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.FileContentUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.intellij.ibatis.IbatisConfigurationModel;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.IbatisProjectComponent;
import org.intellij.ibatis.dom.configuration.SqlMapConfig;
import org.intellij.ibatis.facet.IbatisFacet;
import org.intellij.ibatis.facet.IbatisFacetConfiguration;
import org.intellij.ibatis.intention.GenerateSQLForCrudAction;
import org.intellij.ibatis.provider.JavadocTableNameReferenceProvider;
import org.intellij.ibatis.util.IbatisConstants;
import org.intellij.ibatis.util.IbatisUtil;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * model class generate according to table name
 */
public class ModelClassGeneratorAction extends AnAction {
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getData(DataKeys.PROJECT);
		Module module = e.getData(DataKeys.MODULE);
		IbatisFacet facet = IbatisFacet.getInstance(module);
		IbatisFacetConfiguration config;
		if (facet != null) {
			config = facet.getConfiguration();
		} else {
			return;
		}

		PsiElement psiElement = e.getData(DataKeys.PSI_ELEMENT);
		PsiDirectory psiDirectoryBean;

		if(config.beanPackage.trim().length() == 0){
			// 0 = yes, 1 = no
			int response = Messages.showYesNoDialog("Your default bean creation package is empty.\n"
				+ "This means your bean class will be created in the default package.\n"
				+ "Are you sure this is what you want?",
				"Are you sure?",
				Messages.getQuestionIcon());
			if (response == 1) return;
		}

		if(config.sqlMapPackage.trim().length() == 0){
			// 0 = yes, 1 = no
			int response = Messages.showYesNoDialog("Your default SQL map creation package is empty.\n"
				+ "This means your SQL map file will be created in the default package.\n"
				+ "Are you sure this is what you want?",
				"Are you sure?",
				Messages.getQuestionIcon());
			if (response == 1) return;
		}
		
		try {
			psiDirectoryBean = PackageUtil.findOrCreateDirectoryForPackage(module, config.beanPackage, (PsiDirectory) psiElement, true);
		} catch (IncorrectOperationException e1) {
			Messages.showDialog(
				project,
				"There was an error getting the package for bean creation, so I'm giving up.",
				"Uh-oh.",
				new String[]{"Darn it."},
				0,
				Messages.getErrorIcon());
			return;
		}

		if (psiDirectoryBean != null && JavaDirectoryService.getInstance().getPackage(psiDirectoryBean) != null) {
			PsiDirectory psiDirectorySqlMap;
			try {
				psiDirectorySqlMap = PackageUtil.findOrCreateDirectoryForPackage(module, config.sqlMapPackage, psiDirectoryBean, true);
			} catch (IncorrectOperationException e1) {
				// give up
				Messages.showDialog(
					project,
					"There was an error getting the package for SQL map creation, so I'm giving up.",
					"Uh-oh.",
					new String[]{"Darn it."},
					0,
					Messages.getErrorIcon());
				return;
			}
			DataSource dataSource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(module);
			if (dataSource != null)  //ibatis enabled and datasource is ready
			{
				List<String> tableNames = new ArrayList<String>();
				List<DatabaseTableData> tableDataList = dataSource.getTables();
				for (DatabaseTableData tableData : tableDataList) {
					tableNames.add(tableData.getName());
				}
				if (tableNames.size() > 0) {
					int result = Messages.showChooseDialog(
						project, null,
						"Choose a database table",
						IbatisConstants.DATABASE,
						tableNames.toArray(new String[]{""}),
						tableNames.get(0));
					if (result > -1) {
						//a table name selected
						String tableName = tableNames.get(result);
						// try to make the name singular
						String beanName = StringUtil.unpluralize(tableName);
						if (null == beanName) beanName = tableName;
						for (DatabaseTableData tableData : tableDataList) {
							if (tableData.getName().equals(tableName)) {
								try {
									ApplicationManager.getApplication()
									.runWriteAction(
										new GenerateModelClassRunner(
												project, psiDirectoryBean, psiDirectorySqlMap,
												tableData, beanName, config
											)
										);
								} catch (Exception e1) {
									// we just notified the user, so we can just bail out.
									return;
								}
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * validate action is available
	 *
	 * @param event action event
	 */
	public void update(AnActionEvent event) {
		Presentation presentation = event.getPresentation();
		Module module = event.getData(DataKeys.MODULE);
		PsiElement psiElement = event.getData(DataKeys.PSI_ELEMENT);
		if (psiElement instanceof PsiDirectory) {
			PsiDirectory psiDirectory = (PsiDirectory) psiElement;
			if (JavaDirectoryService.getInstance().getPackage(psiDirectory) != null) {
				DataSource dataSource = JavadocTableNameReferenceProvider.getDataSourceForIbatis(module);
				if (dataSource != null) {
					presentation.setEnabled(true);
					presentation.setVisible(true);
					return;
				}
			}
		}
		presentation.setEnabled(false);
		presentation.setVisible(false);
	}

	/**
	 * get model class name according to class name
	 *
	 * @param tableName table name
	 * @return model class name
	 */
	private String getModelClassName(String tableName) {
		StringBuilder className = new StringBuilder();
		String[] items = tableName.split("_");
		for (String item : items) {
			if(item.equals(item.toUpperCase())){
				// this part is all uppercase, make it lower
				item = item.toLowerCase();
			}
			// add the init cap letter
			item = StringUtil.capitalize(item);
			if(null != StringUtil.unpluralize(item)){
				// if it can be singular, make it so
				item = StringUtil.unpluralize(item);
			}
			className.append(item);
		}
		return className.toString();
	}

	/**
	 * create ClassField object accroding to   DatabaseTableFieldData
	 *
	 * @param tableFieldData DatabaseTableFieldData object
	 * @return ClassField object
	 */
	private ClassField getClassField(DatabaseTableFieldData tableFieldData) {
		ClassField classField = new ClassField();
		classField.setColumnName(tableFieldData.getName());
		classField.setName(StringUtil.decapitalize(getModelClassName(classField.getColumnName())));
		classField.setType(getJavaType(tableFieldData.getType()));
		return classField;
	}

	/**
	 * get java type according to jdbc type
	 *
	 * @param jdbcType jdbc type
	 * @return java type
	 */
	public String getJavaType(String jdbcType) {
		if (jdbcType.contains(".")) {
			return jdbcType.substring(jdbcType.lastIndexOf(".") + 1);
		}
		return jdbcType;
	}

	/**
	 * generate  model class runnable
	 */
	private class GenerateModelClassRunner implements Runnable {
		private Project project;
		private PsiDirectory psiDirectoryBean;
		private PsiDirectory psiDirectorySqlMap;
		private DatabaseTableData tableData;
		private String beanName;
		private IbatisFacetConfiguration config;

		public GenerateModelClassRunner(
			Project project,
			PsiDirectory psiDirectoryBean,
			PsiDirectory psiDirectorySqlMap,
			DatabaseTableData tableData,
			String beanName,
			IbatisFacetConfiguration config
		) throws Exception {
			this.project = project;
			this.psiDirectoryBean = psiDirectoryBean;
			this.psiDirectorySqlMap = psiDirectorySqlMap;
			this.tableData = tableData;
			this.beanName = beanName;
			this.config = config;
		}

		@SuppressWarnings({"ConstantConditions"})
		public void run() {
			try {
				String className = getModelClassName(beanName);
				PsiFile beanFile = psiDirectoryBean.findFile(className + ".java");
				if (beanFile == null) {  //文件不存在
					VelocityContext context = new VelocityContext();
					context.put("package", JavaDirectoryService.getInstance().getPackage(psiDirectoryBean).getQualifiedName());
					context.put("name", className);
					context.put("tableName", tableData.getName());
					List<ClassField> classFields = new ArrayList<ClassField>();
					for (DatabaseTableFieldData tableFieldData : tableData.getFields()) {
						classFields.add(getClassField(tableFieldData));
					}
					context.put("fieldList", classFields);
					StringWriter writer = new StringWriter();
					VelocityEngine engine = new VelocityEngine();
					engine.init();
					engine.evaluate(context, writer, "iBATIS", new StringReader(config.beanTemplate));
					beanFile = psiDirectoryBean.createFile(className + ".java");
					FileContentUtil.setFileText(project, beanFile.getVirtualFile(), writer.toString());

					String sqlMapFileName = className + config.sqlMapSuffix;
					PsiFile sqlMapFile = psiDirectorySqlMap.findFile(sqlMapFileName);
					if (sqlMapFile == null) {
						StringWriter sw = new StringWriter();
						context = new VelocityContext();
						context.put("className", className);
						context.put("FQCN", JavaDirectoryService.getInstance().getPackage(psiDirectoryBean).getQualifiedName() + "." + className);
						sqlMapFile = psiDirectorySqlMap.createFile(sqlMapFileName);
						engine.evaluate(context, sw, "iBATIS", new StringReader(config.sqlMapTemplate));
						FileContentUtil.setFileText(
							project,
							sqlMapFile.getVirtualFile(),
							sw.toString()
						);

					}

					if (config.injectCreatedSqlMap) {
						final PsiFile sqlMapFile1 = sqlMapFile;
						CommandProcessor.getInstance().executeCommand(project, new Runnable() {
							public void run() {
								Module module = ModuleUtil.findModuleForPsiElement(psiDirectoryBean);
								IbatisConfigurationModel configurationModel = null;
								if (module != null) {
									configurationModel = IbatisManager.getInstance().getConfigurationModel(module);
								}
								if (configurationModel != null) {
									SqlMapConfig sqlMapConfig = configurationModel.getMergedModel();
									sqlMapConfig.addSqlMap().getResource().setValue(sqlMapFile1);
								}
							}
						}, "", null, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
					}

					if(config.generateCrudOperations){
						for(PsiElement e : sqlMapFile.getChildren()){
							if(e instanceof XmlDocument){
								final XmlDocument xd = (XmlDocument) e;
								// what is the model class here?
								final PsiClass psiClass = ClassUtil.findPsiClass(PsiManager.getInstance(project), JavaDirectoryService.getInstance().getPackage(psiDirectoryBean).getQualifiedName() + "." + className);
								CommandProcessor.getInstance().executeCommand(project, new Runnable() {
									public void run() {
										GenerateSQLForCrudAction.generateCrudOperations(xd.getRootTag(), IbatisUtil.getConfig(psiDirectorySqlMap), psiClass);
									}
								}, "", null, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
							}
						}
					}
					final PsiFile beanFile1 = beanFile;
					final PsiFile sqlMapFile2 = psiDirectorySqlMap.findFile(sqlMapFileName);
					CommandProcessor.getInstance().executeCommand(project, new Runnable() {
						public void run() {
							try {
								CodeStyleManager csm = CodeStyleManager.getInstance(project);
								csm.reformat(beanFile1,true);
								csm.reformat(sqlMapFile2);
								if(config.injectCreatedSqlMap){
									// reformat the config file, too
									IbatisProjectComponent projectComponent = IbatisProjectComponent.getInstance(project);
									Module module = ModuleUtil.findModuleForPsiElement(psiDirectorySqlMap);

									Set<XmlFile> files = projectComponent.getConfigurationModelFactory().getAllSqlMapConfigurationFile(module);
									for(XmlFile configFile : files){
										csm.reformat(configFile);
									}
								}
							} catch (IncorrectOperationException e) {
								e.printStackTrace();
							}
						}
					}, "", null, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
				}
//				FileEditorManager.getInstance(project).openFile(beanFile.getVirtualFile(), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
