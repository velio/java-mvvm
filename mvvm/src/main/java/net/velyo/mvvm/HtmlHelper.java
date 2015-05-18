package net.velyo.mvvm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.martinbros.ejb.SelectItem;
import com.martinbros.ejb.logging.Log;
import com.martinbros.ejb.mvc.models.Model;
import com.martinbros.ejb.mvc.validation.ValidateCompare;
import com.martinbros.ejb.mvc.validation.ValidateEmail;
import com.martinbros.ejb.mvc.validation.ValidateLength;
import com.martinbros.ejb.mvc.validation.ValidateNumber;
import com.martinbros.ejb.mvc.validation.ValidateRange;
import com.martinbros.ejb.mvc.validation.ValidateRegex;
import com.martinbros.ejb.mvc.validation.ValidateRequired;
import com.martinbros.ejb.mvc.validation.ValidateUrl;
import com.martinbros.ejb.mvc.validation.ValidateZip;

@Named("html")
@RequestScoped
public class HtmlHelper {

	// Fields /////////////////////////////////////////////////////////////////////////////////////

	Model model;
	@Inject
	ModelState state;

	// Methods ////////////////////////////////////////////////////////////////////////////////////

	public void setModel(Model model) {
		this.model = model;
	}

	public String checkBox(String field, String value, String attributes) {
//		if (StringUtils.isEmpty(value)) value = "true";
		return renderInput(InputType.CHECK_BOX, field, value, attributes);
	}

	public String checkBox(String field, String attributes) {
		return checkBox(field, null, attributes);
	}
	
	public String dropDown(String field, String optionsProperty, String attributes) {

		Object value= fetchPropertyValue(optionsProperty);
		if(value != null){
			if(value instanceof Collection<?>){
				@SuppressWarnings("unchecked")
				Collection<SelectItem> list = (Collection<SelectItem>)value;
				return dropDownImpl(field, list, attributes);
			}
			else if(value instanceof String){
				return dropDownValues(field, value.toString(), attributes);
			}
		}
		return dropDownImpl(field, null, attributes);
	}

	public String dropDown(String field, String optionsProperty) {
		return dropDown(field, optionsProperty, null);
	}

	public String dropDownValues(String field, String options, String attributes) {

		Collection<SelectItem> list = null;
		if (StringUtils.isNotEmpty(options)) {
			list = new ArrayList<>();
			for (String item : StringUtils.split(options, ',')) {
				list.add(new SelectItem(item));
			}
		}
		return dropDownImpl(field, list, attributes);
	}

	public String dropDownValues(String field, String options) {
		return dropDownValues(field, options, null);
	}
	
	public String hiddenField(String fieldName, String value, String attributes){
		return renderInput(InputType.HIDDEN, fieldName, value, attributes);
	}
	
	public String hiddenField(String fieldName, String attributes){
		return hiddenField(fieldName, null, attributes);
	}
	
	public String hiddenField(String fieldName){
		return hiddenField(fieldName, null, null);
	}
	
	public String hidden(String field, String value){
		return renderInput(InputType.HIDDEN, field, value, null);
	}

	public String messageSummary(String title) {

		if (state.hasMessages()) {
			StringBuilder buffer = new StringBuilder(256);
			buffer.append("<div class='message-summary ui-state-highlight ui-corner-all'>");
			if (title != null) buffer.append("<span>").append(title).append("</span>");
			buffer.append("<ul>");
			for (String message : state.getMessages()) {
				buffer.append("<li>").append(message).append("</li>");
			}
			buffer.append("</ul>");
			buffer.append("</div>");
			return buffer.toString();
		}
		return null;
	}
	
	public String radioButton(String field, String value, String attributes){
//		if(StringUtils.isEmpty(value)) value = "true";
		return renderInput(InputType.RADIO, field, value, attributes);
	}
	
	public String radioButton(String field, String attributes){
		return radioButton(field, null, attributes);
	}
	
	public String radioGroup(String field, String options, String attributes, boolean vertical, boolean leftlabel) {
		
		Object value= fetchPropertyValue(options);
		if(value != null){
			if(value instanceof Collection<?>){
				@SuppressWarnings("unchecked")
				Collection<SelectItem> list = (Collection<SelectItem>)value;
				return renderRadioGroup(field, list, attributes, vertical, leftlabel);
			}
			else if(value instanceof String) {
				Collection<SelectItem> list = null;
				if (StringUtils.isNotEmpty(options)) {
					list = new ArrayList<>();
					for (String item : StringUtils.split(options, ',')) {
						list.add(new SelectItem(item));
					}
				}
				return renderRadioGroup(field, list, attributes, vertical, leftlabel);
			}
		}
		return renderRadioGroup(field, options, attributes, vertical, leftlabel);
	}
	
