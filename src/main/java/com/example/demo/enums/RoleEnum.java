package com.example.demo.enums;

import com.example.enc.base.enums.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoleEnum implements BaseEnum<String> {
    
    COMMON("COMMON", "普通"),
    ADMIN("ADMIN", "管理员"),
    ;

    private final String code;
    private final String message;

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}