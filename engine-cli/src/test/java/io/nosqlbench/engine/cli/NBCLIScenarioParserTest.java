package io.nosqlbench.engine.cli;

import io.nosqlbench.nb.api.errors.BasicError;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NBCLIScenarioParserTest {

    @Test
    public void providePathForScenario() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "local/example-scenarios" });
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
    }

    @Test
    public void defaultScenario() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test" });
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
    }

    @Test
    public void defaultScenarioWithParams() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "cycles=100"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
        assertThat(cmds.get(0).getCmdSpec()).containsOnlyOnce("cycles=100");
        assertThat(cmds.get(0).getCmdSpec()).containsOnlyOnce("cycles=");
    }

    @Test
    public void namedScenario() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "schema-only"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
    }

    @Test
    public void namedScenarioWithParams() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "schema-only", "cycles=100"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
        assertThat(cmds.get(0).getCmdSpec()).containsOnlyOnce("cycles=100");
        assertThat(cmds.get(0).getCmdSpec()).containsOnlyOnce("cycles=");
    }

    @Test
    public void testThatSilentFinalParametersPersist() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "type=foo"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
        assertThat(cmds.get(0).getCmdSpec()).containsOnlyOnce("driver=stdout");
        assertThat(cmds.get(1).getCmdSpec()).containsOnlyOnce("driver=foo");
    }

    @Test(expected = BasicError.class)
    public void testThatVerboseFinalParameterThrowsError() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "yaml=canttouchthis"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
    }

    @Test(expected = BasicError.class)
    public void testThatMissingScenarioNameThrowsError() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "missing-scenario"});
    }

    @Test
    public void testThatMultipleScenariosConcatenate() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "default", "default"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
        assertThat(cmds.size()).isEqualTo(6);
    }

    @Test
    public void testThatTemplatesAreExpandedDefault() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "template-test"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
        assertThat(cmds.size()).isEqualTo(1);
        assertThat(cmds.get(0).getCmdSpec()).isEqualTo("driver=stdout;cycles=10;workload=activities/scenario-test.yaml;");
    }
    @Test
    public void testThatTemplatesAreExpandedOverride() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "template-test", "cycles-test=20"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
        assertThat(cmds.size()).isEqualTo(1);
        assertThat(cmds.get(0).getCmdSpec()).isEqualTo("driver=stdout;cycles=20;cycles-test=20;workload=activities/scenario-test.yaml;");
    }

    @Test
    public void testThatUndefValuesAreUndefined() {
        NBCLIOptions opts = new NBCLIOptions(new String[]{ "scenario-test", "schema-only", "cycles-test=20"});
        List<NBCLIOptions.Cmd> cmds = opts.getCommands();
        assertThat(cmds.size()).isEqualTo(1);
        assertThat(cmds.get(0).getCmdSpec()).isEqualTo("driver=stdout;workload=scenario-test;tags=phase:schema;cycles-test=20;");
        NBCLIOptions opts1 = new NBCLIOptions(new String[]{ "scenario-test", "schema-only", "doundef=20"});
        List<NBCLIOptions.Cmd> cmds1 = opts1.getCommands();
        assertThat(cmds1.size()).isEqualTo(1);
        assertThat(cmds1.get(0).getCmdSpec()).isEqualTo("driver=stdout;workload=scenario-test;tags=phase:schema;");

    }


}
