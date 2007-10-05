package org.intellij.ibatis.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DataSourceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

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
	private FacetEditorContext editorContext;
    private IbatisFacetConfiguration configuration;

    public IbatisConfigurationTab(final FacetEditorContext editorContext, final IbatisFacetConfiguration configuration) {
        this.editorContext = editorContext;
        this.configuration = configuration;
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
				DataSourceManager sourceManager = DataSourceManager.getInstance(editorContext.getProject());
				sourceManager.manageDatasources();
				List<DataSource> dataSourceList = sourceManager.getDataSources();
				for (DataSource dataSource : dataSourceList) {
					dataSourceComboBox.addItem(dataSource.getName());
				}				
			}
		});
	}

    public void fillData() {
        DataSourceManager sourceManager = DataSourceManager.getInstance(editorContext.getProject());
        List<DataSource> dataSourceList = sourceManager.getDataSources();
        for (DataSource dataSource : dataSourceList) {
            dataSourceComboBox.addItem(dataSource.getName());
        }
        if (configuration.dataSourceName != null)
            dataSourceComboBox.setSelectedItem(configuration.dataSourceName);

		sqlmapSuffixTextField.setText(configuration.sqlMapSuffix);

		sqlMapPackageName.setText(configuration.sqlMapPackage);
		sqlMapPackageName.setEnabled(false);

		beanPackageTextField.setText(configuration.beanPackage);
		beanPackageTextField.setEnabled(false);
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
