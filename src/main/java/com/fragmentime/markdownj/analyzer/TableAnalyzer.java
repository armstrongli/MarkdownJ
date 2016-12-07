package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.table.Table;

import java.util.List;

/**
 * Created by Beancan on 2016/12/3.
 */
public class TableAnalyzer extends Analyzer {
    public boolean belongsToAnalyzer(Element element) {
        return element != null && Element.TABLE.equals(element.getType());
    }

    protected int getWeight() {
        return ANALYZER_TABLE;
    }

    private static boolean isTable(String header, String definition) {
        return Table.isTable(header, definition);
    }

    public boolean analyze(Element root) {
        if (root == null || root.getData().size() == 0) {
            return false;
        }
        int tableCount = 0;
        int i, j;
        Element current = null;
        List<String> data = root.getData();
        for (i = 0; i < data.size(); i++) {
            String item = data.get(i);
            j = i + 1;
            String itemPlus = j == data.size() ? null : data.get(j);
            Element e = null;
            if (isTable(item, itemPlus)) {
                e = new Table();
                tableCount++;
                e.append(item).append(itemPlus);
                for (j = j + 1; j < data.size(); j++) {
                    String tableItem = data.get(j);
                    if (item.trim().length() > 0) {
                        e.append(tableItem);
                    } else {
                        i = j - 1;
                        break;
                    }
                }
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
                if (e == null) {
                    current.append(item);
                } else {
                    e.setParent(current);
                    current.setLeft(e);
                    current = e;
                }
            }
        }

        if (tableCount == 0) {
            root.getRight().setParent(null);
            root.setRight(null);
        }
        return tableCount > 0;
    }
}
