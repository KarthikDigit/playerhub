package com.playerhub.utils;

import android.databinding.InverseMethod;
import android.text.TextUtils;

public class StringUtils {


    public static String isNotEmptyAndNull(String s) {

        return s != null && !TextUtils.isEmpty(s) ? s : "";
    }
}
