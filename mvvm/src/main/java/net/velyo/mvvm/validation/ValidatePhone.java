package net.velyo.mvvm.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ValidateAttribute
public @interface ValidatePhone {
	String errorMessage() default "The field %s is not a valid phone number, format like:<br>'(111) 111-1111' or '111-1111'";
	String pattern() default "^(\\(\\d{3}\\)\\s?)*\\d{3}-\\d{4}$";
}
