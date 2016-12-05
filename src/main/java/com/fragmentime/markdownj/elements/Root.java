package com.fragmentime.markdownj.elements;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Root extends Element {

    private final String sourceType;

    public Root(String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public String getSourceType() {
        return this.sourceType;
    }
}
