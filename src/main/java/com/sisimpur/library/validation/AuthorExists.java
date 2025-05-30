package com.sisimpur.library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AuthorExistsValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorExists {
    String message() default "Author does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
