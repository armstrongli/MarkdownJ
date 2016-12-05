package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.list.List;

/**
 * Created by Beancan on 2016/12/3.
 */
public class ListAnalyzer extends Analyzer {
    public boolean belongsToAnalyzer(Element element) {
        return element != null && Element.LIST.equals(element.getType());
    }

    protected int getWeight() {
        return ANALYZER_LIST;
    }

    //    todo need to separate ol and ul
    private static boolean isList(String text) {
        return List.isList(text);
    }

    public void analyze(Element root) {
        if (root == null || root.getData().size() == 0) {
            return;
        }
        int i, listCount = 0;
        Element current = null;
        java.util.List<String> data = root.getData();
        for (i = 0; i < data.size(); i++) {

            String item = data.get(i);
            Element e;
            if (current == null) {
                if (isList(item)) {
                    e = new List();
                    listCount++;

                    i = appendListItem(e, data, i + 1);
                } else {
                    e = new Element();
                }
                e.append(item);

                e.setParent(root);
                root.setRight(e);
                current = e;
            } else {
                if (isList(item)) {
                    if (belongsToAnalyzer(current)) {
                        current.append(item);
                    } else {
                        e = new List();
                        e.append(item);
                        listCount++;

                        e.setParent(current);
                        current.setLeft(e);
                        current = e;
                    }
                    i = appendListItem(current, data, i + 1);
                } else {
                    if (belongsToAnalyzer(current)) {
                        e = new Element();
                        e.append(item);

                        e.setParent(current);
                        current.setLeft(e);
                        current = e;
                    } else {
                        current.append(item);
                    }
                }
            }
        }

        if (listCount == 0) {
            root.getRight().setParent(null);
            root.setRight(null);
        }
    }

    private int appendListItem(Element list, java.util.List<String> data, int j) {
        boolean breakUntilNotNull = false;
        for (; j < data.size(); j++) {
            String itemPlus = data.get(j);
            if (itemPlus.trim().length() == 0) {
                breakUntilNotNull = true;
            } else {
                if (breakUntilNotNull) {
                    break;
                } else {
                    list.append(itemPlus);
                }
            }
        }
        return j - 1;
    }
}
