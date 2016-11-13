package com.fragmentime.markdownj.elements.table;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/11/13.
 */
public class TableCell extends Element {
    public TableCell() {
        setType(Element.TABLE_CELL);
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<td>");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            sb.append(this.getData().get(0));
        }
        sb.append("</td>");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
