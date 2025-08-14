package com.uams.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for Country field
 * Implements requirement from EPMCDMETST-14148
 */
@Documented
@Constraint(validatedBy = CountryValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCountry {
    
    String message() default "Invalid country name. Please select a valid country.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}