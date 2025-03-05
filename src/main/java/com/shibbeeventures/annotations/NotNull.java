package com.shibbeeventures.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field, method return value, parameter, or local variable cannot be null.
 * This annotation can be used at runtime for validation or reflection-based checks.
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
 * <p>Applying this annotation does not enforce runtime checks by itself but can be used
 * with validation frameworks or custom runtime logic.</p>
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
}
