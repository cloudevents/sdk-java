package io.cloudevents.sql.asserts;

import io.cloudevents.sql.Result;

public class MyAssertions {

    public static ResultAssert assertThat(Result result) {
        return new ResultAssert(result);
    }

}
