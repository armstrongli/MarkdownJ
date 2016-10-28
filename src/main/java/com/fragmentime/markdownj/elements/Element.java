package com.fragmentime.markdownj.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Element {

    public static final String HEADER = "header";
    public static final String BLOCK = "block";
    public static final String LIST = "list";
    public static final String SEPARATOR = "separator";
    public static final String TABLE = "table";
    public static final String TEXT = "text";
    public static final String LI = "li";

    private List<String> data = new ArrayList<String>();
    private Element parent;
    private Element left;
    private Element right;

    public Element append(String item) {
        this.data.add(item);
        return this;
    }

    public boolean hasRight() {
        return this.right != null;
    }

    public boolean hasLeft() {
        return this.left != null;
    }

    public List<String> getData() {
        return this.data;
    }

    public Element getLeft() {
        return left;
    }

    public void setLeft(Element left) {
        this.left = left;
    }

    public Element getRight() {
        return right;
    }

    public void setRight(Element right) {
        this.right = right;
    }

    private String type;

    public Element getParent() {
        return parent;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public String getType() {
        return this.type;
    }

}
