package org.intellij.ibatis.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DataSourceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.EnumComboBoxModel;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * ibatis configuration facet tab
 */
public class IbatisConfigurationTab extends FacetEditorTab {
	private JPanel mainPanel;
    private JComboBox dataSourceComboBox;
    private JTextField sqlmapSuffixTextField;
	private JTextField sqlMapPackageName;
	private JTextField beanPackageTextField;
	private JButton selectSqlMapPackage;
	private JButton button1;
	private JButton configureDatasourcesButton;
	private JTextArea sqlMapTemplate;
	private JTextArea beanTemplate;
	private JTextArea selectKeyTemplate;
	private JComboBox selectKeyType;
	private JButton resetSQLMapTemplateButton;
	private JButton resetBeanTemplateButton;
	private JButton resetSelectKeyTemplateButton;
	private JButton resetAllDefaultValuesButton;
	private FacetEditorContext editorContext;
    private IbatisFacetConfiguration configuration;
	private Project project;

	public IbatisConfigurationTab(final FacetEditorContext editorContext, final IbatisFacetConfiguration configuration) {

		this.editorContext = editorContext;

		this.configuration = configuration;

		project = editorContext.getProject();

		fillData();

		selectSqlMapPackage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PackageChooserDialog pcd = new PackageChooserDialog(
					"Package for SQL Map files", editorContext.getProject());
				pcd.show();
				PsiPackage psiPackage = pcd.getSelectedPackage();
				if(null != psiPackage) sqlMapPackageName.setText(psiPackage.getQualifiedName());
			}
		});

		sqlMapPackageName.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				configuration.sqlMapPackage = sqlMapPackageName.getText();
			}
		});

		beanPackageTextField.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				configuration.beanPackage = beanPackageTextField.getText();
			}
		});

		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PackageChooserDialog pcd = new PackageChooserDialog(
					"Package for generated beans", editorContext.getProject());
				pcd.show();
				PsiPackage psiPackage = pcd.getSelectedPackage();
				if(null != psiPackage) beanPackageTextField.setText(psiPackage.getQualifiedName());
			}
		});

		sqlmapSuffixTextField.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				configuration.sqlMapSuffix = sqlmapSuffixTextField.getText();
			}
		});

		configureDatasourcesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataSourceManager sourceManager = DataSourceManager.getInstance(project);
				sourceManager.manageDatasources();
				fillDatasourceList();
			}
		});

		beanTemplate.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				configuration.beanTemplate = beanTemplate.getText();
			}
		});

		sqlMapTemplate.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				configuration.sqlMapTemplate = sqlMapTemplate.getText();
			}
		});

		selectKeyType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configuration.selectKeyType = (SelectKeyType) selectKeyType.getSelectedItem();
				selectKeyTemplate.setEnabled(configuration.selectKeyType != SelectKeyType.none);
			}
		});

		selectKeyTemplate.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				configuration.selectKeyTemplate = selectKeyTemplate.getText();
			}
		});

		resetBeanTemplateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configuration.resetBeanTemplate();
			}
		});
		resetSQLMapTemplateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configuration.resetSqlMapTemplate();
			}
		});
		resetSelectKeyTemplateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (
					confirm(
						"This will discard your SelectKey template, and replace\n" +
						"it with the default value.\n\nAre you sure you want to do this?",
						"Confirm reset"
					)
				) {
					configuration.resetSelectKeyTemplate();
					selectKeyTemplate.setText(configuration.selectKeyTemplate);
					selectKeyType.setSelectedItem(configuration.selectKeyType);
				}
			}
		});
		resetAllDefaultValuesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configuration.resetToDefaultTemplates();
			}
		});
	}

	private boolean confirm(String message, String title) {
		return Messages.showYesNoDialog(
			message,
			title,
			Messages.getQuestionIcon()
		) == 0;
	}

	public void fillData() {
		fillDatasourceList();

		sqlmapSuffixTextField.setText(configuration.sqlMapSuffix);

		sqlMapPackageName.setText(configuration.sqlMapPackage);
		sqlMapPackageName.setEnabled(false);

		beanPackageTextField.setText(configuration.beanPackage);
		beanPackageTextField.setEnabled(false);

		beanTemplate.setText(configuration.beanTemplate);
		sqlMapTemplate.setText(configuration.sqlMapTemplate);
		selectKeyType.setModel(new EnumComboBoxModel<SelectKeyType>(SelectKeyType.class));
		selectKeyType.setSelectedItem(configuration.selectKeyType);
		selectKeyTemplate.setText(configuration.selectKeyTemplate);
		selectKeyTemplate.setEnabled(configuration.selectKeyType != SelectKeyType.none);
	}

	private void fillDatasourceList() {
		if(null != editorContext.getProject()){
			DataSourceManager sourceManager = DataSourceManager.getInstance(project);
			List<DataSource> dataSourceList = sourceManager.getDataSources();
			dataSourceComboBox.removeAllItems();
			for (DataSource dataSource : dataSourceList) {
				dataSourceComboBox.addItem(dataSource.getName());
			}
			if (configuration.dataSourceName != null)
				dataSourceComboBox.setSelectedItem(configuration.dataSourceName);
		}
	}

	@Nls public String getDisplayName() {
        return "Configuration";
    }

    public JComponent createComponent() {
        return mainPanel;
    }

    public boolean isModified() {
        return true;
    }

    public void apply() throws ConfigurationException {
        Object selectedItem = dataSourceComboBox.getSelectedItem();
        if (selectedItem != null)
            configuration.dataSourceName = selectedItem.toString();
        configuration.sqlMapSuffix = sqlmapSuffixTextField.getText();
    }

    public void reset() {

    }

    public void disposeUIResources() {

    }
}
