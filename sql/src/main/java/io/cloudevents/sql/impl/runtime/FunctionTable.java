package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.Function;
import io.cloudevents.sql.impl.functions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FunctionTable {

    private static class SingletonContainer {
        private final static FunctionTable INSTANCE = new FunctionTable(
            Stream.of(
                new AbsFunction(),
                new IntFunction(),
                new BoolFunction(),
                new StringFunction(),
                new IsBoolFunction(),
                new IsIntFunction(),
                new InfallibleOneArgumentFunction<>("LENGTH", String.class, Integer.class, String::length),
                new ConcatFunction(),
                new ConcatWSFunction(),
                new InfallibleOneArgumentFunction<>("LOWER", String.class, String.class, String::toLowerCase),
                new InfallibleOneArgumentFunction<>("UPPER", String.class, String.class, String::toUpperCase),
                new InfallibleOneArgumentFunction<>("TRIM", String.class, String.class, String::trim),
                new LeftFunction(),
                new RightFunction(),
                new SubstringFunction(),
                new SubstringWithLengthFunction()
            )
        );
    }

    /**
     * @return instance of {@link FunctionTable}
     */
    public static FunctionTable getDefaultInstance() {
        return SingletonContainer.INSTANCE;
    }

    private final Map<String, Functions> functions;

    private FunctionTable(Stream<Function> functions) {
        this.functions = new HashMap<>();
        functions.forEach(this::addFunction);
    }

    protected FunctionTable(FunctionTable functionTable) {
        this(functionTable.getFunctions());
    }

    protected Function resolve(String name, int args) throws IllegalStateException {
        Functions fns = functions.get(name);
        if (fns == null) {
            throw new IllegalStateException(
                "No function named '" + name + "' found. Available function names: " + functions.keySet()
            );
        }

        return fns.resolve(args);
    }

    protected void addFunction(Function function) throws IllegalArgumentException {
        Functions fns = this.functions.computeIfAbsent(function.name(), v -> new Functions());
        fns.addFunction(function);
    }

    private Stream<Function> getFunctions() {
        return functions.values()
            .stream()
            .flatMap(Functions::getFunctions);
    }

    private static class Functions {
        private final Map<Integer, Function> fixedArgsNumberFunctions;
        private Function variadicFunction;

        private Functions() {
            this.fixedArgsNumberFunctions = new HashMap<>();
        }

        public void addFunction(Function function) {
            if (function.isVariadic()) {
                if (
                    fixedArgsNumberFunctions
                        .keySet()
                        .stream()
                        .max(Integer::compareTo)
                        .map(maxArity -> maxArity >= function.arity())
                        .orElse(false)
                ) {
                    throw new IllegalArgumentException(
                        "You're trying to add a variadic function, but one function with the same name and arity greater or equal is already defined: " + function.name()
                    );
                }
                if (this.variadicFunction != null) {
                    throw new IllegalArgumentException("You're trying to add a variadic function, but one is already defined for this function name: " + function.name());
                }
                this.variadicFunction = function;
            } else {
                Function old = this.fixedArgsNumberFunctions.put(function.arity(), function);
                if (old != null) {
                    throw new IllegalArgumentException("You're trying to add a function, but one with the same arity is already defined: " + function.name() + " with arity " + function.arity());
                }
            }
        }

        public Function resolve(int args) {
            Function fn = fixedArgsNumberFunctions.get(args);
            if (fn != null) {
                return fn;
            }
            // Let's try with the variadic functions
            if (variadicFunction == null) {
                // This shouldn't really happen, since this object should not exist in that case
                throw createMissingFunctionException(args);
            }
            if (variadicFunction.arity() > args) {
                throw createMissingFunctionException(args);
            }
            return variadicFunction;
        }

        private RuntimeException createMissingFunctionException(int args) {
            return new IllegalStateException(
                "No functions with arity " + args + " found. Available functions: " +
                    fixedArgsNumberFunctions.values() + ((variadicFunction != null) ? " and variadic " + variadicFunction : "")
            );
        }

        private Stream<Function> getFunctions() {
            if (variadicFunction == null) {
                return fixedArgsNumberFunctions.values().stream();
            }
            return Stream.concat(fixedArgsNumberFunctions.values().stream(), Stream.of(variadicFunction));
        }

    }

}
