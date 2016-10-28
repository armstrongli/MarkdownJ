package com.fragmentime.markdownj.elements.separator;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class Separator extends Element {

    public static boolean isSeparator(String item) {
        return item != null && item.trim().length() == 0;
    }
    @Override
    public String getType() {
        return Element.SEPARATOR;
    }
}
