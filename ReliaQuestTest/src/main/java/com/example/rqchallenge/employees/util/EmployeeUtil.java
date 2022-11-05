package com.example.rqchallenge.employees.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.rqchallenge.employees.EmployeeService;
import com.example.rqchallenge.employees.service.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class EmployeeUtil {
	
	/**
	* This method is called to construct list of employees from given json string 
	* It parses the json string and converts it to list of employees
	*   
	* @param json object 
	* @param key to read to from json
	* @return Returns name of employee which got deleted
	*
	*/
	public static List<Employee> getEmployeeList(JsonObject jsonObject, String attribute) {		 
		ArrayList<Employee> jsonObjList = new ArrayList<Employee>();
		try {
			if (jsonObject == null) {	
				return null;
			}
			
			JsonElement data = jsonObject.get(attribute);		
			if (data == null) {
				return jsonObjList;
			}
			
			final ObjectMapper objectMapper = new ObjectMapper();				
			jsonObjList = objectMapper.readValue(data.toString(), new TypeReference<ArrayList<Employee>>(){});
		
		} catch (JsonProcessingException e) {
			return null;			
		}
				
		return jsonObjList;
	}
	
	public static Employee getEmployeeObject(JsonObject jsonObject, String attribute) {
		
		Employee empObject = null;
		try {
			if (jsonObject == null) {
				return null;
			}
			
			JsonElement data = jsonObject.get(attribute);
			if (data == null) {
				return null;
			}

			final ObjectMapper objectMapper = new ObjectMapper();							
			empObject = objectMapper.readValue(data.toString(), new TypeReference<Employee>(){});
		} catch (JsonProcessingException e) {
			return null;
		}

		return empObject;  
	}

}
