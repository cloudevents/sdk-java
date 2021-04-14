package io.cloudevents.sql;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.sql.impl.ParserImpl;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class TCKTestSuite {

    public static class TestSuiteModel {
        public String name;
        public List<TestCaseModel> tests;
    }

    public enum Error {
        PARSE("parse"),
        MATH("math"),
        CAST("cast"),
        MISSING_ATTRIBUTE("missingAttribute"),
        MISSING_FUNCTION("missingFunction"),
        FUNCTION_EVALUATION("functionEvaluation");

        private final String name;

        Error(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return this.name;
        }
    }

    public static class TestCaseModel {
        public String name;
        public String expression;
        public Object result;
        public CloudEvent event;
        public Map<String, Object> eventOverrides;
        public Error error;
    }

    @TestFactory
    Stream<DynamicTest> tckTests() {
        Parser parser = new ParserImpl();

        ObjectMapper mapper = new YAMLMapper();
        mapper.registerModule(JsonFormat.getCloudEventJacksonModule());

        // Files to load
        Stream<String> tckFiles = Stream.of(
            "binary_math_operators",
            "binary_logical_operators",
            "binary_comparison_operators",
            "case_sensitivity",
            "casting_functions",
            "context_attributes_access",
            "exists_expression",
            "in_expression",
            "integer_builtin_functions",
            "like_expression",
            "literals",
            "negate_operator",
            "not_operator",
            "parse_errors",
            "spec_examples",
            "string_builtin_functions",
            "sub_expression"
        ).map(fileName -> "/tck/" + fileName + ".yaml");

        return DynamicTest.stream(
            tckFiles
                .map(fileName -> {
                    try {
                        return mapper.readValue(this.getClass().getResource(fileName), TestSuiteModel.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(m -> m.tests.stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(m.name + ": " + tc.name, tc))),
            Map.Entry::getKey,
            tcEntry -> executeTestCase(parser, tcEntry.getValue())
        );

    }

    public void executeTestCase(Parser parser, TestCaseModel testCase) throws Exception {
        // Assert parse errors
        if (testCase.error == Error.PARSE) {
            assertThatCode(() -> parser.parse(testCase.expression))
                .isInstanceOf(ParseException.class);
            return;
        }

        CloudEvent inputEvent = (testCase.event == null) ? Data.V1_MIN : testCase.event;
        if (testCase.eventOverrides != null) {
            CloudEventBuilder builder = CloudEventBuilder.from(inputEvent);
            testCase.eventOverrides.forEach((k, v) -> {
                if (v instanceof String) {
                    builder.withContextAttribute(k, (String) v);
                } else if (v instanceof Boolean) {
                    builder.withContextAttribute(k, (Boolean) v);
                } else if (v instanceof Number) {
                    builder.withContextAttribute(k, ((Number) v).intValue());
                } else {
                    throw new IllegalArgumentException("Unexpected event override attribute '" + k + "' type: " + v.getClass());
                }
            });
            inputEvent = builder.build();
        }

        Expression expression = parser.parse(testCase.expression);
        assertThat(expression).isNotNull();

        Result result = expression.evaluate(inputEvent);

        if (testCase.result != null) {
            assertThat(result)
                .value()
                .isEqualTo(testCase.result);
        }

        if (testCase.error == null) {
            assertThat(result)
                .isNotFailed();
            return;
        }
        switch (testCase.error) {
            case CAST:
                assertThat(result).hasFailure(EvaluationException.ErrorKind.INVALID_CAST);
                break;
            case MATH:
                assertThat(result).hasFailure(EvaluationException.ErrorKind.DIVISION_BY_ZERO);
                break;
            case MISSING_FUNCTION:
                assertThat(result).hasFailure(EvaluationException.ErrorKind.FUNCTION_DISPATCH);
                break;
            case MISSING_ATTRIBUTE:
                assertThat(result).hasFailure(EvaluationException.ErrorKind.MISSING_ATTRIBUTE);
                break;
            case FUNCTION_EVALUATION:
                assertThat(result).hasFailure(EvaluationException.ErrorKind.FUNCTION_EXECUTION);
                break;
        }

    }

}
