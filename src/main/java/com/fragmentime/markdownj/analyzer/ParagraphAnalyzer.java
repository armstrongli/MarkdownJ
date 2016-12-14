package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.paragraph.Paragraph;

import java.util.Iterator;

/**
 * Created by Beancan on 2016/12/14.
 */
public class ParagraphAnalyzer extends Analyzer {
    @Override
    public boolean belongsToAnalyzer(Element element) {
        return element != null && Element.PARAGRAPH.equals(element.getType());
    }

    @Override
    protected int getWeight() {
        return ANALYZER_PARAGRAPH;
    }

    @Override
    public boolean analyze(Element root) {
        if (root == null || root.getData().size() <= 2) {
            return false;
        }
        int pCount = 0;
        Iterator<String> it = root.getData().iterator();
        Element current = null;
        while (it.hasNext()) {
            String item = it.next();
            if (current == null) {
                if (item.trim().length() == 0) {
                    continue;
                }
                current = new Paragraph();
                current.append(item);
                pCount++;

                current.setParent(root);
                root.setRight(current);
            } else {
                if (item.trim().length() == 0) {
                    if (current.getData().size() == 0) {
                        continue;
                    }
                    Element p = new Paragraph();
                    pCount++;

                    p.setParent(current);
                    current.setLeft(p);
                    current = p;
                } else {
                    current.append(item);
                }
            }
        }
        if (pCount <= 1) {
            root.getRight().setParent(null);
            root.setRight(null);
        }
        return pCount > 1;
    }
}
