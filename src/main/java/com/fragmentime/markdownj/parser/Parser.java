package com.fragmentime.markdownj.parser;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.block.Block;
import com.fragmentime.markdownj.elements.header.Header;
import com.fragmentime.markdownj.elements.list.Li;
import com.fragmentime.markdownj.elements.list.List;
import com.fragmentime.markdownj.elements.separator.Separator;
import com.fragmentime.markdownj.elements.table.Table;
import com.fragmentime.markdownj.elements.text.Text;
import com.fragmentime.markdownj.exceptions.ElementNotMatchException;
import com.fragmentime.markdownj.logger.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Parser {

    public static String parse(String fileName) throws Exception {
        BufferedReader br = null;
        Element root = new Element();
        try {
            br = new BufferedReader(new FileReader(new File(fileName)));
            while (true) {
                String tmp = br.readLine();
                if (tmp == null) {
                    break;
                }
                root.append(tmp);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        analyze(root);
        analyzeList(root);

        java.util.List<String> result = iterator(root);
        for (String item : result) {
            System.out.println(item);
        }

        System.out.println(root.render());
        return null;
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

    public static Element getRightLeftest(Element element) {
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
                    } else if (List.isList(item)) {
                        e = new List();
                    } else if (Separator.isSeparator(item)) {
                        e = new Separator();
                    } else if (Table.isTable(item, "|---")) {
                        // element.getData().get(index + 1)
                        //todo need to recalculate to avoid fake table
                        e = new Table();
                    } else {
                        e = new Text();
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
                    } else if (List.isList(item)) {
                        e = new List();
                    } else if (Separator.isSeparator(item)) {
                        e = new Separator();
                    } else if (Table.isTable(item, "|---")) {
                        //todo need to recalculate to avoid fake table
                        e = new Table();
                    } else {
                        e = new Text();
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
                    li.append(item);
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
                    li.append(item);
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

    private static final String KEY_CHARS = "*_~`";

    private static Element analyzeText(Element text) {
        if (text == null || text.getData().size() == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer("");
        for (String item : text.getData()) {
            sb.append(item);
        }
        if (Text.hasImage(sb.toString())) {

        }

        return null;
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
