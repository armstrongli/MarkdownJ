package com.fragmentime.markdownj.elements.table;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/11/13.
 */
public class TableHead extends Element {
    public TableHead() {
        setType(Element.TABLE_HEAD);
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append(this.getRight().render());
        sb.append("</tr>");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
