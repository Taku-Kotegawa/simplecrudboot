package com.example.simplecrud.app.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserForm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ユーザID
     */
    @NotBlank
    @Size(max = 20)
    private String uid;

    /**
     * 氏名
     */
    @NotBlank
    @Size(max = 60)
    private String name;

    /**
     * パスワード
     */
    @NotBlank
    @Size(max = 255)
    private String pass;

    /**
     * メール
     */
    @NotBlank
    @Size(max = 255)
    @Email
    private String mail;

    /**
     * コメント
     */
    @Size(max = 1000)
    private String comment;
}