package com.example.rqchallenge.employees.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {

	private String id;
	private String name;
	private int salary;
	private int age;
	private String profileImage;
	
	public Employee(){}
	
	Employee(String id, String name, int salary, int age, String profileImage ){
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.age = age;
		this.profileImage = profileImage;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("employee_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("employee_salary")
	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	@JsonProperty("employee_age")
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@JsonProperty("profile_image")
	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
	        return false;
	    }
		
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    
	    Employee emp = (Employee) obj;
	    if( !this.id.equals(emp.getId()) ||
	    	!this.name.equals(emp.getName()) ||
	    	this.age != emp.getAge() ||
	    	!this.profileImage.equals(emp.getProfileImage())
	      ) {
	    	return false;
	    }
	    
	    return true;
		
	}
	
	@Override
	public String toString() {
		return "Id: " + this.id +  "  Name: " + this.name + " Age: " + this.age + " Salary: " + this.salary;
	}

	
	
}
