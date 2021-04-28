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

        public CloudEvent getTestInputEvent() {
            CloudEvent inputEvent = (this.event == null) ? Data.V1_MIN : this.event;
            if (this.eventOverrides != null) {
                CloudEventBuilder builder = CloudEventBuilder.from(inputEvent);
                this.eventOverrides.forEach((k, v) -> {
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
            return inputEvent;
        }

        public EvaluationException.ErrorKind getEvaluationExceptionErrorKind() {
            switch (this.error) {
                case CAST:
                    return EvaluationException.ErrorKind.INVALID_CAST;
                case MATH:
                    return EvaluationException.ErrorKind.MATH;
                case MISSING_FUNCTION:
                    return EvaluationException.ErrorKind.FUNCTION_DISPATCH;
                case MISSING_ATTRIBUTE:
                    return EvaluationException.ErrorKind.MISSING_ATTRIBUTE;
                case FUNCTION_EVALUATION:
                    return EvaluationException.ErrorKind.FUNCTION_EXECUTION;
            }
            return null;
        }

    }

    public Stream<Map.Entry<String, TestCaseModel>> tckTestCases() {
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

        return tckFiles
            .map(fileName -> {
                try {
                    return mapper.readValue(this.getClass().getResource(fileName), TestSuiteModel.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .flatMap(m -> m.tests.stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(m.name + ": " + tc.name, tc)));
    }

    @TestFactory
    Stream<DynamicTest> evaluate() {
        Parser parser = new ParserImpl();

        return DynamicTest.stream(
            tckTestCases(),
            Map.Entry::getKey,
            tcEntry -> evaluateTestCase(parser, tcEntry.getValue())
        );
    }

    @TestFactory
    Stream<DynamicTest> tryEvaluate() {
        Parser parser = new ParserImpl();

        return DynamicTest.stream(
            tckTestCases(),
            Map.Entry::getKey,
            tcEntry -> tryEvaluateTestCase(parser, tcEntry.getValue())
        );

    }

    public void evaluateTestCase(Parser parser, TestCaseModel testCase) throws Exception {
        // Assert parse errors
        if (testCase.error == Error.PARSE) {
            assertThatCode(() -> parser.parse(testCase.expression))
                .isInstanceOf(ParseException.class);
            return;
        }

        Expression expression = parser.parse(testCase.expression);
        assertThat(expression).isNotNull();

        Result result = expression.evaluate(testCase.getTestInputEvent());

        if (testCase.result != null) {
            assertThat(result)
                .value()
                .isEqualTo(testCase.result);
        }

        if (testCase.error == null) {
            assertThat(result)
                .isNotFailed();
        } else {
            assertThat(result)
                .hasFailure(testCase.getEvaluationExceptionErrorKind());
        }
    }

    public void tryEvaluateTestCase(Parser parser, TestCaseModel testCase) throws Exception {
        // Assert parse errors
        if (testCase.error == Error.PARSE) {
            assertThatCode(() -> parser.parse(testCase.expression))
                .isInstanceOf(ParseException.class);
            return;
        }

        Expression expression = parser.parse(testCase.expression);
        assertThat(expression).isNotNull();

        if (testCase.error != null) {
            assertThatCode(() -> expression.tryEvaluate(testCase.getTestInputEvent()))
                .isInstanceOf(EvaluationException.class)
                .extracting(t -> ((EvaluationException) t).getKind())
                .isEqualTo(testCase.getEvaluationExceptionErrorKind());
        } else {
            assertThat(
                expression.tryEvaluate(testCase.getTestInputEvent())
            ).isEqualTo(testCase.result);
        }
    }

}
