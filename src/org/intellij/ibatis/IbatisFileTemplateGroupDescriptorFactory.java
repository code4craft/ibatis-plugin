package org.intellij.ibatis;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;

/**
 * iBATIS file template group
 *
 * @author linux_china@hotmail.com
 */
public class IbatisFileTemplateGroupDescriptorFactory implements FileTemplateGroupDescriptorFactory {
    /**
     * Template for sqlmap config
     */
    @NonNls
    public static final String SQLMAP_CONFIG = "sqlmap-config.xml";

    /**
     * Template for sqlmap
     */
    @NonNls
    public static final String SQLMAP = "sqlmap.xml";

    /**
     * get file template group descriptor
     *
     * @return group descriptor
     */
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("iBATIS", IbatisConstants.IBATIS_LOGO);
        group.addTemplate(new FileTemplateDescriptor(SQLMAP_CONFIG, IbatisConstants.IBATIS_LOGO));
        group.addTemplate(new FileTemplateDescriptor(SQLMAP, IbatisConstants.IBATIS_LOGO));
        return group;
    }
}
