package io.cloudevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.v1.AttributesImpl;
import io.cucumber.datatable.DataTable;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java8.En;
import io.cucumber.java8.Ru;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@ScenarioScoped
public class CloudEventsSteps implements En {

    private static final Map<String, Function<Attributes, Object>> ATTRIBUTE_GETTERS;

    static {
        ATTRIBUTE_GETTERS = new HashMap<>();
        ATTRIBUTE_GETTERS.put("id", Attributes::getId);
        ATTRIBUTE_GETTERS.put("specversion", attributes -> attributes.getSpecVersion().toString());
        ATTRIBUTE_GETTERS.put("type", Attributes::getType);
        ATTRIBUTE_GETTERS.put("source", it -> it.getSource().toString());
        ATTRIBUTE_GETTERS.put("time", it -> it.getTime().map(Object::toString).orElse(null));
        ATTRIBUTE_GETTERS.put("datacontenttype", it -> it.getDataContentType().orElse(null));
    }

    public Optional<CloudEvent> cloudEvent = Optional.empty();

    public CloudEventsSteps() {
        Then("{string} attribute is {string}", (String key, String value) -> {
            if (!ATTRIBUTE_GETTERS.containsKey(key)) {
                throw new IllegalArgumentException(String.format("Unknown attribute : '%s'", key));
            }

            assertThat(cloudEvent).hasValueSatisfying(cloudEvent -> {
                assertThat(ATTRIBUTE_GETTERS.get(key).apply(cloudEvent.getAttributes()))
                    .describedAs(key)
                    .isEqualTo(value);
            });
        });

        Then("the attributes are:", (DataTable dataTable) -> {
            Map<String, String> attributes = dataTable.asMap(String.class, String.class);
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(cloudEvent).hasValueSatisfying(cloudEvent -> {
                    attributes.forEach((key, value) -> {
                        if ("key".equals(key)) {
                            // Skip header
                            return;
                        }

                        softAssertions
                            .assertThat(
                                ATTRIBUTE_GETTERS
                                    .computeIfAbsent(key, __ -> {
                                        throw new IllegalArgumentException("Unknown key: '" + key + "'");
                                    })
                                    .apply(cloudEvent.getAttributes())
                            )
                            .as(key)
                            .isEqualTo(value);
                    });
                });
            });
        });

        Then("the data is equal to the following JSON:", (String expected) -> {
            assertThat(cloudEvent).hasValueSatisfying(cloudEvent -> {
                try {
                    String actual = cloudEvent.getData()
                        .map(String::new)
                        .orElse(null);

                    JSONAssert.assertEquals(expected, actual, true);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
