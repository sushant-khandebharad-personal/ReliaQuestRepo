package com.example.rqchallenge.employees;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;


import com.example.rqchallenge.employees.service.Constants;
import com.example.rqchallenge.employees.service.Employee;
import com.example.rqchallenge.employees.util.EmployeeUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class EmployeeService {
		
	Logger logger = LoggerFactory.getLogger(EmployeeService.class);
	
	@Autowired
    private RestTemplate restTemplate;
		
	/**
	* This method is called to get list of all employees as part of ResponseEntity
	* Internally it calls helper method to get list of employees, 
	* then creates ResponseEntity object with this list. 
	* One of the caller of this method is RestController. 
	*   	
	* @return Returns the ResponseEntity containing list of employees 
	*
	*/
	
	public ResponseEntity<List<Employee>>  getAllEmployees() {
		List<Employee> empList = getAllEmployeeList();
		HttpStatus status= (empList == null) ? HttpStatus.INTERNAL_SERVER_ERROR: HttpStatus.OK;
		return new ResponseEntity<>(empList, status);
	}
	
	
	/**
	* This method retrieves list of all employees by executing REST API to server.
	*   	
	* @return Returns list of employees 
	 * @throws Exception 
	*
	*/
	public List<Employee> getAllEmployeeList() {

		List<Employee> empList  = new ArrayList<>();
    	
		URI uri = null;
		 try {
			uri = new URI(Constants.URI_EMPLOYEES);			
		} catch (URISyntaxException e) {
			logger.debug(String.format("Exception occured. URI created for %s is incorrect: ", Constants.URI_EMPLOYEES));
			return null;
		}
	
		logger.debug(String.format("Invoking uri %s: ", uri));
		
		ResponseEntity<String> response = this.restTemplate.getForEntity(uri, String.class);			
		logger.trace(String.format("Response for get all employee list from server: %s", response));
		
							
		if(response.getStatusCode() != HttpStatus.OK) {
			logger.error(String.format("Error in executing uri to get all employee list. HTTP status is not OK. Returning null."));
			return null;
		}
		
		String employeeJson = response.getBody();
		JsonObject jsonObject = new JsonParser().parse(employeeJson).getAsJsonObject();
		empList = EmployeeUtil.getEmployeeList(jsonObject, "data");
		
		
		
		return empList;
	}
	
	
	
	/**
	* This method is called to get details of single employee, as part of ResponseEntity.
	* Internally it calls helper method to get employee details
	* then it creates ResponseEntity object and returns it. 
	* One of the caller of this method is RestController. 
	*   
	* @return Returns the ResponseEntity containing details of single employee with given id. 
	*
	*/
	public ResponseEntity<Employee> getEmployeesById(String id) {
		Map<String, Object> result = getEmployeeWithId(id);
    	Employee emp = null;
    	HttpStatus status = HttpStatus.OK;
    	
    	// NULL received from helper function, return null value as employee details and status as 500 Internal Server Error.
    	if (result == null) {			
			logger.error(String.format("Error is fetching employee details. Returning null value as employee details and marking this request with error internal server error"));
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			return new ResponseEntity<>(emp, status);
		}
    	
    	// Sample value of return result of successful request
    	// {statusKey=success, empObjKey=Id: 4  Name: Cedric Kelly Age: 22 Salary: 433060}
    	// If query is executed successfully, then extract the employee details
    	// This is to differentiate between successful and failed request
    	if(result.containsKey(Constants.STATUS_KEY)) {
    		emp = (result.get(Constants.STATUS_KEY).toString().equalsIgnoreCase(Constants.SUCCESS)) ? (Employee)result.get(Constants.EMP_OBJ_KEY) : null;
    	}
    	
    	return new ResponseEntity<>(emp, status);
    	
	}
	
	
	/**
	* This method retrieves details of employee with given id, by executing REST API to server.
	* 
	* @param  employee id	
	* @return Returns employee details 
	*
	*/
	public Map<String, Object> getEmployeeWithId(String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		if (id == null) {
			logger.error(String.format("Invalid input. NULL value passed as empolyee id."));
			return null;
		}
		
		String uriString = Constants.URI_GET_EMPLOYEE_BY_ID + id;
		URI uri =null;
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e) {
			logger.debug(String.format("Exception occured. URI created for %s is incorrect: ", Constants.URI_GET_EMPLOYEE_BY_ID));
			return null;
		}
		
		ResponseEntity<String> response = this.restTemplate.getForEntity(uri, String.class);
		logger.trace(String.format("Response for get single employee details from server: %s", response));
		
		if(response.getStatusCode() != HttpStatus.OK) {
			logger.error(String.format("Error in executing uri to single employee details. HTTP status is not OK. Returning null."));
			return null;
		}
		
		// If request is successful on server, mark the status as success
		result.put(Constants.STATUS_KEY, Constants.SUCCESS);
		
		String employeeJsonString = response.getBody();
		if (employeeJsonString == null) {
			logger.error(String.format("Error occurred in getting employee by id. Response body is null. Returning null."));
			return null;
		}
		
		JsonObject jsonObject = new JsonParser().parse(employeeJsonString).getAsJsonObject();
		Employee emp = EmployeeUtil.getEmployeeObject(jsonObject, "data");
		if(emp != null) {				
			result.put(Constants.EMP_OBJ_KEY, emp);
		}
		
		logger.trace(String.format("Returning employee details: %s", result));
		
		return result;
			

	}
	
	
	/**
	* This method is called to get list of employees whose name contains given string
	* Internally it retrieves list of all employees from server
	* and then filters out employees whose name contains gives string. 
	* One of the caller of this method is RestController. 
	*   
	* @param String to search in employee name   
	* @return Returns list of employees whose name contains given string.
	*
	*/
	public List<Employee> getEmployeesByNameSearch(String stringToSearch) {
		List<Employee> empList  = new ArrayList<>();				
		
		if (stringToSearch == null) {
			logger.error(String.format("Invalid input. NULL value passed as string to search."));
			return null;
		}
					
		// Get list of all employees from server
		List<Employee> allEmpList = getAllEmployeeList();
		if (allEmpList == null) {
			logger.error(String.format("Error occured while fetching list of all employees from server. Returning null result for name search."));
			return null;
		}
		
		empList = allEmpList.stream().filter(emp -> emp.getName().toLowerCase().contains(stringToSearch.toLowerCase()))
									 .collect(Collectors.toList()); 
		
		logger.trace(String.format("String: %s found in names of these employees: %s", stringToSearch, empList));
		
		return empList;
	}
	

	
	/**
	* This method is called to get highest salary among all employees
	* Internally it retrieves list of all employees from server
	* and then extracts highest salary amount from this list. 
	* One of the caller of this method is RestController. 
	*   
	* @return Returns highest salary amount among all employees
	*
	*/
	public Integer getHighestSalaryOfEmployees() {		
			
		List<Employee> allEmpList = getAllEmployeeList();
		if(allEmpList == null){
			logger.error(String.format("Error in getting highest salary among employee. List of all employees is null."));
			return null;			
		}
		Optional<Employee> emp = allEmpList.stream()
		        						   .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
		        						   .findFirst();
		 
		int highestSalary =  emp.get().getSalary();
		logger.trace(String.format("Highest salary is: %d ", highestSalary));
		
		return highestSalary;
	}

	
	/**
	* This method is called to get list of N employees having highest salary 
	* Internally it retrieves list of all employees from server
	* and then extracts N employees having highest salary 
	* One of the caller of this method is RestController. 
	*   
	* @param count of employees  
	* @return Returns list of N employees having highest salary amount among all employees
	*
	*/
	public List<Employee> getTopNHighestEarningEmployeeNames(int count) {
		
		if(count < 0 ){
			logger.error(String.format("Invalid input. Count of employees should be non-negative. Returning null."));
			return null;
		}
		
		List<Employee> allEmpList = getAllEmployeeList();
		if(allEmpList == null){
			logger.error(String.format("Error in getting top %d employees with highest salary. List of all employees is null.", count));
			return null;
		}
		
		
		List<Employee> highestEarningEmpNames = allEmpList.stream()
		        						   				.sorted(Comparator.comparingInt(Employee::getSalary).reversed())
		        						   				.limit(count)
		        						   				.collect(Collectors.toList());		        						   						        						   				
				
		logger.trace(String.format("List of top %d employees with gighest salary is: %s", count, highestEarningEmpNames));
		
		return highestEarningEmpNames;
	}
	
	
	
	/**
	* This method validates input for create employee request
	* 
	* @param  employee details	
	* @return Returns boolean depending upon validation of input 
	*
	*/
	private static boolean validateInputForCreateEmployee(Map<String, Object> employeeInput) {
		if ( CollectionUtils.isEmpty(employeeInput) ||
			 ! employeeInput.containsKey(Constants.NAME) ||
			 ! employeeInput.containsKey(Constants.AGE) ||
			 ! employeeInput.containsKey(Constants.SALARY)
			) {
			
			return false;
		}
		 
				
		 Object nameObj = employeeInput.get(Constants.NAME);
		 Object ageObj = employeeInput.get(Constants.AGE);
		 Object salaryObj = employeeInput.get(Constants.SALARY);
		 if (nameObj == null || ageObj == null || salaryObj == null) {
			return false;
		 }
				
			 
		return true; 
	}
	
	
	
	/**
	* This method is called to create employee
	* Internally it calls URI to create employee on server.
	* One of the caller of this method is RestController. 
	*   
	* @param employee details
	* @return Returns boolean depending upon result of employee creation operation.
	*
	*/
	
	public boolean createEmployee(Map<String, Object> employeeInput) {
		boolean result = true;
		
		if (employeeInput == null) {
			logger.error(String.format("Invalid input for create employee request. NULL value passed as employee details. Returning false."));
			return false;
		}			
		    	
		
		boolean validInput = validateInputForCreateEmployee(employeeInput);
		if (! validInput) {
			logger.error(String.format("Invalid input for create employee request. Please check of all required field of employee are passed and are valid."));
			return false;
		}
	
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        HttpEntity<?> requestEntity = new HttpEntity<>(employeeInput, headers);	        
        ResponseEntity<String> response = this.restTemplate.exchange(Constants.URI_CREATE_EMPLOYEE, HttpMethod.POST, requestEntity, String.class);            
    
	
		
        logger.trace(String.format("Server response for create employee request: %s", response));			
        HttpStatus status = response.getStatusCode();
        if (status != HttpStatus.OK) {
        	logger.debug(String.format("Status of request to create employee is not OK. Returning false"));
        	return false;
        }
		
		return result;
	}
	
	
	/**
	* This method is called to delete employee with given id.
	* Internally it calls URI to check if employee exists.
	* If employee exists, it calls request to delete employee.
	* One of the caller of this method is RestController. 
	*   
	* @param employee id 
	* @return Returns name of employee which got deleted
	*
	*/
	public String deleteEmployee(String id) {		
		
		if(id == null) {
			logger.error(String.format("Invalid input for delete emplyee request. NULL value passed as employee id. Returning null."));
			return null;
		}
		
		
		String nameOfEmpToDelete = null;		
		Map<String, Object> result = getEmployeeWithId(id);		
		
		if(result == null) {
			logger.error(String.format("Error occured in fetching emploee with id: %s. Returning null.", id));
			return null;
		}
		
		logger.trace(String.format("Details of employee with id: %s, %s", id, result));
		
		Employee empToDelete = null;
		if(result.containsKey(Constants.STATUS_KEY)) {
			empToDelete = (result.get(Constants.STATUS_KEY).toString().equalsIgnoreCase(Constants.SUCCESS)) ? (Employee)result.get(Constants.EMP_OBJ_KEY) : null;
    	}
		
		if(empToDelete != null) {
			nameOfEmpToDelete = empToDelete.getName();
			try {				
				Map<String, String> uriVariables = new HashMap<>();			 
		        uriVariables.put("id", id);
				HttpHeaders headers = new HttpHeaders();
				HttpEntity<?> requestEntity = new HttpEntity<>(headers);				
				ResponseEntity<String> response = this.restTemplate.exchange(Constants.URI_DELETE_EMPLOYEE, HttpMethod.DELETE, requestEntity, String.class, uriVariables);
				logger.debug(String.format("Response for delete request of employee with id: %s %s", id, response));
			}catch (Exception e) {
				logger.error(String.format("Exception occured in deleting employee having id %s Exception details: %s ", id, e));
				return null;
			}
			
		}
		
		return nameOfEmpToDelete;
	}
	
	
	
}
