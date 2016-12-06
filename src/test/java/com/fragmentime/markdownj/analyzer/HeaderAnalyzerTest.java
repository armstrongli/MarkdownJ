package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Beancan on 2016/12/6.
 */
public class HeaderAnalyzerTest {
    @Test
    public void HeaderTest() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-header.md");
        Assert.assertNotNull(e);
        new HeaderAnalyzer().analyze(e);
        System.out.println(e.render());
    }
}
