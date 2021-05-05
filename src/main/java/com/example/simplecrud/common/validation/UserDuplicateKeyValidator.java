package com.example.simplecrud.common.validation;

import com.example.simplecrud.app.user.UserForm;
import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserDuplicateKeyValidator implements ConstraintValidator<UserDuplicateKey, String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public void initialize(UserDuplicateKey constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 同じIDのデータが未登録の場合、検査合格
        User user = userRepository.selectByPrimaryKey(value);
        return user == null;
    }
}
