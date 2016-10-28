package com.fragmentime.markdownj.elements.table;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class Table extends Element {
    public static boolean isTable(String content, String content2) {
        return content != null && content.trim().startsWith("|")
                &&
                content2 != null && content2.trim().startsWith("|---");
    }

    public static boolean isTableItem(String content) {
        return content != null && content.trim().startsWith("|");
    }
    @Override
    public String getType() {
        return Element.TABLE;
    }
}
