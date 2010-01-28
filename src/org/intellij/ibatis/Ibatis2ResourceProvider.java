package org.intellij.ibatis;

import com.intellij.javaee.ResourceRegistrar;
import com.intellij.javaee.StandardResourceProvider;
import org.intellij.ibatis.util.IbatisConstants;
import org.jetbrains.annotations.NonNls;

/**
 * iBATIS resource provider
 *
 * @author linux_china@hotmail.com
 */
public class Ibatis2ResourceProvider implements StandardResourceProvider {
    /**
     * dtd path
     */
    private static String DTD_PATH = "/org/intellij/ibatis/dtds";

    public void registerResources(ResourceRegistrar resourceRegistrar) {
        registerDTDs(IbatisConstants.CONFIGURATION_DTDS, resourceRegistrar);
        registerDTDs(IbatisConstants.SQLMAP_DTDS, resourceRegistrar);
        registerDTDs(IbatisConstants.ABATOR_DTDS, resourceRegistrar);
    }

    /**
     * register  dtd list
     *
     * @param dtdUrls           dtd url list
     * @param resourceRegistrar resource registar
     */
    public void registerDTDs(String[] dtdUrls, ResourceRegistrar resourceRegistrar) {
        for (String dtdUrl : dtdUrls) {
            addDTDResource(dtdUrl, resourceRegistrar);
        }
    }

    /**
     * Adds a DTD resource from local DTD resource path.
     *
     * @param uri       Resource URI.
     * @param registrar Resource registrar.
     */
    private static void addDTDResource(@NonNls final String uri, final ResourceRegistrar registrar) {
        if (uri.startsWith("http://")) {
            int pos = uri.lastIndexOf('/');
            String file = DTD_PATH + uri.substring(pos);
            registrar.addStdResource(uri, file, IbatisApplicationComponent.class);
        }
    }
}