	public String radioGroup(String field, String optionsProperty, String attributes) {
		return radioGroup(field, optionsProperty, attributes, true, true);
	}
	
	public String radioGroup(String field, String optionsProperty) {
		return radioGroup(field, optionsProperty, null, true, true);
	}
	
	public String textArea(String property) {
		return textArea(property, null);
	}

	public String textArea(String field, String attributes) {

		StringBuilder buffer = new StringBuilder(256);

		buffer.append("<textarea ");
		writeFieldIdentity(buffer, field);
		writeCssClass(buffer, field);
		writeValidationAttributes(buffer, field);
		writeAttributes(buffer, attributes);
		buffer.append(">");
		writeValueContent(buffer, field);
		buffer.append("</textarea>");

		return buffer.toString();
	}

	public String textBox(String property) {
		return textBox(property, null, null);
	}
	
	public String textBox(String field, String attributes) {
		return textBox(field, null, attributes);
	}
	
	public String textBox(String field, String value, String attributes) {
		
		StringBuilder buffer = new StringBuilder(256);
		
		buffer.append("<input type='text' ");
		writeFieldIdentity(buffer, field);
		writeCssClass(buffer, field);
		if (value == null)
			writeValue(buffer, field);
		else {
			buffer.append("value='");
			buffer.append(fetchPropertyValue(value));
			buffer.append("' ");
		}
		writeValidationAttributes(buffer, field);
		writeAttributes(buffer, attributes);
		buffer.append("/>");
		
		return buffer.toString();
	}

	public String passwordBox(String property) {
		return passwordBox(property, null);
	}

	public String passwordBox(String field, String attributes) {

		StringBuilder buffer = new StringBuilder(256);

		buffer.append("<input type=\"password\" ");
		writeFieldIdentity(buffer, field);
		writeCssClass(buffer, field);
		writeValue(buffer, field);
		writeValidationAttributes(buffer, field);
		writeAttributes(buffer, attributes);
		buffer.append("/>");

		return buffer.toString();
	}

	public String validationMessage(String property) {

		StringBuilder buffer = new StringBuilder(256);
		boolean hasError = state.hasError(property);

		String css = hasError ? "field-validation-error" : "field-validation-valid";
		buffer.append("<span data-valmsg-for='").append(property);
		buffer.append("' class='").append(css).append("'");
		buffer.append(">");
		if (hasError) {
			buffer.append(state.getFirstError(property));
		}
		buffer.append("</span>");
		return buffer.toString();
	}

	public String validationSummary() {
		return validationSummary(null);
	}

	public String validationSummary(String title) {

		StringBuilder buffer = new StringBuilder(256);
		String css = state.isValid() ? "validation-summary-valid" : "validation-summary-errors";

		buffer.append("<div class='ui-state-error ui-corner-all ").append(css).append("'>");
		buffer.append("<div ").append(" class='").append(css).append("' data-valmsg-summary='true'>");
		if (title != null) buffer.append("<span>").append(title).append("</span>");
		buffer.append("<ul>");
		for (String error : state.getErrors()) {
			buffer.append("<li>").append(error).append("</li>");
		}
		buffer.append("</ul>");
		buffer.append("</div>");
		buffer.append("</div>");

		return buffer.toString();
	}
	
	// Render Methods /////////////////////////////////////////////////////////////////////////////
	
	protected String renderInput(String type, String field, String value, String attributes) {
		
		StringBuilder buffer = new StringBuilder(256);
		
		buffer.append("<input type='").append(type).append("' ");
		writeFieldIdentity(buffer, field);
		writeCssClass(buffer, field);
		if (StringUtils.isEmpty(value))
			writeValue(buffer, field);
		else {
			buffer.append("value='");
			buffer.append(fetchPropertyValue(value));
			buffer.append("' ");
		}
		writeValidationAttributes(buffer, field);
		writeAttributes(buffer, attributes);
		buffer.append("/>");
		
		return buffer.toString();
	}
	
