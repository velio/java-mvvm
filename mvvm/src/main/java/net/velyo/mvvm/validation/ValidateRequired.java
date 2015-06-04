package net.velyo.mvvm.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ValidateAttribute
public @interface ValidateRequired {
	String errorMessage() default "The field %s is required";
}
