package com.jackson;

import java.io.IOException;

import com.Gson.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsonToObject {
	public static void main(String[] args) {
	    Person person=new Person();  
        person.setId(1);  
        person.setName("zhangsan");  
        person.setAge(30);  
        ObjectMapper objectMapper = new ObjectMapper();  
        try {  
           String json=objectMapper.writeValueAsString(person);  
           System.out.println(json);  
           Person info=objectMapper.readValue(json, Person.class);  
           System.out.println(info.toString());  
       } catch (JsonProcessingException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       } catch (IOException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }  
   }  
	}

