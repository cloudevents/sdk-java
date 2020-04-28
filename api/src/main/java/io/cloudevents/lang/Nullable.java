package io.cloudevents.lang;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.annotation.meta.When.MAYBE;

@Target(value = {METHOD, PARAMETER, FIELD})
@Retention(value = RUNTIME)
@Documented
@Nonnull(when = MAYBE)
@TypeQualifierNickname
public @interface Nullable {
}
