/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.velyo.mvvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Velio
 */
@Named
@RequestScoped
public class ModelState {

	// Fields /////////////////////////////////////////////////////////////////////////////////////

	private final Map<String, List<String>> errors = new HashMap<>();
	private final List<String> messages = new ArrayList<>();

	// Methods ////////////////////////////////////////////////////////////////////////////////////

	public void addError(String error) {
		this.addError("", error);
	}

	public void addError(String field, String error) {

		List<String> list = errors.get(field);
		if (list == null) list = new ArrayList<>();
		list.add(error);
		errors.put(field, list);
	}
	
	public void addMessage(String message) {
		this.messages.add(message);
	}
	
	public void clearMessage(){
		this.messages.clear();
	}
	
	public Collection<String> getErrors() {

		Collection<String> result = new ArrayList<>();
		for (List<String> fieldErrors : errors.values()) {
			for (String err : fieldErrors)
				result.add(err);
		}
		return result;
	}

	public Collection<String> getErrors(String field) {
		return hasError(field) ? errors.get(field) : new ArrayList<String>();
	}
	
	public String getFirstError(String field){
		return hasError(field) ? errors.get(field).get(0) : null;
	}
	
	public Collection<String> getMessages(){
		return this.messages;
	}
	
	public boolean hasError(String field) {
		return errors.containsKey(field);
	}
	
	public boolean hasMessages(){
		return !this.messages.isEmpty();
	}

	public boolean isValid() {
		return (errors.keySet().isEmpty());
	}

	public void removeErrors(String field) {
		if (errors.containsKey(field)) errors.remove(field);
	}
}