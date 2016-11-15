package com.fragmentime.markdownj.parser;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.block.Block;
import com.fragmentime.markdownj.elements.dict.Dictionary;
import com.fragmentime.markdownj.elements.header.Header;
import com.fragmentime.markdownj.elements.list.Li;
import com.fragmentime.markdownj.elements.list.List;
import com.fragmentime.markdownj.elements.separator.Separator;
import com.fragmentime.markdownj.elements.table.*;
import com.fragmentime.markdownj.elements.text.Text;
import com.fragmentime.markdownj.exceptions.ElementNotMatchException;
import com.fragmentime.markdownj.logger.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Parser {

    public static String parseFile(File file) throws Exception {
        if (file == null) {
            return "";
        }

        BufferedReader br = null;
        java.util.List<String> content = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(file));
            while (true) {
                String tmp = br.readLine();
                if (tmp == null) {
                    break;
                }
                content.add(tmp);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return parseContent(content);
    }

    public static String parseString(String mdString) throws Exception {
        BufferedReader br = null;
        java.util.List<String> content = new ArrayList<String>();
        try {
            br = new BufferedReader(new StringReader(mdString));
            while (true) {
                String tmp = br.readLine();
                if (tmp == null) {
                    break;
                }
                content.add(tmp);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return parseContent(content);
    }

    private static String parseContent(java.util.List<String> content) throws Exception {
        if (content == null || content.size() == 0) {
            return "";
        }
        Element root = new Element();
        for (String item : content) {
            root.append(item);
        }
        analyzeElementTree(root);
        return root.render();
    }

    public static String parseFile(String fileName) throws Exception {
        return parseFile(new File(fileName));
    }

    private static Element analyzeElementTree(Element elementTree) {
        analyze(elementTree);
        analyzeList(elementTree);
        analyzeTable(elementTree);
        analyzeText(elementTree);
        return elementTree;
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
        analyzeTable(element.getRight());
        analyzeTable(element.getLeft());
    }

    private static void combine(Element element) throws ElementNotMatchException {
        if (element == null || element.getData().size() == 0) {
            return;
        }
        for (String item : element.getData()) {
            Element eRLeftest = getRightLeftest(element);
            Element e = null;
            if (item.trim().length() == 0) {
                e = new Separator();
            } else if (Header.isHeader(item)) {
                e = new Header();
                e.append(item);
            } else {
                e = new Element();
                e.append(item);
            }
            if (eRLeftest == null) {
                element.setRight(e);
                e.setParent(element);
            } else {
                if (e instanceof Header || e instanceof Separator) {
                    if (eRLeftest instanceof Separator) {
                        continue;
                    }
                    eRLeftest.setLeft(e);
                    e.setParent(eRLeftest);
                } else {
                    eRLeftest.append(e.getData().get(0));
                }
            }
        }
    }

    private static Element getRightLeftest(Element element) {
        if (!element.hasRight()) {
            return null;
        }
        Element e = element.getRight();
        while (e.hasLeft()) {
            e = e.getLeft();
        }
        return e;
    }

    private static void analyze(Element element) {
        if (element == null) {
            return;
        }
        if (element.getType() == null) {
            if (element.getData().size() == 0) {
                return;
            }
            for (int index = 0; index < element.getData().size(); index++) {
                String item = element.getData().get(index);
                // System.out.println(index+"---"+item);
                Element e = null;
                Element eRLeftest = getRightLeftest(element);
                if (eRLeftest == null) {
                    if (Block.isBlock(item)) {
                        e = new Block();
                    } else if (Header.isHeader(item)) {
                        e = new Header();
                        Element filler = new Text();
                        filler.append(item.replaceFirst("#+", ""));
                        filler.setParent(e);
                        e.setRight(filler);
                    } else if (Dictionary.isDictionary(item)) {
                        e = new Dictionary();
                    } else if (List.isList(item)) {
                        e = new List();
                    } else if (Separator.isSeparator(item)) {
                        e = new Separator();
                    } else {
                        String itemP1 = element.getData().get(index + 1 == element.getData().size() ? index : index + 1);
                        if (item != itemP1 && Table.isTable(item, itemP1)) {
                            e = new Table();
                            Element current = e;
                            TableHead th = new TableHead();
                            th.append(item);
                            th.setParent(current);
                            current.setRight(th);
                            current = th;

                            TableColumnDefine tcd = new TableColumnDefine();
                            tcd.append(itemP1);
                            current.setLeft(tcd);
                            tcd.setParent(current);
                            current = tcd;

                            int tableIndex = index + 2;
                            while (tableIndex < element.getData().size()) {
                                String potentialTableItem = element.getData().get(tableIndex);
                                if (!Table.isTableItem(potentialTableItem)) {
                                    break;
                                }
                                TableRow tr = new TableRow();
                                tr.append(element.getData().get(tableIndex));
                                current.setLeft(tr);
                                tr.setParent(current);
                                current = tr;

                                tableIndex++;
                            }
                            index = tableIndex - 1;
                        } else {
                            e = new Text();
                        }
                    }
                    e.append(item);
                    element.setRight(e);
                    e.setParent(element);
                } else {
                    // append block data
                    if (eRLeftest instanceof Block) {
                        if (!((Block) eRLeftest).isBlockEnd()) {
                            eRLeftest.append(item);
                            continue;
                        }
                    }
                    // skip multiple separator
                    if (eRLeftest instanceof Separator) {
                        if (Separator.isSeparator(item)) {
                            continue;
                        }
                    }
                    // append list items
                    if (eRLeftest instanceof List) {
                        if (!Block.isBlock(item) && !Header.isHeader(item) && !Separator.isSeparator(item)) {
                            eRLeftest.append(item);
                            continue;
                        }
                    }
                    // append table item
                    if (eRLeftest instanceof Table) {
                        if (Table.isTableItem(item)) {
                            eRLeftest.append(item);
                            continue;
                        }
                    }
                    // append text
                    if (eRLeftest instanceof Text) {
                        if (!Block.isBlock(item) && !Header.isHeader(item) && !List.isList(item) && !Separator.isSeparator(item)) {
                            eRLeftest.append(item);
                            continue;
                        }
                    }

                    if (Block.isBlock(item)) {
                        e = new Block();
                    } else if (Header.isHeader(item)) {
                        e = new Header();
                        Element filler = new Text();
                        filler.append(item.replaceFirst("#+", ""));
                        filler.setParent(e);
                        e.setRight(filler);
                    } else if (Dictionary.isDictionary(item)) {
                        e = new Dictionary();
                    } else if (List.isList(item)) {
                        e = new List();
                    } else if (Separator.isSeparator(item)) {
                        e = new Separator();
                    } else {
                        String itemP1 = element.getData().get(index + 1 == element.getData().size() ? index : index + 1);
                        if (item != itemP1 && Table.isTable(item, itemP1)) {
                            e = new Table();
                            Element current = e;
                            TableHead th = new TableHead();
                            th.append(item);
                            th.setParent(current);
                            current.setRight(th);
                            current = th;

                            TableColumnDefine tcd = new TableColumnDefine();
                            tcd.append(itemP1);
                            current.setLeft(tcd);
                            tcd.setParent(current);
                            current = tcd;

                            int tableIndex = index + 2;
                            while (tableIndex < element.getData().size()) {
                                String potentialTableItem = element.getData().get(tableIndex);
                                if (!Table.isTableItem(potentialTableItem)) {
                                    break;
                                }
                                TableRow tr = new TableRow();
                                tr.append(element.getData().get(tableIndex));
                                current.setLeft(tr);
                                tr.setParent(current);
                                current = tr;

                                tableIndex++;
                            }
                            index = tableIndex - 1;
                        } else {
                            e = new Text();
                        }
                    }
                    e.append(item);
                    eRLeftest.setLeft(e);
                    e.setParent(eRLeftest);
                }
            }
            //analyze to create right tree
            analyze(element.getRight()); // reanalyze the right tree
        }
        analyze(element.getLeft());
    }

    private static void analyzeList(Element element) {
        if (element == null) {
            return;
        }
        if (element instanceof List) {
            int index = List.indexOfList(element.getData().get(0));
            for (String item : element.getData()) {
                Element rLeftest = getRightLeftest(element);
                if (rLeftest == null) {
                    Li li = new Li();
                    String txt = item.trim();
                    li.append(txt.substring(txt.indexOf(' ') + 1));
                    element.setRight(li);
                    li.setParent(element);
                    continue;
                }
                if (!List.isList(item)) {
                    rLeftest.append(item);
                    continue;
                }
                if (List.indexOfList(item) == index) {
                    Li li = new Li();
                    String txt = item.trim();
                    li.append(txt.substring(txt.indexOf(' ') + 1));
                    rLeftest.setLeft(li);
                    li.setParent(rLeftest);
                } else {
                    if (rLeftest instanceof Li) {
                        // new list
                        List list = new List();
                        list.append(item);
                        rLeftest.setLeft(list);
                        list.setParent(rLeftest);
                    } else {
                        // append
                        rLeftest.append(item);
                    }
                }

            }
        }
        analyzeList(element.getLeft());
        analyzeList(element.getRight());
    }

    private static void analyzeText(Element element) {
        if (element == null) {
            return;
        }
        if (element.hasRight()) {
            analyzeText(element.getRight());
        } else {
            if (Arrays.asList(Element.TEXT, Element.HEADER, Element.LI).contains(element.getType())) {
                StringBuilder sb = new StringBuilder();
                for (String item : element.getData()) {
                    sb.append(item).append(" ");
                }
                sb.deleteCharAt(sb.length() - 1);
                Element e = new TextAnalyser(sb.toString()).analyze();
                element.setRight(e);
                e.setParent(element);
                analyzeText(e.getRight());
            }
        }

        analyzeText(element.getLeft());
    }

    private static java.util.List<String> iterator(Element element) {
        if (!element.hasLeft() && !element.hasRight()) {
            Log.log("Adding " + element.getType());
            return new ArrayList<String>(element.getData());
        }
        java.util.List<String> result = new ArrayList<String>();
        if (!element.hasRight()) {
            Log.log("Adding " + element.getType());
            result.addAll(element.getData());
        } else {
            result.addAll(iterator(element.getRight()));
        }
        if (element.hasLeft()) {
            result.addAll(iterator(element.getLeft()));
        }

        return result;
    }
}
