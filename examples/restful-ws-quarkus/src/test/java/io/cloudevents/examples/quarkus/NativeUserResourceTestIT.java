package io.cloudevents.examples.quarkus;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeUserResourceTestIT extends UserResourceTest {

    // Execute the same tests but in native mode.
}
