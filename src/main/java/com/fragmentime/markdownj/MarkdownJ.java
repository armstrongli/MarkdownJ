package com.fragmentime.markdownj;

import com.fragmentime.markdownj.parser.Parser;

/**
 * Created by Beancan on 2016/10/26.
 */
public class MarkdownJ {
    public static void main(String[] args) throws Throwable {
        table();
    }

    private static void sample() throws Throwable {
        Parser.parse("C:\\Users\\Beancan\\test.md");
    }


    private static void table() throws Throwable {
        Parser.parse("C:\\Users\\Beancan\\IdeaProjects\\markdownj\\src\\test\\resources\\test-table.md");
    }

    private static void text() throws Throwable {
        Parser.parse("C:\\Users\\Beancan\\test-text.md");
    }

    private static void separator() throws Throwable {
        Parser.parse("C:\\Users\\Beancan\\test-separator.md");
    }

    private static void block() throws Throwable {
        Parser.parse("C:\\Users\\Beancan\\test-block.md");
    }

    private static void list() throws Throwable {
        Parser.parse("C:\\Users\\Beancan\\test-list.md");
    }

    private static void header() throws Throwable {
        Parser.parse("C:\\Users\\Beancan\\test-header.md");
    }

}
