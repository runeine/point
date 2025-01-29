package com.example.point.entity;


public enum TransactionTypeDetail {
    
    NORMAL_USE("0001", "일반 사용","USE", false),
    NORMAL_SAVE("0002", "일반 적립","SAVE", false),
    ADMIN_SAVE("0003", "관리자 적립","ADMIN_SAVE", true), 
    CANCEL_USE("0004","사용 취소","CANCEL_USE", false), 
    CANCEL_SAVE("0005","적립 취소","CANCEL_SAVE",false),;
    
    private final String code, description, type;
    private final boolean isAdmin;
    
    TransactionTypeDetail(String code, String description, String type, boolean isAdmin) {
        this.code = code;
        this.description = description;
        this.type = type;
        this.isAdmin = isAdmin;
    }
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    public String getType() {
        return type;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
}
