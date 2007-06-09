/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package org.intellij.ibatis.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

/**
 * @author Dmitry Avdeev
 */
public class IbatisFacetConfiguration implements FacetConfiguration, ModificationTracker {
    private long myModificationCount;
    public String dataSourceName;
    public String sqlMapSuffix;
    public String tableNamePattern;
    public String domainNamePattern;
    public String abatorOverWrite;

    public FacetEditorTab[] createEditorTabs(final FacetEditorContext editorContext, final FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{
                new IbatisConfigurationTab(editorContext, this)
        };
    }

    public void readExternal(Element element) throws InvalidDataException {
        dataSourceName = JDOMExternalizer.readString(element, "datasourceName");
        sqlMapSuffix = JDOMExternalizer.readString(element, "sqlMapSuffix");
        tableNamePattern = JDOMExternalizer.readString(element, "tableNamePattern");
        domainNamePattern = JDOMExternalizer.readString(element, "domainNamePattern");
        abatorOverWrite = JDOMExternalizer.readString(element, "abatorOverWrite");
    }

    public void writeExternal(Element element) throws WriteExternalException {
        JDOMExternalizer.write(element, "datasourceName", dataSourceName);
        JDOMExternalizer.write(element, "sqlMapSuffix", sqlMapSuffix);
        JDOMExternalizer.write(element, "tableNamePattern", tableNamePattern);
        JDOMExternalizer.write(element, "domainNamePattern", domainNamePattern);
        JDOMExternalizer.write(element, "abatorOverWrite", abatorOverWrite);

    }

    public long getModificationCount() {
        return myModificationCount;
    }

    public void setModified() {
        myModificationCount++;
    }
}