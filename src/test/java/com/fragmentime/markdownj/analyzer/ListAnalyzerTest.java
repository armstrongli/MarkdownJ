package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import org.junit.Assert;
import org.junit.Test;

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
}
