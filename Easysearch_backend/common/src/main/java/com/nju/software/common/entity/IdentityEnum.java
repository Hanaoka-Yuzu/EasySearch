package com.nju.software.common.entity;

/**
 * @Description 用户角色枚举，用于鉴权
 * @Author wxy
 * @Date 2024/1/30
 **/
public enum IdentityEnum {
    ADMIN("ADMIN"),

    NORMAL("NORMAL");

    private String code;

    IdentityEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * 如果无匹配则返回 Normal
     * @param value
     * @return
     */
    public static IdentityEnum fromValue(String value) {
        for (IdentityEnum identity : IdentityEnum.values()) {
            if (identity.code.equalsIgnoreCase(value)) {
                return identity;
            }
        }
        return IdentityEnum.NORMAL;
    }

    @Override
    public String toString() {
        return "IdentityEnum{"
                + "code='" + code + '\''
                + "} "
                + super.toString();
    }
}
