package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.header.Header;

import java.util.List;

/**
 * Created by Beancan on 2016/12/3.
 */
public class HeaderAnalyzer extends Analyzer {
    public boolean belongsToAnalyzer(Element element) {
        return element != null && Element.HEADER.equals(element.getType());
    }

    protected int getWeight() {
        return ANALYZER_HEADER;
    }

    private static int isHeader(String text) {
        return Header.isHeader(text) ? 1 : 0;
    }

    private static int isHeader(String text, String textPlus) {
        if (isHeader(text) > 0) {
            return 1;
        }
        if (textPlus != null && (textPlus.trim().matches("\\-+") || textPlus.trim().matches("=+"))) {
            return 2;
        }
        return 0;
    }

    public void analyze(Element root) {
        int headerCount = 0;
        if (root == null || root.getData().size() == 0) {
            return;
        }
        List<String> data = root.getData();
        int i, j;
        Element current = null;
        for (i = 0; i < data.size(); i++) {
            j = i + 1;
            String item = data.get(i);
            String itemPlus = j == data.size() ? null : data.get(j);

            int headerResult = isHeader(item, itemPlus);
            Element e = null;
            if (headerResult == 2) {
                e = new Header();
                e.append(item).append(itemPlus);
                headerCount++;
            } else if (headerResult == 1) {
                e = new Header();
                e.append(item);
                headerCount++;
            }
            if (current == null) {
                if (e == null) {
                    e = new Element();
                    e.append(item);
                }
                e.setParent(root);
                root.setRight(e);
                current = e;
            } else {
                if (belongsToAnalyzer(current)) {
                    if (e == null) {
                        e = new Element();
                        e.append(item);
                    }

                    e.setParent(current);
                    current.setLeft(e);
                    current = e;
                } else {
                    if (e == null) {
                        current.append(item);
                    } else {
                        e.setParent(current);
                        current.setLeft(e);
                        current = e;
                    }
                }
            }
        }
        if (headerCount == 0) {
            root.getRight().setParent(null);
            root.setRight(null);
        }
    }
}
