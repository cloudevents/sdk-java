package io.cloudevents.bench.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Parser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static io.cloudevents.core.test.Data.V1_WITH_JSON_DATA_WITH_EXT;

public class RunBenchmark {

    @State(Scope.Thread)
    public static class TestCaseSmallExpression {
        public CloudEvent event = CloudEventBuilder
            .v1(V1_WITH_JSON_DATA_WITH_EXT)
            .withExtension("a", "10")
            .withExtension("b", 3)
            .withExtension("c", "-3")
            .build();
        public Expression expression = Parser
            .parseDefault("(a + b + c) = 10 AND TRUE OR CONCAT('1', '2', id) = '123'");
    }

    @State(Scope.Thread)
    public static class TestCaseSmallTrueConstantExpression {
        public CloudEvent event = CloudEventBuilder
            .v1(V1_WITH_JSON_DATA_WITH_EXT)
            .build();
        public Expression expression = Parser
            .parseDefault("(1 + 2 + 3) = 10 AND TRUE OR CONCAT('1', '2', '3') = 123");
    }

    @State(Scope.Thread)
    public static class TestCaseSmallFalseConstantExpression {
        public CloudEvent event = CloudEventBuilder
            .v1(V1_WITH_JSON_DATA_WITH_EXT)
            .build();
        public Expression expression = Parser
            .parseDefault("(1 + 2 + 3) = 10 AND FALSE OR CONCAT('1', '2', '3') = 124");
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testEvaluateSmallExpression(TestCaseSmallExpression testCase, Blackhole bh) {
        bh.consume(
            testCase.expression.evaluate(testCase.event)
        );
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testTryEvaluateSmallExpression(TestCaseSmallExpression testCase, Blackhole bh) {
        bh.consume(
            testCase.expression.tryEvaluate(testCase.event)
        );
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testEvaluateSmallTrueConstantExpression(TestCaseSmallTrueConstantExpression testCase, Blackhole bh) {
        bh.consume(
            testCase.expression.evaluate(testCase.event)
        );
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testTryEvaluateSmallTrueConstantExpression(TestCaseSmallTrueConstantExpression testCase, Blackhole bh) {
        bh.consume(
            testCase.expression.tryEvaluate(testCase.event)
        );
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testEvaluateSmallFalseConstantExpression(TestCaseSmallFalseConstantExpression testCase, Blackhole bh) {
        bh.consume(
            testCase.expression.evaluate(testCase.event)
        );
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testTryEvaluateSmallFalseConstantExpression(TestCaseSmallFalseConstantExpression testCase, Blackhole bh) {
        bh.consume(
            testCase.expression.tryEvaluate(testCase.event)
        );
    }

}
