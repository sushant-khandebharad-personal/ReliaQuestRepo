package com.example.rqchallenge.employees;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rqchallenge.employees.service.Employee;

@RestController
public class EmployeeController implements IEmployeeController {
	
	@Autowired
	EmployeeService empService;
	
	@GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
		return empService.getAllEmployees();
    }

	
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {    	
		List<Employee> empList = empService.getEmployeesByNameSearch(searchString);
    	HttpStatus status= (empList == null) ? HttpStatus.INTERNAL_SERVER_ERROR: HttpStatus.OK;
    	return new ResponseEntity<>(empList, status);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {    	
    	return empService.getEmployeesById(id);
    	
    }

    
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees(){    
    	Integer highestSalary= empService.getHighestSalaryOfEmployees();
    	HttpStatus status= (highestSalary == null) ? HttpStatus.INTERNAL_SERVER_ERROR: HttpStatus.OK;
    	return new ResponseEntity<>(highestSalary, status);
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<Employee>> getTopTenHighestEarningEmployeeNames() {    	
    	List<Employee> highestEarningEmp = empService.getTopNHighestEarningEmployeeNames(10);
    	HttpStatus status= (highestEarningEmp == null) ? HttpStatus.INTERNAL_SERVER_ERROR: HttpStatus.OK;    	
    	return new ResponseEntity<>(highestEarningEmp, status);
    	
    }

    @PostMapping()
    public ResponseEntity<String> createEmployee(@RequestBody Map<String, Object> employeeInput) {   
    	boolean result =  empService.createEmployee(employeeInput);    	
    	String resultString = result == true ? "Success" : "Failed";    	
    	HttpStatus status= result ?  HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
    	return new ResponseEntity<>(resultString, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id){    	
    	String empName = empService.deleteEmployee(id);
    	HttpStatus status= (empName == null) ? HttpStatus.INTERNAL_SERVER_ERROR: HttpStatus.OK; 
    	return new ResponseEntity<>(empName, status);
    }
     

}

