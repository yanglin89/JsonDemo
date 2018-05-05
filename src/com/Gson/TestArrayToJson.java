package com.Gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class TestArrayToJson {
    public static void main(String[] args) {  
    List<Person> list=new ArrayList<Person>();  
            Gson gson=new Gson();  
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
            String str=gson.toJson(list);  
            System.out.println(str);  
    }  

}
