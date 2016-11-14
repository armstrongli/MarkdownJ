package com.fragmentime.markdownj;

import com.fragmentime.markdownj.parser.Parser;

import java.io.File;

/**
 * Created by Beancan on 2016/10/26.
 */
public class MarkdownJ {
    public static String parseFile(String fileName) throws Exception {
        return Parser.parseFile(fileName);
    }

    public static String parseFile(File file) throws Exception {
        return Parser.parseFile(file);
    }

    public static String parseString(String content) throws Exception {
        return Parser.parseString(content);
    }
}
