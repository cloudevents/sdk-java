/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.format;

import java.time.format.DateTimeFormatter;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author dturanski
 */
public class DateTimeValidator implements ConstraintValidator<DateTimeFormat, String> {
    private String pattern;

    @Override
    public void initialize(DateTimeFormat constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if ( object == null ) {
            return true;
        }

        try {
            if (pattern != null && pattern.length() > 0) {
                DateTimeFormatter.ofPattern(pattern).parse(object);
            } else {
                DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(object);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
