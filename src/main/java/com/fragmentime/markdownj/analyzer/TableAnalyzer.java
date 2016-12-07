package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.table.*;

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
                TableHead head = new TableHead();
                head.append(item);

                head.setParent(e);
                e.setRight(head);

                TableColumnDefine tableColumnDefine = new TableColumnDefine();
                tableColumnDefine.append(itemPlus);

                head.setLeft(tableColumnDefine);

                Element tableRowItem = null;
                for (j = j + 1; j < data.size(); j++) {
                    String tableItem = data.get(j);
                    if (item.trim().length() > 0) {
                        e.append(tableItem);

                        if (tableRowItem == null) {
                            tableRowItem = new TableRow();
                            tableRowItem.append(tableItem);

                            tableRowItem.setParent(tableColumnDefine);
                            tableColumnDefine.setLeft(tableRowItem);
                        } else {
                            TableRow tmpTableRow = new TableRow();
                            tmpTableRow.append(tableItem);

                            tableRowItem.setLeft(tmpTableRow);
                            tmpTableRow.setParent(tableRowItem);
                            tableRowItem = tmpTableRow;
                        }
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
        current = root.getRight();
        while (current != null) {
            if (this.belongsToAnalyzer(current)) {
                analyzeTable(current);
            }
            current = current.getLeft();
        }
        return tableCount > 0;
    }


    private static void analyzeTable(Element element) {
        if (element == null) {
            return;
        }
        if (element instanceof Table) {
            Element header = element.getRight();
            Element rowDefinition = header.getLeft();
            int columns = rowDefinition.getData().get(0).split("\\|").length;
            String[] cells = header.getData().get(0).split("\\|", columns);

            Element current = null, stake = null;
            for (int i = 1; i < columns; i++) {
                String data = (i > cells.length - 1) ? "&nbsp;" : cells[i].trim();
                if (data.endsWith("|")) {
                    data = data.substring(0, data.length() - 1);
                }
                TableCell cell = new TableCell();
                cell.append(data);
                if (current == null) {
                    stake = current = cell;
                } else {
                    cell.setParent(current);
                    current.setLeft(cell);
                    current = cell;
                }
            }
            header.setRight(stake);
            Element row = rowDefinition.getLeft();
            while (row != null) {
                current = stake = null;
                cells = row.getData().get(0).split("\\|", columns);
                for (int i = 1; i < columns; i++) {
                    String data = (i > cells.length - 1) ? "&nbsp;" : cells[i].trim();
                    if (data.endsWith("|")) {
                        data = data.substring(0, data.length() - 1);
                    }
                    TableCell cell = new TableCell();
                    cell.append(data);
                    if (current == null) {
                        stake = current = cell;
                    } else {
                        cell.setParent(current);
                        current.setLeft(cell);
                        current = cell;
                    }
                }
                row.setRight(stake);
                row = row.getLeft();
            }
        }
    }
}
