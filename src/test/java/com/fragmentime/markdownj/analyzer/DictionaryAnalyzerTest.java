package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Beancan on 2016/12/4.
 */
public class DictionaryAnalyzerTest {
    @Test
    public void testDictionary() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-dictionary.md");
        Assert.assertNotNull(e);
        new DictionaryAnalyzer().analyze(e);
        System.out.println(e.render());
    }
}
