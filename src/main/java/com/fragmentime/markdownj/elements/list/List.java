package com.fragmentime.markdownj.elements.list;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class List extends Element {
    public static boolean isList(String content) {
        return content != null && content.trim().startsWith("- ");
    }

    public static int indexOfList(String content) {
        int index = 0;
        while (content.charAt(index) == ' ') {
            index++;
        }
        return index;
    }

    @Override
    public String getType() {
        return Element.LIST;
    }
}
