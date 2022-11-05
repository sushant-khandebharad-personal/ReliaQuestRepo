package com.example.rqchallenge;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.util.*;
import java.net.URI;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.rqchallenge.employees.EmployeeService;
import com.example.rqchallenge.employees.service.Constants;
import com.example.rqchallenge.employees.service.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;



@SpringBootTest
class RqChallengeApplicationTests {

    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private EmployeeService empService = new EmployeeService();

    /**
	* This is a utility method to read json file 
	*    
	* @param  filePath Path of file to read 
	* @return Returns contents of file in string form
	*
	*/    
    private static String readFileContents(String filePath) {
        StringBuilder strBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currLine;
            while ((currLine = br.readLine()) != null) {
                strBuilder.append(currLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strBuilder.toString();
    }
    
    
    /**
	* Tests for getting details of employee having given id   
	* Tests for 3 scenarios-
	* 	1. Employee with given id exists. id=2 
	* 	2. Employee with given id does not exists. id=9999  
	* 	3. Null value passed for input id   
	*   
	* Internally it calls the helper method searchForId() for tests.
	* 
	* @see searchForId
	* 
	*/  
    
    @Test
    void testGetEmployeeById() {
    	searchForId("2");
    	searchForId("9999");
    	searchForId(null);
    }
    
    
    /**
	* This method tests the request of getting employee having given id 
	* It mocks the execution of REST URI- https://dummy.restapiexample.com/api/v1/employee/{id}
	* The result of request is read from file.
	* This method handles 3 scenarios-
	* 	1. Employee with given id exists. id=2 
	* 	2. Employee with given id does not exists. id=9999  
	* 	3. Null value passed for input id   
	*   
	* This method tests the business logic, for given request and responses.
	* The sample response data is collected actual response for different types of employee id
	*   
	* @param id search employee with this id 
	* 
	*/
    void searchForId(String id) {    	
    	try {    		
    		if (id == null) {    			
    			Map<String, Object> actaulMap = empService.getEmployeeWithId(id);
    	    	assertTrue(actaulMap == null);    	    	    
    	    	return;
    		}
			
	    	String url = Constants.URI_GET_EMPLOYEE_BY_ID + id;	    	    	
    		URI uri = new URI(url);

    		// Test case for employee id exists
			if(id.equals("2")) {
				
				// Mock the response for request "https://dummy.restapiexample.com/api/v1/employee/{id}" from the file
				File responseFile = new File("./src/test/java/resource/response/TestOutput_GetEmployeeWithId2.json");
				String empId2Response = readFileContents(responseFile.getAbsolutePath());									

	    		Mockito
	            .when(restTemplate.getForEntity(uri, String.class))
	            .thenReturn(new ResponseEntity<String>(empId2Response, HttpStatus.OK));

				Map<String, Object> actaulMap = empService.getEmployeeWithId(id);
				assertTrue(actaulMap.get("statusKey").equals("success"));
				
				Employee actualEmp = ((Employee) actaulMap.get("empObjKey"));
				assertTrue(actualEmp.getId().equals("2"));
				assertTrue(actualEmp.getName().equals("Garrett Winters"));
				assertTrue(actualEmp.getAge() == 63);
				assertTrue(actualEmp.getSalary() == 170750);				
				return;			
			}
		
			
			if(id.equals("9999")) {
				File responseFile = new File("./src/test/java/resource/response/TestOutput_GetEmployeeWithId_9999.json");
				String empId9999Response = readFileContents(responseFile.getAbsolutePath());			

				Mockito
		        .when(restTemplate.getForEntity(uri, String.class))
		        .thenReturn(new ResponseEntity<String>(empId9999Response, HttpStatus.OK));
											
				Map<String, Object> actaulMap = empService.getEmployeeWithId(id);
				assertTrue(actaulMap.get("statusKey").equals("success"));
				assertTrue(actaulMap.get("data") == null);
				
				return;
			}			
			
    	 } catch(Exception e) {    		 
    		 e.printStackTrace();
    	}
    }
    
        
    
    
    /**
	* Tests for getting list of all employees   
	* Tests scenario of list of employee successfully retrieved
	* It mocks the request- https://dummy.restapiexample.com/api/v1/employees
	* The result of this request is read from file.
	* Business logic is tested on the result	
	* 
	*/  
    
    @Test
    void testGetAllEmployee() {
    	    	
    	try {
    		File responseFile = new File("./src/test/java/resource/response/TestOutput_GetAllEmployee.json");    		       		    		  	
    		String allEmpResponseString = readFileContents(responseFile.getAbsolutePath());    		 
    		
    		URI uri = new URI(Constants.URI_EMPLOYEES);
    		Mockito
            .when(restTemplate.getForEntity(uri, String.class))
            .thenReturn(new ResponseEntity<String>(allEmpResponseString, HttpStatus.OK));

    		ResponseEntity<List<Employee>> response = empService.getAllEmployees();    		
	    	HttpStatus status = response.getStatusCode();
	    	List<Employee> responseBody = response.getBody();
	    	
	    	if (status == HttpStatus.OK)  {
	    		assertTrue(responseBody != null);
	    	}
	    	else { 	    		
	    		assertTrue(responseBody == null);
	    	}
	    	 
    	 } catch(Exception e) {
    		 e.printStackTrace();
    	}
    	
    }
    
    
    
    /**
	* Tests for getting employee details whose name contains given string   
	* Tests 3 scenarios:
	* 1. Input string found in employee names, input: "ON"
	* 2. Input string not found in employee names, input: "NON_OCCURING_STRING"
	* 3. Null value as input string
	* 
	*/  
	@Test
    void testGetEmployeesByNameSearch() {
    	searchForString("ON"); 
    	searchForString("NON_OCCURING_STRING");
    	searchForString(null);
    }
    
    
    /**
	* Tests for getting employee details whose name contains given string   
	* Handles 3 scenarios:
	* 1. Input string found in employee names, input: "ON"
	* 2. Input string not found in employee names, input: "NON_OCCURING_STRING"
	* 3. Null value as input string	*
	* 
	* It mocks the request https://dummy.restapiexample.com/api/v1/employees to get list of all employees
	*/  
    void searchForString(String searchString) {
    	try {
    			
    		File responseFile = new File("./src/test/java/resource/response/TestOutput_GetAllEmployee.json");    		       		    		  
			String allEmpResponseString = readFileContents(responseFile.getAbsolutePath());
		
			URI uri = new URI(Constants.URI_EMPLOYEES);
			Mockito
	        .when(restTemplate.getForEntity(uri, String.class))
	        .thenReturn(new ResponseEntity<String>(allEmpResponseString, HttpStatus.OK));	          
			
			if (searchString == null) {
				List<Employee> receivedEmpList = empService.getEmployeesByNameSearch(null);
		    	assertTrue(receivedEmpList == null );
		    	return;
			}
			
			if(searchString.equalsIgnoreCase("ON")) {				
		    	List<Employee> receivedEmpList = empService.getEmployeesByNameSearch("ON");	    	
	    	
		    	// Verify the received emp list with expected output
		    	File expectedEmpListFile = new File("./src/test/java/resource/response/TestOutput_SearchString_ON.json");				    		    		       		    		  
				String expectedEmpListStr = readFileContents(expectedEmpListFile.getAbsolutePath());			
				
				// Convert string into list of employee objects
	  	        ObjectMapper objMapper = new ObjectMapper();
	   	        CollectionType typeReference =  TypeFactory.defaultInstance().constructCollectionType(List.class, Employee.class);
		    	List<Employee> expectedEmpList =  objMapper.readValue(expectedEmpListStr, typeReference);		    			    
		    	
		    	assertTrue(receivedEmpList.containsAll(expectedEmpList));	    
		    	assertTrue(expectedEmpList.containsAll(receivedEmpList));
		    	return;
			} 
			
			if (searchString.equalsIgnoreCase("NON_OCCURING_STRING") ) {				
				List<Employee> receivedEmpList = empService.getEmployeesByNameSearch("NON_OCCURING_STRING");	    	
		    	assertTrue(receivedEmpList.isEmpty());
		    	return;
			}
			

    	 } catch(Exception e) {
    		 e.printStackTrace();
    	}
    	
    }
    
   
    
    /**
	* Tests for getting highest salary among employees    
	* It mocks the request https://dummy.restapiexample.com/api/v1/employees to get list of all employees
	*/ 
    @Test
    void testHighestSalary() {
    	    	
    	try {    		    	

    		File responseFile = new File("./src/test/java/resource/response/TestOutput_GetAllEmployee.json");
    		String allEmpResponseString = readFileContents(responseFile.getAbsolutePath());
    		URI uri = new URI(Constants.URI_EMPLOYEES);
    		Mockito
            .when(restTemplate.getForEntity(uri, String.class))
            .thenReturn(new ResponseEntity<String>(allEmpResponseString, HttpStatus.OK));

    		Integer highestSalary = empService.getHighestSalaryOfEmployees();	    	
	    	assertTrue(highestSalary == 725000);
	    	
    	 } catch(Exception e) {
    		 e.printStackTrace();
    	}    	
    }
    
    
    
        
   
    /**
	* Tests for create employee     
	* It mocks the request https://dummy.restapiexample.com/api/v1/create to create employee
	*/ 
    @Test
    void testCreateEmployee() {    	    	
    	try { 
			File responseFile = new File("./src/test/java/resource/response/TestOutput_CreateEmployee.json");
			String createEmpResponseExpected = readFileContents(responseFile.getAbsolutePath());

			HttpHeaders headers = new HttpHeaders();
			ResponseEntity<String> responseEntity = new ResponseEntity<>(createEmpResponseExpected, headers, HttpStatus.OK);
			
			Map<String, Object> empData = new HashMap<String, Object>();
	    	empData.put("name", "Sushant Khandebharad");
	    	empData.put("age", 33);
	    	empData.put("salary", 10000);
			
			MultiValueMap<String, String> mocheaders = new LinkedMultiValueMap<String, String>();
	        HttpEntity<?> requestEntity = new HttpEntity<>(empData, mocheaders);	  
			
			Mockito
			.when(restTemplate.exchange(
					 Constants.URI_CREATE_EMPLOYEE, 
					 HttpMethod.POST,
					 requestEntity,
					 String.class)
				 )
			.thenReturn(responseEntity);
			
			boolean result =  empService.createEmployee(empData);
			assertTrue(result == true);
			
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
     
     
     
    /**
	* Tests for delete employee
	* Tests 3 cases:
	* 1. Employee to be deleted exists, id=2
	* 2. Employee to be deleted does not exists, id=9999
	* 3. Null id input
	* 
	*/ 
    @Test
    void testDeleteEmployeeById() {
    	deleteById("2");
    	deleteById("9999");
    	deleteById(null);    	   
    }
	
	
    /**
	* Helper method to test delete employee
	* Handles  3 cases:
	* 1. Employee to be deleted exists, id=2
	* 2. Employee to be deleted does not exists, id=9999
	* 3. Null id input
	* 
	* It mocks request: https://dummy.restapiexample.com/api/v1/employee/
	* 
	*/ 
    void deleteById(String id) {    	
    	try {
	    	String url = Constants.URI_GET_EMPLOYEE_BY_ID + id;
	    	URI uri = new URI(url);
    	
			if (id == null) {
				String empName = empService.deleteEmployee(id);
				assertTrue(empName == null);
		    	return;
			}
			
	    	
	    	if (id.equalsIgnoreCase("2") ) {
	    		File responseFile = new File("./src/test/java/resource/response/TestOutput_GetEmployeeWithId2.json");
	    		String empId2String = readFileContents(responseFile.getAbsolutePath());

	    		Mockito
	    		.when(restTemplate.getForEntity(uri, String.class))
	    		.thenReturn(new ResponseEntity<String>(empId2String, HttpStatus.OK));

	    		Map<String, String> uriVariables = new HashMap<>();			 
	        	uriVariables.put("id", id);
				HttpHeaders headers = new HttpHeaders();
				HttpEntity<?> requestEntity = new HttpEntity<>(headers);
								
				File deleteResponseFileEmpId2 = new File("./src/test/java/resource/response/TestOutput_DeleteEmpWithId_2.json");				    		       		    		  
				String deleteEmpResponseExpected = readFileContents(deleteResponseFileEmpId2.getAbsolutePath());				
				ResponseEntity<String> responseEntity = new ResponseEntity<>(deleteEmpResponseExpected, headers, HttpStatus.OK);				    		 
								
				Mockito
				.when(restTemplate.exchange(
						 Constants.URI_DELETE_EMPLOYEE, 
						 HttpMethod.DELETE,
						 requestEntity,
						 String.class,
						 uriVariables)
					 )
				.thenReturn(responseEntity);
				
	    		
	    		String empName = empService.deleteEmployee(id);	    		
				assertTrue(empName.equals("Garrett Winters"));
	    	}
	    	
	    	
			if(id.equals("9999")) {
				File responseFile = new File("./src/test/java/resource/response/TestOutput_GetEmployeeWithId_9999.json");
				String empId9999String = readFileContents(responseFile.getAbsolutePath());
							
				Mockito
		        .when(restTemplate.getForEntity(uri, String.class))
		        .thenReturn(new ResponseEntity<String>(empId9999String, HttpStatus.OK));
				
	    		String empName = empService.deleteEmployee(id);
				assertTrue(empName == null);
			}				    
	    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }        
	
}
