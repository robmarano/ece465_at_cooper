package edu.cooper.ece465.concurrent;

public class Message {
    private String msg;
    
    public Message(String str){
        this.msg=str;
    }

    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String str) {
        this.msg=str;
    }
}