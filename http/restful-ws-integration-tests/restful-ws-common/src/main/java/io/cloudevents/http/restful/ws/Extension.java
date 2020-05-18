package io.cloudevents.http.restful.ws;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import javax.ws.rs.client.WebTarget;
import java.util.function.Supplier;

/**
 * Makes injection of WebTarget parameter to test possible.
 * Jersey extension does that, but some other extensions doesn't.
 * To be able to have common base test we need this.
 */
public class Extension implements ParameterResolver {

    private Supplier<WebTarget> targetSupplier;

    public Extension(Supplier<WebTarget> targetSupplier) {
        this.targetSupplier = targetSupplier;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType.equals(WebTarget.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return targetSupplier.get();
    }
}
