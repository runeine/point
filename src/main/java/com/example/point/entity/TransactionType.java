package com.example.point.entity;


public enum TransactionType {
    
    SAVE("SAVE"),
    USE("USE");

    private final String code;
    
    TransactionType(String code){
        this.code=code;
    }

    public String getCode() {
        return code;
    }
        
}
