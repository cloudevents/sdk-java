package io.cloudevents.sql;

import com.fasterxml.jackson.annotation.JsonValue;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import io.cloudevents.jackson.JsonFormat;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TCKTestSuite {
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
                    return EvaluationException.ErrorKind.CAST;
                case MATH:
                    return EvaluationException.ErrorKind.MATH;
                case MISSING_FUNCTION:
                    return EvaluationException.ErrorKind.MISSING_FUNCTION;
                case MISSING_ATTRIBUTE:
                    return EvaluationException.ErrorKind.MISSING_ATTRIBUTE;
                case FUNCTION_EVALUATION:
                    return EvaluationException.ErrorKind.FUNCTION_EVALUATION;
            }
            return null;
        }

    }

    public Stream<Map.Entry<String, TestCaseModel>> tckTestCases() {
        ObjectMapper mapper = YAMLMapper.builder().addModule(JsonFormat.getCloudEventJacksonModule()).build();

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
            "sub_expression",
            "subscriptions_api_recreations"
        ).map(fileName -> "/tck/" + fileName + ".yaml");

        return tckFiles
            .map(fileName -> {
                try {
                    Path path = Paths.get(this.getClass().getResource(fileName).toURI());
                    return mapper.readValue(path.toFile(), TestSuiteModel.class);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(fileName, e);
                }

            })
            .filter(Objects::nonNull)
            .flatMap(m -> m.tests.stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(m.name + ": " + tc.name, tc)));
    }

    @TestFactory
    Stream<DynamicTest> evaluateWithoutConstantFolding() {
        Parser parser = Parser.builder().disableConstantFolding().build();

        return DynamicTest.stream(
            tckTestCases(),
            Map.Entry::getKey,
            tcEntry -> evaluate(parser, tcEntry.getValue(), this::assertEqualToTestCaseExpectedValue, this::assertEqualToTestCaseExpectedFailure)
        );
    }

    @TestFactory
    Stream<DynamicTest> evaluate() {
        Parser parser = Parser.getDefault();

        return DynamicTest.stream(
            tckTestCases(),
            Map.Entry::getKey,
            tcEntry -> evaluate(parser, tcEntry.getValue(), this::assertEqualToTestCaseExpectedValue, this::assertEqualToTestCaseExpectedFailureWithConstantFolding)
        );
    }

    @TestFactory
    Stream<DynamicTest> tryEvaluate() {
        Parser parser = Parser.getDefault();

        return DynamicTest.stream(
            tckTestCases(),
            Map.Entry::getKey,
            tcEntry -> tryEvaluate(parser, tcEntry.getValue(), this::assertEqualToTestCaseExpectedValue, this::assertEqualToTestCaseExpectedFailureWithConstantFolding)
        );

    }

    private void evaluate(Parser parser, TestCaseModel testCase, BiConsumer<TestCaseModel, Object> valueAssertion, BiConsumer<TestCaseModel, Throwable> throwableAssertion) {
        try {
            Expression expression = parser.parse(testCase.expression);

            Result result = expression.evaluate(testCase.getTestInputEvent());

            valueAssertion.accept(testCase, result.value());
            throwableAssertion.accept(testCase, result.causes().stream().findFirst().orElse(null));
        } catch (ParseException parseException) {
            throwableAssertion.accept(testCase, parseException);
        }
    }

    private void tryEvaluate(Parser parser, TestCaseModel testCase, BiConsumer<TestCaseModel, Object> valueAssertion, BiConsumer<TestCaseModel, Throwable> throwableAssertion) {
        try {
            Expression expression = parser.parse(testCase.expression);

            Object result = expression.tryEvaluate(testCase.getTestInputEvent());
            valueAssertion.accept(testCase, result);
            throwableAssertion.accept(testCase, null);
        } catch (ParseException | EvaluationException parseException) {
            throwableAssertion.accept(testCase, parseException);
        }
    }

    private void assertEqualToTestCaseExpectedValue(TestCaseModel testCase, Object result) {
        if (testCase.result != null) {
            assertThat(result)
                .isEqualTo(testCase.result);
        }
    }

    private void assertEqualToTestCaseExpectedFailureWithConstantFolding(TestCaseModel testCase, Throwable throwable) {
        if (throwable instanceof ParseException && throwable.getCause() instanceof EvaluationException) {
            assertEqualToTestCaseExpectedFailure(testCase, throwable.getCause());
        } else {
            assertEqualToTestCaseExpectedFailure(testCase, throwable);
        }
    }

    private void assertEqualToTestCaseExpectedFailure(TestCaseModel testCase, Throwable throwable) {
        if (testCase.error == Error.PARSE) {
            assertThat(throwable)
                .isInstanceOf(ParseException.class);
            return;
        }

        if (testCase.error == null) {
            assertThat(throwable)
                .isNull();
        } else {
            assertThat(throwable)
                .isInstanceOf(EvaluationException.class)
                .extracting(t -> ((EvaluationException) t).getKind())
                .isEqualTo(testCase.getEvaluationExceptionErrorKind());
        }
    }
}
