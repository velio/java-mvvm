package net.velyo.mvvm.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ValidateAttribute
public @interface ValidateNumber {
	String errorMessage() default "The field %s value must be a number";
	String pattern() default "[-+]?([0-9]*\\.[0-9]+|[0-9]+)";
}
