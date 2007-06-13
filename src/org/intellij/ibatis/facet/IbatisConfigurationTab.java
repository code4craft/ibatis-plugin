package org.intellij.ibatis.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DataSourceManager;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.util.List;

/**
 * ibatis configuration facet tab
 */
public class IbatisConfigurationTab extends FacetEditorTab {
    private JPanel mainPanel;
    private JComboBox dataSourceComboBox;
    private JTextField sqlmapSuffixTextField;
    private FacetEditorContext editorContext;
    private IbatisFacetConfiguration configuration;

    public IbatisConfigurationTab(FacetEditorContext editorContext, IbatisFacetConfiguration configuration) {
        this.editorContext = editorContext;
        this.configuration = configuration;
        fillData();
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
