package com.fragmentime.markdownj.elements.table;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class Table extends Element {

    public Table() {
        super.setType(Element.TABLE);
    }

    public static boolean isTable(String content, String content2) {
        return content != null && content.trim().startsWith("|")
                &&
                content2 != null && content2.trim().replace('|', ' ').replace('-', ' ').replace(':', ' ').trim().length() == 0;
    }

    public static boolean isTableItem(String content) {
        return content != null && content.trim().startsWith("|");
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\" cellPadding=\"0\" cellSpacing=\"0\">").append("\n");
        sb.append(this.getRight().render());
        sb.append("</table>").append("\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
