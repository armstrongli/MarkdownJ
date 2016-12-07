package com.fragmentime.markdownj.elements.list;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class List extends Element {
    public List() {
        super.setType(Element.LIST);
    }

    public static boolean isList(String content) {
        return content != null && content.trim().matches("^((\\-|\\d+\\.|\\+\\*) +.+)$");
    }

    public static int indexOfList(String content) {
        int index = 0;
        while (content.charAt(index) == ' ') {
            index++;
        }
        return index;
    }

    @Override
    public String render() {
        boolean ol = true;
        if (this.getData().get(0).trim().charAt(0) == '-') {
            ol = false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ol ? "<ol>" : "<ul>").append("\n");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        }
        sb.append(ol ? "</ol>" : "</ul>").append("\n");

        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
