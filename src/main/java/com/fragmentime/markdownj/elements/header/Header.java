package com.fragmentime.markdownj.elements.header;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.exceptions.ElementNotMatchException;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Header extends Element {

    public Header() {
        super.setType(Element.HEADER);
    }

    private int headerSize = 0;

    public static boolean isHeader(String content) {
        return content != null && content.length() > 0 && content.startsWith("#");
    }

    @Override
    public Element append(String item) {
        if (item.startsWith("#")) {
            initHeaderSize(item, "");
            super.append(item.substring(this.headerSize));
        } else {
            initHeaderSize("", item);
            if (this.headerSize == 0) {
                super.append(item);
            }
        }
        return this;
    }

    private int initHeaderSize(String item, String itemPlus) {
        if (this.headerSize > 0) {
            return this.headerSize;
        }
        char[] chars = item.toCharArray();

        if (item.startsWith("#")) {
            int headerSize = 0;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '#') {
                    headerSize++;
                } else {
                    break;
                }
            }
            this.headerSize = headerSize;
        } else {
            if (itemPlus.trim().startsWith("=")) {
                this.headerSize = 1;
            } else if (itemPlus.trim().startsWith("-")) {
                this.headerSize = 2;
            }
        }
        return this.headerSize;
    }

    @Override
    public String render() {
        StringBuffer sb = new StringBuffer();
        sb.append("<h").append(headerSize).append(">");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            sb.append(this.getData().get(0).trim().substring(headerSize).trim());
        }
        sb.append("</h").append(headerSize).append(">").append("\n").append("<hr/>\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
