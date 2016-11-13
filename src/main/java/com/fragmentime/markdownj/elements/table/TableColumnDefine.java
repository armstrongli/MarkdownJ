package com.fragmentime.markdownj.elements.table;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/11/13.
 */
public class TableColumnDefine extends Element {
    @Override
    public String render() {
        return this.getLeft().render();
    }
}
