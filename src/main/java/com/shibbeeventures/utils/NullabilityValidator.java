package com.shibbeeventures.utils;

import com.shibbeeventures.annotations.NotNull;
import com.shibbeeventures.annotations.NonNull;
import com.shibbeeventures.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Utility class to validate method parameters at runtime based on
 * {@link NotNull}, {@link NonNull}, and {@link Nullable} annotations.
 */
public class NullabilityValidator {

    /**
     * Validates method arguments based on {@link NotNull}, {@link NonNull}, and
     * {@link Nullable} annotations.
     * If a parameter is annotated with {@link NotNull} or {@link NonNull} and is
     * null, an exception is thrown.
     * If a parameter is explicitly marked {@link Nullable} but receives a non-null
     * value, no exception is thrown.
     *
     * @param method The method being called.
     * @param args   The arguments passed to the method.
     * @throws IllegalArgumentException if a non-null parameter receives a null
     *                                  value.
     */
    public static void validateMethodParameters(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            boolean isNonNull = isAnnotatedNonNull(parameters[i]);
            boolean isNullable = isAnnotatedNullable(parameters[i]);

            if (args[i] == null) {
                if (isNonNull) {
                    throw new IllegalArgumentException("Parameter '" + parameters[i].getName() +
                            "' in method '" + method.getName() + "' must not be null.");
                }
            } else {
                if (isNullable) {
                    System.out.println("Warning: Parameter '" + parameters[i].getName() +
                            "' in method '" + method.getName() + "' is marked @Nullable but has a value.");
                }
            }
        }
    }

    /**
     * Checks if a parameter is annotated with {@link NotNull} or {@link NonNull}.
     *
     * @param parameter The parameter to check.
     * @return true if the parameter is annotated as non-null, otherwise false.
     */
    private static boolean isAnnotatedNonNull(Parameter parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (annotation.annotationType() == NotNull.class || annotation.annotationType() == NonNull.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a parameter is annotated with {@link Nullable}.
     *
     * @param parameter The parameter to check.
     * @return true if the parameter is annotated as nullable, otherwise false.
     */
    private static boolean isAnnotatedNullable(Parameter parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (annotation.annotationType() == Nullable.class) {
                return true;
            }
        }
        return false;
    }
}
