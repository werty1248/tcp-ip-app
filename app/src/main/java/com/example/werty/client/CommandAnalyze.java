package com.example.werty.client;

public class CommandAnalyze {
    public String Chat(String str)
    {
        String[] split = str.split(" ",2);
        if(split.length<2)
            return null;
        if(!split[0].equals("chat"))
            return null;
        return split[1];
    }
}