package com.hua.watchappname.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZHONG WEI  HUA on 2016/5/9.
 */
public class KeywordUtils {

    /**
     * 关键字高亮
     * @param color 高亮颜色
     * @param text 文字
     * @param keyword 关键字
     * @return
     */
    public static SpannableString matcherSearchText(int color, String text, String keyword){

        SpannableString ss = new SpannableString(text);
        Pattern pattern = Pattern.compile(keyword);
        Matcher matcher = pattern.matcher(ss);
        while (matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            ss.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    /**
     * 多个关键字高亮
     * @param color
     * @param text
     * @param keyword
     * @return
     */
    public static SpannableString matcherSearchText(int color, String text, String[] keyword){

        SpannableString ss = new SpannableString(text);
        for(int i = 0; i < keyword.length; i++) {
            Pattern pattern = Pattern.compile(keyword[i]);
            Matcher matcher = pattern.matcher(ss);
            while (matcher.find()){
                int start = matcher.start();
                int end = matcher.end();
                ss.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ss;
    }

}
