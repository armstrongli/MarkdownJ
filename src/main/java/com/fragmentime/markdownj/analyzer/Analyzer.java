package com.fragmentime.markdownj.analyzer;

import com.fragmentime.markdownj.elements.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Beancan on 2016/11/20.
 */
public abstract class Analyzer implements Comparable<Analyzer> {
    public static final int ANALYZER_CODE_BLOCK = 5000;
    public static final int ANALYZER_DICT = 4000;
    public static final int ANALYZER_HEADER = 3000;
    public static final int ANALYZER_LIST = 2500;
    public static final int ANALYZER_LI = 2499;
    public static final int ANALYZER_SEPARATOR = 2000;
    public static final int ANALYZER_TABLE = 1000;
    public static final int ANALYZER_TEXT = 0;

    /**
     * check whether the element belongs to this analyzer
     *
     * @param element
     * @return
     */
    public abstract boolean belongsToAnalyzer(Element element);

    /**
     * used to set the priority of analyzer
     *
     * @return
     */
    protected abstract int getWeight();

    /**
     * @param root
     */
    public abstract boolean analyze(Element root);

    public final Analyzer addSubAnalyzers(Analyzer... analyzers) {
        this.analyzers.addAll(Arrays.asList(analyzers));
        return this;
    }

    public final List<Analyzer> getSubAnalyzers() {
        Collections.sort(this.analyzers);
        return this.analyzers;
    }

    private List<Analyzer> analyzers = new ArrayList<Analyzer>();

    public final int compareTo(Analyzer o) {
        return getWeight() - o.getWeight();
    }
}
