package com.Gson;

import java.text.MessageFormat;

import com.google.gson.Gson;

public class TestObjectToJson {
    public static void main(String[] args) {  
        Gson gson=new Gson();  
        Person person=new Person();  
        person.setId(1);  
        person.setName("zhangsan");  
        person.setAge(30);  
        String str=gson.toJson(person);  
        System.out.println(str);  
        
        
        String message = "oh, {0} is a pig";    
        MessageFormat messageFormat = new MessageFormat(message);    
        Object[] array = new Object[]{"ZhangSan"};    
        String value = messageFormat.format(array);    
            
        System.out.println(value);
}  

}
