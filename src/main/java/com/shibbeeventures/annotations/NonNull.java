package com.shibbeeventures.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field, method return value, parameter, or local variable must not be null.
 * This annotation serves the same purpose as {@link NotNull} and can be used interchangeably.
 * 
 * <p>Can be used on:
 * <ul>
 *     <li>Fields</li>
 *     <li>Method parameters</li>
 *     <li>Method return values</li>
 *     <li>Local variables</li>
 *     <li>Other annotations (meta-annotation)</li>
 *     <li>Constructors</li>
 * </ul>
 *
 * <p>Retention: RUNTIME (Available at runtime for reflection).</p>
 *
 * <p>This annotation is useful for runtime validation frameworks and debugging tools that need to check
 * nullability constraints dynamically.</p>
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface NonNull {
}
