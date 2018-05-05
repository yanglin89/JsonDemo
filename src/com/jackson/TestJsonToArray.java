package com.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.Gson.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsonToArray {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    List<Person> list=new ArrayList<Person>();  
        Person person=new Person();  
        person.setId(1);  
        person.setName("zhangsan");  
        person.setAge(30);  
        list.add(person);  
        Person person1=new Person();  
        person1.setId(2);  
        person1.setName("lisi");  
        person1.setAge(20);  
        list.add(person1);  
        Person person2=new Person();  
        person2.setId(3);  
        person2.setName("wangwu");  
        person2.setAge(30);  
        list.add(person2);  
        ObjectMapper objectMapper = new ObjectMapper();  
        try {  
           String json=objectMapper.writeValueAsString(list);  
           System.out.println(json);  
           Person[] persons=objectMapper.readValue(json, Person[].class);  
           for (Person info:persons) {  
               System.out.println(info.toString());  
           }  
       } catch (JsonProcessingException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       } catch (IOException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }  
	}

}
