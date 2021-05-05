package com.example.simplecrud.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = com.example.simplecrud.common.validation.UserDuplicateKeyValidator.class)
public @interface UserDuplicateKey {

    String message() default "{user.duplicateKey}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
