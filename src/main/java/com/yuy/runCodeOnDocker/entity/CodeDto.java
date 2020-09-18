package com.yuy.runCodeOnDocker.entity;

public class CodeDto {

    // 代码类型
    private String codeType;

    // 代码
    private String content;

    // 用户id
    private Integer memberId;

    // 用户名
    private String username;

    // 密码
    private String password;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getCodeType() {
        return codeType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
