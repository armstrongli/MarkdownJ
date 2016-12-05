package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.elements.block.Block;

import java.util.Iterator;

/**
 * Created by Beancan on 2016/12/3.
 */
public class CodeBlockAnalyzer extends Analyzer {
    public boolean belongsToAnalyzer(Element element) {
        return element != null && Element.BLOCK.equals(element.getType());
    }

    protected int getWeight() {
        return ANALYZER_CODE_BLOCK;
    }

    public void analyze(Element root) {
        if (root == null || root.getData().size() == 0) {
            return;
        }
        boolean isCodeBlock = false;
        Iterator<String> it = root.getData().iterator();
        Element current = null;
        while (it.hasNext()) {
            String tmp = it.next();
            if (current == null) {
                if (Block.isBlock(tmp)) {
                    current = new Block();
                    isCodeBlock = true;
                } else {
                    current = new Element();
                    isCodeBlock = false;
                }
                current.append(tmp);

                current.setParent(root);
                root.setRight(current);
            } else {
                if (isCodeBlock) {
                    if (tmp.trim().equals("```")) {
                        current.append(tmp);
                        isCodeBlock = false;
                    } else {
                        current.append(tmp);
                    }
                } else {
                    if (Block.isBlock(tmp)) {
                        Element e = new Block();
                        e.append(tmp);

                        current.setLeft(e);
                        e.setParent(current);
                        current = e;
                        isCodeBlock = true;
                    } else {
                        if (belongsToAnalyzer(current)) {
                            Element e = new Element();
                            e.append(tmp);

                            current.setLeft(e);
                            e.setParent(current);
                            current = e;
                            isCodeBlock = false;
                        } else {
                            current.append(tmp);
                        }
                    }
                }
            }
        }
    }
}
