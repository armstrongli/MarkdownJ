package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.analyzer.TextAnalyser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Beancan on 2016/11/2.
 */
public class TextAnalyzerText {

    @Test
    public void TextTest() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-text.md");
        Assert.assertNotNull(e);
        new TextAnalyser().analyze(e);
        System.out.println(e.render());
    }

}
