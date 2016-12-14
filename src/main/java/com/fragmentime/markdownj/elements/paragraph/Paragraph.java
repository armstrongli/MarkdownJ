package com.fragmentime.markdownj.elements.paragraph;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/12/14.
 */
public class Paragraph extends Element {

    public Paragraph() {
        super.setType(Element.PARAGRAPH);
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            sb.append(super.render());
        }
        sb.append("</p>");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
