package com.Gson;

import com.google.gson.Gson;

public class TestJsonToObject {
    public static void main(String[] args) {  
    String str="{\"id\":1,\"name\":\"zhangsan\",\"age\":30}";  
            Gson gson=new Gson();  
            Person person=gson.fromJson(str, Person.class);  
            System.out.println(person.toString());  
    }  

}
