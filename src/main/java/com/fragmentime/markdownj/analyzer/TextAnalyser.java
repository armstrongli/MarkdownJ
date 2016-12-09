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
    }

    private List<TextIndexer> indexers = new ArrayList<TextIndexer>();

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
            // Log.log(multiplyStrings.toString());
            multiplyStrings = analyzeAndMultiply(multiplyStrings, item, this.indexers);
        }
        Collections.sort(this.indexers);
        for (TextIndexer item : this.indexers) {
            Log.log(item.type + ": " + item.start + "-" + item.end);
        }

        // build render tree and fill blanks
        TextIndexer current = new TextIndexer(Element.TEXT, 0, this.text.length());
        TextIndexer root = current;
        for (TextIndexer item : this.indexers) {
            if (item.start < current.end) {
                if (item.start > current.start) {
                    int fillerStart = current.start;
                    if (Text.TEXT_BOLD.equals(current.type)) {
                        fillerStart += 2;
                    } else if (Text.TEXT_ITALIC.equals(current.type)) {
                        fillerStart += 1;
                    }
                    TextIndexer filler = new TextIndexer(Element.TEXT, fillerStart, item.start);
                    current.right = filler;
                    filler.parent = current;
                    current = filler;
                }
                if (current.parent != null && current == current.parent.right) {
                    current.left = item;
                    item.parent = current;
                } else {
                    current.right = item;
                    item.parent = current;
                }
                current = item;
            } else {
                while (true) {
                    TextIndexer parentOfCurrent = current;
                    if (current == root) {
                        break;
                    }
                    while (true) {
                        if (parentOfCurrent == parentOfCurrent.parent.right) {
                            parentOfCurrent = parentOfCurrent.parent;
                            break;
                        } else {
                            parentOfCurrent = parentOfCurrent.parent;
                        }
                    }
                    if (item.end < parentOfCurrent.end) {
                        if (item.start > current.end) {
                            TextIndexer filler = new TextIndexer(Element.TEXT, current.end, item.start);
                            current.left = filler;
                            filler.parent = current;
                            current = filler;
                        }
                        item.parent = current;
                        current.left = item;
                        current = item;
                        break;
                    } else {
                        if (parentOfCurrent.end >= current.end) {
                            int fillerEnd = parentOfCurrent.end;
                            if (Text.TEXT_BOLD.equals(current.type)) {
                                fillerEnd -= 2;
                            } else if (Text.TEXT_ITALIC.equals(current.type)) {
                                fillerEnd -= 1;
                            }
                            TextIndexer filler = new TextIndexer(Element.TEXT, current.end, fillerEnd);
                            current.left = filler;
                            filler.parent = current;
                        }
                        current = parentOfCurrent;
                    }
                }
            }
        }
        if (current.end < this.text.length()) {
            while (true) {
                TextIndexer parentOfCurrent = current;
                while (true) {
                    if (parentOfCurrent == parentOfCurrent.parent.right) {
                        parentOfCurrent = parentOfCurrent.parent;
                        break;
                    } else {
                        parentOfCurrent = parentOfCurrent.parent;
                    }
                }
                if (parentOfCurrent.end > current.end) {
                    int fillerEnd = parentOfCurrent.end;
                    if (Text.TEXT_BOLD.equals(current.type)) {
                        fillerEnd -= 2;
                    } else if (Text.TEXT_ITALIC.equals(current.type)) {
                        fillerEnd -= 1;
                    }
                    TextIndexer filler = new TextIndexer(Element.TEXT, current.end, fillerEnd);
                    current.left = filler;
                    filler.parent = current;
                }
                current = parentOfCurrent;
                if (current == root) {
                    break;
                }
            }
        }
        return root;
    }

    private Element buildElementTree(TextIndexer ti) {
        if (ti == null) {
            return null;
        }

        Text t = new Text();
        t.append(this.text.substring(ti.start, ti.end));
        t.setType(ti.type);

        t.setRight(buildElementTree(ti.right));
        t.setLeft(buildElementTree(ti.left));
        return t;
    }

    public Element analyze() {
        TextIndexer reactTree = this.analyzeElementsTypes();
        return buildElementTree(reactTree);
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
                indexers.add(new TextIndexer(analyser.getType(), leftIndex, leftIndex + item.length()));
                multiplyStrings.append(generateString(item.length()));
            } else {
                multiplyStrings.append(item);
            }
            leftIndex += item.length();
        }
        return multiplyStrings;
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
