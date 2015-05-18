package net.velyo.mvvm.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ValidateAttribute
public @interface ValidateZip {
	String errorMessage() default "The field %s is not a valid USA zip code";
	String pattern() default "^\\d{5}$|^\\d{5}-\\d{4}$";
}	