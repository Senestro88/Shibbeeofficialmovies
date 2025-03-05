package com.shibbeeventures.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field, method return value, parameter, or local variable can be null.
 * This annotation is used for documentation and runtime validation purposes.
 *
 * <p>Can be applied to:
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
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {
}
