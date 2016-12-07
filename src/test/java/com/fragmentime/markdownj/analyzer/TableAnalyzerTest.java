package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Beancan on 2016/12/7.
 */
public class TableAnalyzerTest {

    @Test
    public void TableTest() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-table.md");
        Assert.assertNotNull(e);
        new TableAnalyzer().analyze(e);
        System.out.println(e.render());
    }
}
