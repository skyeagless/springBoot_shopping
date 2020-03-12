package com.skyeagle.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterForm {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
}


//@NotEmpty用于集合（数组等）
//@NotNull
//@NotBlank用于String，可判断空格