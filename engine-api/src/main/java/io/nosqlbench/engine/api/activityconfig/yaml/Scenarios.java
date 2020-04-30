package io.nosqlbench.engine.api.activityconfig.yaml;

import io.nosqlbench.engine.api.activityconfig.rawyaml.RawScenarios;

import java.util.List;
import java.util.Map;

public class Scenarios {

    private RawScenarios rawScenarios;

    public Scenarios(RawScenarios rawScenarios) {
        this.rawScenarios = rawScenarios;
    }

    public List<String> getScenarioNames() {
        return rawScenarios.getScenarioNames();
    }

    public Map<String,String> getNamedScenario(String scenarioName) {
        return rawScenarios.getNamedScenario(scenarioName);
    }
}
