package com.seyed.ali.timeentryservice.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom validator for the {@link OptionalField} annotation.
 * <p>
 * This validator checks whether a field annotated with {@code @OptionalField} is optional or not.
 * The field is considered optional if its value is null or an empty string ("").
 * </p>
 *
 * @author [Seyed Ali]
 * @since 0.1
 */
public class OptionalFieldValidator implements ConstraintValidator<OptionalField, Object> {

    /**
     * Checks whether the given value is valid.
     * The value is considered valid if it is null or an empty string ("").
     *
     * @param value   The value to validate
     * @param context The constraint validator context
     * @return {@code true}, indicating that the field is optional and considered valid.
     * Otherwise, it returns {@code false}, indicating that the field has a value and should be validated according to other constraints
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value == null || (value instanceof String && ((String) value).isEmpty());
    }

}

