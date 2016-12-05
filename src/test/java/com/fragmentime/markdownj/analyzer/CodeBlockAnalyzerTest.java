package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import org.junit.Assert;

/**
 * Created by Beancan on 2016/12/3.
 */
public class CodeBlockAnalyzerTest {
    @org.junit.Test
    public void TestAnalyzeCodeBlock() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-block.md");
        Assert.assertNotNull(e);
        new CodeBlockAnalyzer().analyze(e);
        System.out.println(e.render());
    }
}
