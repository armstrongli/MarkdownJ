package com.fragmentime.markdownj.elements;

import java.util.ArrayList;
import java.util.Iterator;
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
    public static final String TABLE_HEAD = "head";
    public static final String TABLE_ROW = "row";
    public static final String TABLE_CELL = "cell";
    public static final String DICTIONARY = "dictionary";

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

    protected void setData(List<String> data) {
        this.data = data;
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


    public void setType(String type) {
        this.type = type;
    }

    public String render() {
        StringBuffer sb = new StringBuffer();
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            Iterator<String> items = getData().iterator();
            while (items.hasNext()) {
                sb.append(items.next());
                if (items.hasNext()) {
                    sb.append(" ");
                }
            }
        }
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }

    public String getSourceType() {
        return parent.getSourceType();
    }

}
