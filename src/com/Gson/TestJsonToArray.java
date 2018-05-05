package com.Gson;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestJsonToArray {
    public static void main(String[] args) {  
    String str = "[{\"id\":1,\"name\":\"zhangsan\",\"age\":30},{\"id\":2,\"name\":\"lisi\",\"age\":20}]";  
            Gson gson = new Gson();  
            
            //Type是java里的reflect包的Type ，TypeToken 是google提供的一个解析Json数据的类库中一个类
            Type type = new TypeToken<List<Person>>() {  
            }.getType();  
            List<Person> list = gson.fromJson(str, type);  
            for (Person person : list) {  
                System.out.println(person.toString());  
            }  
    }  

}
