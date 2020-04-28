package io.cloudevents;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {
        "pretty",
    },
    features = {
        "file:../conformance/features/"
    }
)
public class RunCucumberTests {
}
