package com.seyed.ali.timeentryservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation to mark fields as optional for validation purposes.
 * Fields annotated with {@code @OptionalField} are considered optional,
 * allowing users to omit these fields in their input without triggering validation errors.
 * <p>
 * This annotation should be used in conjunction with a custom validator to enforce the optional behavior.
 * </p>
 *
 * @author [Seyed Ali]
 * @since 0.1
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = OptionalFieldValidator.class)
public @interface OptionalField {

    /**
     * Returns the error message template.
     *
     * @return the error message template
     */
    String message() default "Field is optional";

    /**
     * Returns the groups the constraint belongs to.
     *
     * @return the groups the constraint belongs to
     */
    Class<?>[] groups() default {};

    /**
     * Returns the payload associated with the constraint.
     *
     * @return the payload associated with the constraint
     */
    Class<? extends Payload>[] payload() default {};

}

