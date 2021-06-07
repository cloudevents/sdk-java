package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import io.cloudevents.sql.impl.functions.BaseFunction;
import io.cloudevents.sql.impl.functions.InfallibleOneArgumentFunction;
import io.cloudevents.sql.impl.runtime.EvaluationRuntimeBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CustomFunctionsTest {

    @Test
    void addSimpleFunction() {
        EvaluationRuntime runtime = EvaluationRuntime.builder()
            .addFunction(new InfallibleOneArgumentFunction<>(
                "MY_STRING_PREDICATE",
                String.class,
                s -> s.length() % 2 == 0
            ))
            .build();

        assertThat(
            Parser.parseDefault("MY_STRING_PREDICATE('abc')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(
            Parser.parseDefault("MY_STRING_PREDICATE('abc', 'xyz')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .hasFailure(EvaluationException.ErrorKind.FUNCTION_DISPATCH);
        assertThat(
            Parser.parseDefault("MY_STRING_PR('abc', 'xyz')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .hasFailure(EvaluationException.ErrorKind.FUNCTION_DISPATCH);
    }

    @Test
    void addVariadicFunction() {
        EvaluationRuntime runtime = EvaluationRuntime.builder()
            .addFunction(new VariadicMockFunction("MY_STRING_FN", 2, Type.STRING))
            .build();

        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .hasFailure(EvaluationException.ErrorKind.FUNCTION_DISPATCH);
        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc', 'b')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asInteger()
            .isEqualTo(2);
        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc', 'b', 'c')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asInteger()
            .isEqualTo(3);
        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc', 'b', 'c', 123, 456, 789)")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asInteger()
            .isEqualTo(6);
    }

    @Test
    void addSimpleFunctionAndVariadicFunction() {
        EvaluationRuntime runtime = EvaluationRuntime.builder()
            .addFunction(new InfallibleOneArgumentFunction<>(
                "MY_STRING_FN",
                String.class,
                s -> s.length() % 2 == 0
            ))
            .addFunction(new VariadicMockFunction("MY_STRING_FN", 2, Type.STRING))
            .build();

        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asBoolean()
            .isFalse();
        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc', 'b')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asInteger()
            .isEqualTo(2);
        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc', 'b', 'c')")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asInteger()
            .isEqualTo(3);
        assertThat(
            Parser.parseDefault("MY_STRING_FN('abc', 'b', 'c', 123, 456, 789)")
                .evaluate(runtime, Data.V1_MIN)
        )
            .isNotFailed()
            .asInteger()
            .isEqualTo(6);
    }

    @Test
    void cannotAddVariadicWithFixedArgsLowerThanMaxArgsOverload() {
        EvaluationRuntimeBuilder runtime = EvaluationRuntime.builder()
            .addFunction(new InfallibleOneArgumentFunction<>(
                "MY_STRING_FN",
                String.class,
                s -> s.length() % 2 == 0
            ));

        assertThatThrownBy(() -> runtime.addFunction(
            new VariadicMockFunction("MY_STRING_FN", 0, Type.STRING)
        )).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> runtime.addFunction(
            new VariadicMockFunction("MY_STRING_FN", 1, Type.STRING)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cannotAddTwoVariadicOverloads() {
        EvaluationRuntimeBuilder runtime = EvaluationRuntime.builder()
            .addFunction(new VariadicMockFunction("MY_STRING_FN", 0, Type.STRING));

        assertThatThrownBy(() -> runtime.addFunction(
            new VariadicMockFunction("MY_STRING_FN", 1, Type.STRING)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addSimpleFunctionFails() {
        EvaluationRuntimeBuilder runtime = EvaluationRuntime.builder()
            .addFunction(new InfallibleOneArgumentFunction<>(
                "MY_STRING_FN",
                String.class,
                s -> s.length() % 2 == 0
            ));

        assertThatThrownBy(() -> runtime.addFunction(
            new InfallibleOneArgumentFunction<>(
                "MY_STRING_FN",
                String.class,
                s -> s.length() % 2 == 0
            )
        )).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> runtime.addFunction(
            new InfallibleOneArgumentFunction<>(
                "MY_STRING_FN",
                Integer.class,
                s -> s % 2 == 0
            )
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void customFunctionSpecTest() {
        EvaluationRuntime runtime = EvaluationRuntime.builder()
            .addFunction(new InfallibleOneArgumentFunction<>(
                "MY_STRING_PREDICATE",
                String.class,
                s -> s.length() % 2 == 0
            ))
            .build();

        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("sequence", "12")
            .build();

        assertThat(
            Parser.parseDefault("MY_STRING_PREDICATE(sequence + 10)")
                .evaluate(runtime, event)
        )
            .isNotFailed()
            .asBoolean()
            .isTrue();

    }

    private static class VariadicMockFunction extends BaseFunction {

        private final int fixedArgs;
        private final Type argsType;

        private VariadicMockFunction(String name, int fixedArgs, Type argsType) {
            super(name);
            this.fixedArgs = fixedArgs;
            this.argsType = argsType;
        }

        @Override
        public Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments) {
            return arguments.size();
        }

        @Override
        public Type typeOfParameter(int i) throws IllegalArgumentException {
            return argsType;
        }

        @Override
        public int arity() {
            return fixedArgs;
        }

        @Override
        public boolean isVariadic() {
            return true;
        }
    }

}