	protected String renderRadioGroup(String field, Collection<SelectItem> list, String attributes, boolean vertical, boolean leftlabel){
		
		StringBuilder buffer = new StringBuilder(256);
		Object value = fecthFieldValue(model, field);
		String optionValue, optionText;
		boolean selected;
		int index = 1;
		
		for (SelectItem option : list) {
			optionValue = option.getValue();
			optionText = option.getText();
			selected = ((value != null) && (value.toString().equals(optionValue)));
			
			if(vertical) buffer.append("<div>");
			
			if(leftlabel){
				buffer
					.append("<label>")
					.append(optionText)
					.append("</label>")
					.append("<input type='radio' value='")
					.append(optionValue)
					.append("'")
					.append(" id='")
					.append(field)
					.append(index ++)
					.append("' name='")
					.append(field)
					.append("' ");
				
				writeCssClass(buffer, field);
				writeValidationAttributes(buffer, field);
				writeAttributes(buffer, attributes);
				
				if (selected) 
					buffer.append("checked = checked");
				
				buffer.append(">");
				}else{
					buffer
					.append("<input type='radio' value='")
					.append(optionValue)
					.append("'")
					.append(" id='")
					.append(field)
					.append(index ++)
					.append("' name='")
					.append(field)
					.append("' ");
				
				writeCssClass(buffer, field);
				writeValidationAttributes(buffer, field);
				writeAttributes(buffer, attributes);
				
				if (selected) 
					buffer.append("checked = checked");
				
				buffer.append(">")
					  .append("<label>")
					  .append(optionText)
					  .append("</label>");
				}
			
			if(vertical) buffer.append("</div>");
		}
		
		return buffer.toString();
	}
	
	protected String renderRadioGroup(String field, String options, String attributes, boolean vertical, boolean leftlabel){
		
		if (StringUtils.isNotEmpty(options)) {
			Collection<SelectItem> list = null;
			if (StringUtils.isNotEmpty(options)) {
				list = new ArrayList<>();
				for (String item : StringUtils.split(options, ',')) {
					list.add(new SelectItem(item));
				}
			}
			return renderRadioGroup(field, list, attributes, vertical, leftlabel);
		}
		return null;
	}

	// Utility Methods ////////////////////////////////////////////////////////////////////////////

	protected String dropDownImpl(String field, Collection<SelectItem> list, String attributes) {

		StringBuilder buffer = new StringBuilder(256);
		buffer.append("<select ");
		writeFieldIdentity(buffer, field);
		writeCssClass(buffer, field);
		writeValidationAttributes(buffer, field);
		writeAttributes(buffer, attributes);
		buffer.append(">");

		if (list != null) writeSelectOptions(buffer, field, list);

		buffer.append("</select>");

		return buffer.toString();
	}

	protected Object fecthFieldValue(Object instance, String fieldName) {

		Object value = null;
		if(fieldName.indexOf('.') == -1) {
			try {
				value = FieldUtils.readField(instance, fieldName, true);
			}
			catch (Exception ex) {
				Log.warn(ex);
			}
		}
		else {
			String childFieldName = fieldName.substring(0, fieldName.indexOf('.'));
			String subFieldName = fieldName.substring(fieldName.indexOf('.') + 1);
			try {
				Object child =  FieldUtils.readField(instance, childFieldName, true);
				if(child != null) value = fecthFieldValue(child, subFieldName);
					
			}
			catch(Exception ex) {
				Log.warn(ex);
			}
		}
		return value;
	}
	
	protected Object fetchPropertyValue(String property){
		
		Object value = null;
		if(StringUtils.isNotBlank(property)) {
			
			try{
				value = MethodUtils.invokeMethod(model, property, null);
			}
			catch(Exception ex){
				Log.warn(ex);
			}
		}
		return (value != null) ? value : property;
	}

	protected StringBuilder writeAttributes(StringBuilder buffer, String attributes) {

		if (attributes != null) buffer.append(" ").append(attributes).append(" ");
		return buffer;
	}

	protected StringBuilder writeCssClass(StringBuilder buffer, String field) {

		if (state.hasError(field)) buffer.append("class='input-validation-error' ");
		return buffer;
	}

	protected StringBuilder writeFieldIdentity(StringBuilder buffer, String field) {

		buffer.append("id='").append(field.replace('.', '_')).append("' name='").append(field).append("' ");
		return buffer;
	}

