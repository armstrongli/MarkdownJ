package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Beancan on 2016/12/4.
 */
public class ResourceLoader {
    static final Element LoadResourceFromClass(Class c, String resource) throws IOException {
        Element e = new Element();
        try (InputStream is = c.getResourceAsStream(resource);
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);) {
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                e.append(tmp);
            }
        }
        return e;
    }
}
