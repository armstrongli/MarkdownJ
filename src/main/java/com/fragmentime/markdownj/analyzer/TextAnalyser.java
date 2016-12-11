package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.text.Text;
import com.fragmentime.markdownj.logger.Log;

import java.util.*;

/**
 * Created by Beancan on 2016/11/2.
 */
public class TextAnalyser extends Analyzer {

    public boolean belongsToAnalyzer(Element element) {
        return true;
    }

    protected int getWeight() {
        return ANALYZER_TEXT;
    }

    public boolean analyze(Element root) {
        if (root == null || root.getData().size() == 0) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = root.getData().iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(" ");
            }
        }
        String text = sb.toString();
        if (text.trim().length() == 0) {
            return false;
        }
        Element e = new TextAnalyser(sb.toString()).analyze();
        root.setRight(e);
        e.setParent(root);
        return false;
    }

    private static class TextIndexer implements Comparable<TextIndexer> {
        private TextIndexer parent;
        private TextIndexer left;
        private TextIndexer right;

        private final String type;
        private final int start;
        private final int end;

        public TextIndexer(String type, int start, int end) {
            this.type = type;
            this.start = start;
            this.end = end;
        }

        public int compareTo(TextIndexer o) {
            return this.start - o.start;
        }

        @Override
        public String toString() {
            return type + ": " + start + "-" + end;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TextIndexer) {
                TextIndexer b = (TextIndexer) obj;
                return this.type.equals(b.type) && this.start == b.start && this.end == b.end;
            } else {
                return false;
            }
        }
    }

    private List<TextIndexer> indexers = new ArrayList<>();

    private String text;

    public TextAnalyser(String text) {
        this.text = text;
    }


    public TextAnalyser() {
    }

    public Element analyze(String text) {
        return null;
    }

    private TextIndexer analyzeElementsTypes() {
        // analyze types
        List<Analyser> analysers = Arrays.asList(new BlockAnalyser(), new ImageAnalyser(), new LinkAnalyser(), new BoldAnalyser(), new ItalicAnalyser());

        StringBuffer multiplyStrings = new StringBuffer(this.text);
        for (Analyser item : analysers) {
            multiplyStrings = analyzeAndMultiply(multiplyStrings, item, this.indexers);
        }
        Collections.sort(this.indexers);
        for (TextIndexer item : this.indexers) {
            Log.log(item.type + ": " + item.start + "-" + item.end);
        }

        // build render tree and fill blanks
        ReactTree rt = new ReactTree(new TextIndexer(Element.TEXT, 0, this.text.length()), this.text);
        rt.buildReactTree(this.indexers);
        rt.fillBlanks();
        return rt.root;
    }

    private Element buildElementTree(TextIndexer ti) {
        if (ti == null) {
            return null;
        }

        Text t = new Text();
        t.append(this.text.substring(ti.start, ti.end));
        t.setType(ti.type);
        t.setAcceptAnalyzed(false);

        t.setRight(buildElementTree(ti.right));
        t.setLeft(buildElementTree(ti.left));
        return t;
    }

    public Element analyze() {
        TextIndexer reactTree = this.analyzeElementsTypes();
        Element tree = buildElementTree(reactTree);
        tree.setAcceptAnalyzed(false);
        return tree;
    }

    private static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        while (length > 0) {
            sb.append("A");
            length--;
        }
        return sb.toString();
    }

    private static StringBuffer analyzeAndMultiply(StringBuffer origin, Analyser analyser, List<TextIndexer> indexers) {
        String textA = origin.toString();
        if (!analyser.has(textA)) {
            return origin;
        }
        StringBuffer multiplyStrings = new StringBuffer("");
        List<String> txts = analyser.split(textA);
        int leftIndex = 0;
        for (String item : txts) {
            if (analyser.has(item)) {
                if (analyser instanceof BoldAnalyser) {
                    // fill dead zone of bold & italic
                    StringBuffer boldString = new StringBuffer(origin.substring(leftIndex + 2, leftIndex + item.length() - 2));
                    List<TextIndexer> subBoldIndexers = new ArrayList<>();


                    List<Analyser> analysers = Arrays.asList(new BlockAnalyser(), new ImageAnalyser(), new LinkAnalyser(), new BoldAnalyser(), new ItalicAnalyser());
                    for (Analyser subitem : analysers) {
                        boldString = analyzeAndMultiply(boldString, subitem, subBoldIndexers);
                    }
                    if (subBoldIndexers.size() > 0) {
                        // there're sub elements in bold string
                        for (TextIndexer tiItem : subBoldIndexers) {
                            indexers.add(new TextIndexer(tiItem.type, leftIndex + 2 + tiItem.start, leftIndex + 2 + tiItem.end));
                        }
                    }
                }
                indexers.add(new TextIndexer(analyser.getType(), leftIndex, leftIndex + item.length()));

                multiplyStrings.append(generateString(item.length()));
            } else {
                multiplyStrings.append(item);
            }
            leftIndex += item.length();
        }
        return multiplyStrings;
    }

    private static class ReactTree {
        private TextIndexer root;
        private final String context;

        public ReactTree(TextIndexer root, String context) {
            this.root = root;
            this.context = context;
        }

        private TextIndexer buildReactTree(List<TextIndexer> nodes) {
            if (nodes == null || nodes.size() == 0) {
                return null;
            }
            for (TextIndexer item : nodes) {
                insertTextIndexer(item);
            }
            return this.root;
        }

        private void fillBlanks() {
            fillBlanks(this.root);
        }

        private void fillBlanks(TextIndexer node) {
            if (node == null) {
                return;
            }
            if (node.right != null) {
                int minStart = node.right.start;
                int maxEnd = node.right.end;
                TextIndexer current = node.right, lastLeft = current;
                while (current != null) {
                    {
                        // fill middle and find the max end
                        int currentEnd = current.end;
                        TextIndexer ti = current.left;
                        if (ti == null) {
                            maxEnd = current.end;
                            break;
                        }
                        int nextStart = ti.start;
                        if (currentEnd + 1 < nextStart) {
                            TextIndexer filler = new TextIndexer(Element.TEXT, currentEnd + 1, nextStart - 1);
                            filler.parent = current;
                            filler.left = ti;
                            current.left = filler;
                            ti.parent = filler;

                            current = filler;
                        }
                    }
                    maxEnd = current.end;
                    lastLeft = current;
                    current = current.left;
                }
                if (node.start < minStart) {
                    // fill start
                    int fillStartStart = node.start;
                    if (Text.TEXT_ITALIC.equals(node.type)) {
                        fillStartStart += 1;
                    } else if (Text.TEXT_BOLD.equals(node.type)) {
                        fillStartStart += 2;
                    } else if (Text.TEXT_LINK.equals(node.type)) {
                        fillStartStart += 1;
                    }
                    TextIndexer filler = new TextIndexer(Element.TEXT, fillStartStart, minStart);
                    filler.parent = node;
                    filler.left = node.right;
                    node.right.parent = filler;
                    node.right = filler;
                }
                if (maxEnd < node.end) {
                    // fill end
                    int fillEndEnd = node.end;
                    if (Text.TEXT_ITALIC.equals(node.type)) {
                        fillEndEnd -= 1;
                    } else if (Text.TEXT_BOLD.equals(node.type)) {
                        fillEndEnd -= 2;
                    } else if (Text.TEXT_LINK.equals(node.type)) {
                        String tmp = this.context.substring(maxEnd, node.end);
                        int linkTextEnd = tmp.indexOf(']');
                        fillEndEnd = maxEnd + linkTextEnd;
                    }
                    TextIndexer filler = new TextIndexer(Element.TEXT, maxEnd, fillEndEnd);
                    filler.parent = lastLeft;
                    lastLeft.left = filler;
                }
            }
            fillBlanks(node.right);
            fillBlanks(node.left);
        }

        private void insertTextIndexer(TextIndexer node) {
            if (root == null) {
                root = node;
                return;
            }
            TextIndexer current = root;
            while (true) {
                if (node.end <= current.end) {
                    if (current.right == null) {
                        current.right = node;
                        node.parent = current;
                        break;
                    } else {
                        current = current.right;
                    }
                } else {
                    if (current.left == null) {
                        current.left = node;
                        node.parent = current;
                        break;
                    } else {
                        current = current.left;
                    }
                }
            }
        }

    }

    private interface Analyser {
        List<String> split(String text);

        boolean has(String text);

        String getType();
    }

    private static class BlockAnalyser implements Analyser {

        public List<String> split(String text) {
            return Text.splitBlock(text);
        }

        public boolean has(String text) {
            return Text.hasBlock(text);
        }

        public String getType() {
            return Text.TEXT_BLOCK;
        }
    }

    private static class ImageAnalyser implements Analyser {

        public List<String> split(String text) {
            return Text.splitImage(text);
        }

        public boolean has(String text) {
            return Text.hasImage(text);
        }

        public String getType() {
            return Text.TEXT_IMAGE;
        }
    }

    private static class LinkAnalyser implements Analyser {

        public List<String> split(String text) {
            return Text.splitLink(text);
        }

        public boolean has(String text) {
            return Text.hasLink(text);
        }

        public String getType() {
            return Text.TEXT_LINK;
        }
    }

    private static class ItalicAnalyser implements Analyser {

        public List<String> split(String text) {
            return Text.splitItalic(text);
        }

        public boolean has(String text) {
            return Text.hasItalic(text);
        }

        public String getType() {
            return Text.TEXT_ITALIC;
        }
    }

    private static class BoldAnalyser implements Analyser {

        public List<String> split(String text) {
            return Text.splitBold(text);
        }

        public boolean has(String text) {
            return Text.hasBold(text);
        }

        public String getType() {
            return Text.TEXT_BOLD;
        }
    }
}
