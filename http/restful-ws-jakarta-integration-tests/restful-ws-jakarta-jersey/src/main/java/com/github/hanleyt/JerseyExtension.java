/*
 * Ported from https://github.com/hanleyt/jersey-junit as no version support Jesery versions >=3.0.0
 */

package com.github.hanleyt;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class JerseyExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final Collection<Class<?>> INJECTABLE_PARAMETER_TYPES = Arrays.asList(Client.class, WebTarget.class, URI.class);

    private final Function<ExtensionContext, TestContainerFactory> testContainerFactoryProvider;
    private final Function<ExtensionContext, DeploymentContext> deploymentContextProvider;
    private final BiFunction<ExtensionContext, ClientConfig, ClientConfig> configProvider;

    private JerseyExtension() {
        throw new IllegalStateException("JerseyExtension must be registered programmatically");
    }

    public JerseyExtension(Supplier<Application> applicationSupplier) {
        this((unused) -> applicationSupplier.get(), null);
    }

    public JerseyExtension(Supplier<Application> applicationSupplier,
                           BiFunction<ExtensionContext, ClientConfig, ClientConfig> configProvider) {
        this((unused) -> applicationSupplier.get(), configProvider);
    }

    public JerseyExtension(Function<ExtensionContext, Application> applicationProvider) {
        this(applicationProvider, null);
    }

    public JerseyExtension(Function<ExtensionContext, Application> applicationProvider,
                           BiFunction<ExtensionContext, ClientConfig, ClientConfig> configProvider) {
        this(null, (context) -> DeploymentContext.builder(applicationProvider.apply(context)).build(), configProvider);
    }

    public JerseyExtension(Function<ExtensionContext, TestContainerFactory> testContainerFactoryProvider,
                           Function<ExtensionContext, DeploymentContext> deploymentContextProvider,
                           BiFunction<ExtensionContext, ClientConfig, ClientConfig> configProvider) {
        this.testContainerFactoryProvider = testContainerFactoryProvider;
        this.deploymentContextProvider = deploymentContextProvider;
        this.configProvider = configProvider;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        JerseyTest jerseyTest = initJerseyTest(context);
        getStore(context).put(Client.class, jerseyTest.client());
        getStore(context).put(WebTarget.class, jerseyTest.target());
        getStore(context).put(URI.class, jerseyTest.target().getUri());
    }

    private JerseyTest initJerseyTest(ExtensionContext context) throws Exception {
        JerseyTest jerseyTest = new JerseyTest() {

            @Override
            protected DeploymentContext configureDeployment() {
                forceSet(TestProperties.CONTAINER_PORT, "0");
                return deploymentContextProvider.apply(context);
            }

            @Override
            protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
                if (testContainerFactoryProvider != null) {
                    return testContainerFactoryProvider.apply(context);
                }
                return super.getTestContainerFactory();
            }

            @Override
            protected void configureClient(ClientConfig config) {
                if (configProvider != null) {
                    config = configProvider.apply(context, config);
                }
                super.configureClient(config);
            }
        };
        jerseyTest.setUp();
        getStore(context).put(JerseyTest.class, jerseyTest);
        return jerseyTest;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = getStore(context);
        store.remove(JerseyTest.class, JerseyTest.class).tearDown();
        INJECTABLE_PARAMETER_TYPES.forEach(store::remove);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return INJECTABLE_PARAMETER_TYPES.contains(parameterType);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return getStore(extensionContext).get(parameterType, parameterType);
    }

    public static ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.GLOBAL);
    }

}
