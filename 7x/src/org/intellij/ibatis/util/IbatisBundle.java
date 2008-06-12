package org.intellij.ibatis.util;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * resource bundle in the project
 *
 * @author Jacky
 */
public class IbatisBundle {
    private static Reference ourBundle;
    protected static final String PATH_TO_BUNDLE = "resource.IbatisBundle";

    private IbatisBundle() {
    }

    /**
     * get resource bundle value from   resource.IbatisBundle
     *
     * @param key    key name
     * @param params parameters
     * @return value
     */
    public static String message(@PropertyKey(resourceBundle = PATH_TO_BUNDLE)String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    /**
     * get the bundle for resource.IbatisBundle
     * @return ResourceBundle object
     */
    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if (ourBundle != null)
            bundle = (ResourceBundle) ourBundle.get();
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(PATH_TO_BUNDLE);
            ourBundle = new SoftReference(bundle);
        }
        return bundle;
    }

}