	protected void writeSelectOptions(StringBuilder buffer, String field, Collection<SelectItem> list) {

		Object value = fecthFieldValue(model, field);
		String optionValue, optionText;
		boolean selected;
		
		for (SelectItem option : list) {
			optionValue = option.getValue();
			optionText = option.getText();
			selected = ((value != null) && (value.toString().equals(optionValue)));
			
			buffer
				.append("<option value='")
				.append(optionValue)
				.append("'");
			
			if (selected) 
				buffer.append(" selected='selected'");
			
			buffer
				.append(">")
				.append(optionText)
				.append("</option>");
		}
	}

	protected StringBuilder writeValidationAttributes(StringBuilder output, String fieldName) {

		Field field = FieldUtils.getDeclaredField(model.getModelType(), fieldName, true);
		if (field != null) {
			StringBuilder buffer = new StringBuilder();
			String message;

			// required
			ValidateRequired required = field.getAnnotation(ValidateRequired.class);
			if (required != null) {
				message = String.format(required.errorMessage(), fieldName);
				buffer.append("data-val-required='").append(message).append("' ");
			}

			// regex
			ValidateRegex regex = field.getAnnotation(ValidateRegex.class);
			if (regex != null) {
				message = String.format(regex.errorMessage(), fieldName);
				buffer.append("data-val-regex='").append(message).append("' data-val-regex-pattern='").append(regex.value()).append("' ");
			}

			// number
			ValidateNumber number = field.getAnnotation(ValidateNumber.class);
			if (number != null) {
				message = String.format(number.errorMessage(), fieldName);
				buffer.append("data-val-number='").append(message).append("' ");
			}

			// range
			ValidateRange range = field.getAnnotation(ValidateRange.class);
			if (range != null) {
				message = String.format(range.errorMessage(), fieldName, range.min(), range.max());
				buffer.append("data-val-range='").append(message).append("' data-val-range-min='").append(range.min()).append("' data-val-range-max='")
								.append(range.max()).append("' ");
			}

			// length
			ValidateLength length = field.getAnnotation(ValidateLength.class);
			if (length != null) {
				message = String.format(length.errorMessage(), fieldName, length.max());
				buffer.append("data-val-length='").append(message).append("' data-val-length-max='").append(length.max()).append("' ");
			}

			// compare
			ValidateCompare compare = field.getAnnotation(ValidateCompare.class);
			if (compare != null) {
				message = String.format(compare.errorMessage(), fieldName, compare.target());
				buffer.append("data-val-equalto='").append(message).append("' data-val-equalto-other='*.").append(compare.target()).append("' ");
			}

			// email
			ValidateEmail email = field.getAnnotation(ValidateEmail.class);
			if (email != null) {
				message = String.format(email.errorMessage(), fieldName);
				buffer.append("data-val-email='").append(message).append("' ");
			}

			// url
			ValidateUrl url = field.getAnnotation(ValidateUrl.class);
			if (url != null) {
				message = String.format(url.errorMessage(), fieldName);
				buffer.append("data-val-url='").append(message).append("' ");
			}

			if (buffer.length() > 0) output.append("data-val='true' ").append(buffer.toString());
			
			// zip
			ValidateZip zip = field.getAnnotation(ValidateZip.class);
			if (zip != null) {
				message = String.format(zip.errorMessage(), fieldName);
				buffer.append("data-val-regex='").append(message).append("' data-val-regex-pattern='").append(zip.pattern()).append("' ");
			}
		}

		return output;
	}

	protected StringBuilder writeValue(StringBuilder buffer, String field) {

		Object value = fecthFieldValue(model, field);
		if (value != null) buffer.append("value='").append(value).append("' ");
		return buffer;
	}

	protected StringBuilder writeValueContent(StringBuilder buffer, String field) {

		Object value = fecthFieldValue(model, field);
		if (value != null) buffer.append(value);
		return buffer;
	}
	
	// Nested Types ///////////////////////////////////////////////////////////////////////////////
	
	public class InputType {
		public static final String CHECK_BOX = "checkbox";
		public static final String HIDDEN = "hidden";
		public static final String RADIO = "radio";
		public static final String TEXT = "text";
	}
}

/*
 * required regex range number length equalto email url ------------------------------- accept creditcard date digits
 * remote password
 */

