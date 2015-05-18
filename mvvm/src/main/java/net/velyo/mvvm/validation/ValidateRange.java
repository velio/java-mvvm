package net.velyo.mvvm.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ValidateAttribute
public @interface ValidateRange {
	String errorMessage() default "The field %s value must be between %s and %s";
	double max() default 0d;
	double min() default 0d;
	String maxLookup() default "";
	String minLookup() default "";
}
