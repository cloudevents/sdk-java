package io.cloudevents.bench.sql;

import io.cloudevents.sql.Parser;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;

public class CompileBenchmark {

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testCompileSmallExpression(Blackhole bh) {
        bh.consume(
            Parser.parseDefault("(a + b + c) = 10 AND TRUE OR CONCAT('1', '2', id) = '123'")
        );
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testCompileSmallTrueConstantExpression(Blackhole bh) {
        bh.consume(
            Parser.parseDefault("(1 + 2 + 3) = 10 AND TRUE OR CONCAT('1', '2', '3') = 123")
        );
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void testCompileSmallFalseConstantExpression(Blackhole bh) {
        bh.consume(
            Parser.parseDefault("(1 + 2 + 3) = 10 AND FALSE OR CONCAT('1', '2', '3') = 124")
        );
    }

}
