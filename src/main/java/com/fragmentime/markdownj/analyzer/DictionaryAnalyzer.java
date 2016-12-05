package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.dict.Dictionary;

import java.util.Iterator;

/**
 * Created by Beancan on 2016/12/3.
 */
public class DictionaryAnalyzer extends Analyzer {
    public boolean belongsToAnalyzer(Element element) {
        return element != null && Element.DICTIONARY.equals(element.getType());
    }

    protected int getWeight() {
        return ANALYZER_DICT;
    }

    private static boolean isDictionary(String text) {
        return Dictionary.isDictionary(text);
    }

    public void analyze(Element root) {
        if (root == null || root.getData().size() == 0) {
            return;
        }
        Element current = null;
        Iterator<String> it = root.getData().iterator();
        while (it.hasNext()) {
            String item = it.next();
            if (current == null) {
                Element e = null;
                if (isDictionary(item)) {
                    e = new Dictionary();
                } else {
                    e = new Element();
                }
                e.append(item);
                root.setRight(e);
                e.setParent(root);
                current = e;
            } else {
                if (isDictionary(item)) {
                    Element e = new Dictionary();
                    e.append(item);
                    current.setLeft(e);
                    e.setParent(current);
                    current = e;
                } else {
                    if (!belongsToAnalyzer(current)) {
                        current.append(item);
                    } else {
                        Element e = new Element();
                        e.append(item);
                        e.setParent(current);
                        current.setLeft(e);
                        current = e;
                    }
                }
            }
        }
    }
}
