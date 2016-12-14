package com.fragmentime.markdownj.parser;

import com.fragmentime.markdownj.analyzer.ResourceLoader;
import com.fragmentime.markdownj.elements.Element;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Beancan on 2016/12/9.
 */
public class ParserTest {
    @Test
    public void TestFile() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-parser.md");
        Assert.assertNotNull(e);
        String result = Parser.parseStrings(e.getData());
        System.out.println(result);
    }

    @Test
    public void TestFile1() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-parser1.md");
        Assert.assertNotNull(e);
        String result = Parser.parseStrings(e.getData());
        System.out.println(result);
    }

    @Test
    public void TestText() throws Exception {
        Element e = ResourceLoader.LoadResourceFromClass(this.getClass(), "/test-text.md");
        Assert.assertNotNull(e);
        String result = Parser.parseStrings(e.getData());
        System.out.println(result);
    }
}
