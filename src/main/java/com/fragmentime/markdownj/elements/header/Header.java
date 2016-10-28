package com.fragmentime.markdownj.elements.header;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.exceptions.ElementNotMatchException;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Header extends Element {
    public static boolean isHeader(String content) {
        return content != null && content.trim().length() > 0 && content.trim().startsWith("#");
    }

    @Override
    public String getType() {
        return Element.HEADER;
    }

}
