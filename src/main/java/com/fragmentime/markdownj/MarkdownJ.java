package com.fragmentime.markdownj;

import com.fragmentime.markdownj.parser.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Beancan on 2016/10/26.
 */
public class MarkdownJ {
    public static void main(String[] args) throws Exception {
//        Parser.parse("C:\\Users\\Beancan\\test.md");
        Parser.parse("C:\\Users\\Beancan\\test-list.md");
    }
}
