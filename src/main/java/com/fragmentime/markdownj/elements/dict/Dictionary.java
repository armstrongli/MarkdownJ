package com.fragmentime.markdownj.elements.dict;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/11/15.
 */
public class Dictionary extends Element {
    public Dictionary() {
        setType(Element.DICTIONARY);
        setAcceptAnalyzed(false);
    }

    public static boolean isDictionary(String txt) {
        return txt != null && txt.trim().matches("([ ]+)?\\[.+\\]([ ]+)?\\:.{0,}");
    }

    @Override
    public String render() {
        if (this.hasLeft()) {
            return this.getLeft().render();
        }
        return "";
    }
}
