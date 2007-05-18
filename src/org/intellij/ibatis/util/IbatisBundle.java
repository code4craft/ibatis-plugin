// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IbatisBundle.java

package org.intellij.ibatis.util;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public class IbatisBundle {
    private static Reference ourBundle;
    protected static final String PATH_TO_BUNDLE = "resource.IbatisBundle";

    private IbatisBundle() {
    }

    public static String message(@PropertyKey(resourceBundle = PATH_TO_BUNDLE)String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

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
