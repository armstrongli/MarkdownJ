package com.fragmentime.markdownj.parser;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.text.Text;
import com.fragmentime.markdownj.logger.Log;

import java.util.*;

/**
 * Created by Beancan on 2016/11/2.
 */
public class TextAnalyser {
    private static class TextIndexer implements Comparable<TextIndexer> {
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
    }

    private Stack<TextIndexer> stack = new Stack<TextIndexer>();
    private List<TextIndexer> indexers = new ArrayList<TextIndexer>();

    private String text;

    public TextAnalyser(String text) {
        this.text = text;
    }

    private void analyzeElementsTypes() {
        List<Analyser> analysers = Arrays.asList(new BlockAnalyser(), new ImageAnalyser(), new LinkAnalyser(), new BoldAnalyser(), new ItalicAnalyser());

        StringBuffer multiplyStrings = new StringBuffer(this.text);
        for (Analyser item : analysers) {
//            Log.log(multiplyStrings.toString());
            multiplyStrings = analyzeAndMultiply(multiplyStrings, item, this.indexers);
        }
        Collections.sort(this.indexers);
        for (TextIndexer item : this.indexers) {
            Log.log(item.type + ": " + item.start + "-" + item.end);
        }
    }

    public void analyze() {
        this.analyzeElementsTypes();

        Element text = new Element(), current = null;
        text.append(this.text);

        for (TextIndexer item : this.indexers) {
            if (current == null) {
                Text e = new Text();
                e.append(this.text.substring(item.start, item.end));
                e.setType(item.type);
                current = e;
                this.stack.push(item);
            } else {
                TextIndexer previousIndex = null;
                while (true) {
                    if (this.stack.size() == 0) {
                        break;
                    }
                    previousIndex = this.stack.pop();
                    if (item.start > previousIndex.end) {
                        while (true) {
                            if (current.getParent() == null) {
                                break;
                            }
                            if (current == current.getParent().getRight()) {
                                current = current.getParent();
                                break;
                            } else {
                                current = current.getParent();
                            }
                        }
                    } else {
                        this.stack.push(previousIndex);
                        break;
                    }
                }
                Text e = new Text();
                e.append(this.text.substring(item.start, item.end));
                e.setType(item.type);
                current.setLeft(e);
                e.setParent(current);
                current = e;
                this.stack.push(item);
            }
        }
        while (current != null && current.getParent() != null) {
            current = current.getParent();
        }
        System.out.println("Done");
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
