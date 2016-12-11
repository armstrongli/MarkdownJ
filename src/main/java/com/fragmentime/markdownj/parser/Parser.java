package com.fragmentime.markdownj.parser;

import com.fragmentime.markdownj.analyzer.*;
import com.fragmentime.markdownj.elements.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Parser {

    public static String parseFile(File file) throws Exception {
        if (file == null) {
            return "";
        }

        BufferedReader br = null;
        java.util.List<String> content = new ArrayList<>();
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
        return analyzeContent(content);
    }

    public static String parseString(String mdString) throws Exception {
        BufferedReader br = null;
        java.util.List<String> content = new ArrayList<>();
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
        return analyzeContent(content);
    }

    public static String parseStrings(java.util.List<String> mdStrings) throws Exception {
        return analyzeContent(mdStrings);
    }

    private static String analyzeContent(java.util.List<String> content) throws Exception {
        if (content == null || content.size() == 0) {
            return "";
        }
        Element root = new Element();
        for (String item : content) {
            root.append(item);
        }

        getParser().analyzeContent(root);
        return root.render();
    }

    public static String parseFile(String fileName) throws Exception {
        return parseFile(new File(fileName));
    }

    private void analyzeContent(Element root) {
        for (Analyzer analyzerItem : this.analyzers) {
            analyzeContent(root, analyzerItem);
        }
    }

    private void analyzeContent(Element root, Analyzer analyzer) {
        if (root == null || analyzer == null) {
            return;
        }
        if (root.getData().size() == 0) {
            return;
        }
        if (root.hasRight()) {
            analyzeContent(root.getRight(), analyzer);
        } else {
            if (root.isAcceptAnalyzed()) {
                iterateAnalyze(analyzer, root);
                analyzeContent(root.getRight(), analyzer);
            }
        }
        if (root.hasLeft()) {
            analyzeContent(root.getLeft(), analyzer);
        }
    }

    private Parser() {
        this.init();
    }

    public static Parser getParser() {
        return new Parser();
    }

    private void init() {
        this.registerAnalyzer(new TextAnalyser());
        this.registerAnalyzer(new CodeBlockAnalyzer());
        this.registerAnalyzer(new DictionaryAnalyzer());
        this.registerAnalyzer(new HeaderAnalyzer());

        ListAnalyzer listAnalyzer = new ListAnalyzer();
        LiAnalyzer liAnalyzer = new LiAnalyzer();
        listAnalyzer.addSubAnalyzers(liAnalyzer);
        liAnalyzer.addSubAnalyzers(listAnalyzer);
        this.registerAnalyzer(listAnalyzer);

        this.registerAnalyzer(new TableAnalyzer());
    }

    private void iterateAnalyze(Analyzer analyzer, Element element) {
        boolean analyzeResult = analyzer.analyze(element);
        if (analyzeResult) {
            Element current = element.getRight();
            while (current != null) {
                if (analyzer.belongsToAnalyzer(current)) {
                    java.util.List<Analyzer> analyzers = analyzer.getSubAnalyzers();
                    for (Analyzer item : analyzers) {
                        iterateAnalyze(item, current);
                    }
                }
                current = current.getLeft();
            }
        }
    }

    private void registerAnalyzer(Analyzer analyzer) {
        if (analyzer == null) {
            throw new NullPointerException("Analyzer can't be null when registering new one");
        }
        this.analyzers.add(analyzer);
        Collections.sort(this.analyzers);
    }

    private java.util.List<Analyzer> analyzers = new ArrayList<>();
}
