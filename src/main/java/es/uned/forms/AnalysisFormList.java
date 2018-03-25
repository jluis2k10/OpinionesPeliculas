package es.uned.forms;

import org.apache.commons.collections.FactoryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AnalysisFormList {

    private boolean execute;

    private List analysis = ShrinkableList.decorate(new ArrayList(), FactoryUtils.instantiateFactory(AnalysisForm.class));

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public List getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List analysis) {
        this.analysis = analysis;
    }
}
