package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.list.Li;
import com.fragmentime.markdownj.elements.list.List;

import java.util.Iterator;

/**
 * Created by Beancan on 2016/12/6.
 */
public class LiAnalyzer extends Analyzer {
    @Override
    public boolean belongsToAnalyzer(Element element) {
        return element != null && Element.LI.equals(element.getType());
    }

    @Override
    protected int getWeight() {
        return ANALYZER_LI;
    }

    private static int getListDeep(String text) {
        return List.indexOfList(text);
    }

    private static boolean isListItem(String text) {
        return List.isList(text);
    }

    private static String trimLiItem(String text) {
        if (!isListItem(text)) {
            return text;
        }
        String trimedItem = text.trim();
        return trimedItem.substring(trimedItem.indexOf(' ') + 1);
    }

    @Override
    public boolean analyze(Element root) {
        if (root == null || !Element.LIST.equals(root.getType()) || root.getData().size() == 0) {
            return false;
        }
        int deep = getListDeep(root.getData().get(0));
        Element current = null;
        Iterator<String> it = root.getData().iterator();
        while (it.hasNext()) {
            String item = it.next();
            if (current == null) {
                current = new Li();
                current.append(trimLiItem(item));

                root.setRight(current);
                current.setParent(root);
            } else {
                if (!isListItem(item) || (isListItem(item) && getListDeep(item) > deep)) {
                    current.append(item);
                } else {
                    Li li = new Li();
                    li.append(trimLiItem(item));

                    li.setParent(current);
                    current.setLeft(li);
                    current = li;
                }
            }
        }
        return true;
    }
}
