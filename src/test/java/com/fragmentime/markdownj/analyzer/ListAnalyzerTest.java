package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Beancan on 2016/12/6.
 */
public class ListAnalyzerTest {
    @Test
    public void ListTest() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-list.md");
        Assert.assertNotNull(e);
        new ListAnalyzer().analyze(e);
        System.out.println(e.render());
    }

    @Test
    public void ListLiTest() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-list.md");
        Assert.assertNotNull(e);
        new ListAnalyzer().analyze(e);
        Element current = e.getRight();
        LiAnalyzer liAnalyzer = new LiAnalyzer();
        while (current != null) {
            liAnalyzer.analyze(current);
            current = current.getLeft();
        }
        System.out.println(e.render());
    }

    @Test
    public void TestListLi() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-list.md");
        Assert.assertNotNull(e);
        ListAnalyzer listAnalyzer = new ListAnalyzer();
        LiAnalyzer liAnalyzer = new LiAnalyzer();
        listAnalyzer.addSubAnalyzers(liAnalyzer);
        liAnalyzer.addSubAnalyzers(listAnalyzer);

        iterateAnalyze(listAnalyzer, e);
        System.out.println(e.render());
    }

    private void iterateAnalyze(Analyzer analyzer, Element element) {
        boolean analyzeResult = analyzer.analyze(element);
        if (analyzeResult) {
            Element current = element.getRight();
            while (current != null) {
                if (analyzer.belongsToAnalyzer(current)) {
                    List<Analyzer> analyzers = analyzer.getSubAnalyzers();
                    for (Analyzer item : analyzers) {
                        iterateAnalyze(item, current);
                    }
                }
                current = current.getLeft();
            }
        }
    }
}
