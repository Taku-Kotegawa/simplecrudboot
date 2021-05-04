package com.example.simplecrud.domain.service;

import com.example.simplecrud.common.exception.DuplicateKeyBusinessException;
import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.model.UserExample;
import com.example.simplecrud.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final String MSG_USER_NOT_FOUND = "User not found.";
    private final String MSG_USER_ALREADY_REGISTERED = "User already registered.";

    @Inject
    UserRepository userRepository;

    @Override
    public User findOneByPrimaryKey(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException();
        }
        return findOne(uid);
    }

    @Override
    public List<User> findAllByExample(UserExample userExample) {
        // 簡易な入力チェック
        if (userExample == null) {
            throw new IllegalArgumentException();
        }

        return userRepository.selectByExample(userExample);
    }

    @Override
    public User create(User user) {
        // 簡易な入力チェック
        if (user == null || user.getUid() == null) {
            throw new IllegalArgumentException();
        }

        // 重複チェック
        if (userRepository.selectByPrimaryKey(user.getUid()) != null) {
            throw new DuplicateKeyBusinessException(ResultMessages.error().add(MSG_USER_ALREADY_REGISTERED));
        }

        user.setStatus(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setChangedAt(now);

        userRepository.insert(user);
        return findOne(user.getUid());
    }

    @Override
    public User update(User user) {
        // 簡易な入力チェック
        if (user == null || user.getUid() == null) {
            throw new IllegalArgumentException();
        }

        user.setStatus(findOne(user.getUid()).getStatus());
        user.setChangedAt(LocalDateTime.now());

        int count = userRepository.updateByPrimaryKey(user);
        if (count == 0) {
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND);
        }

        return findOne(user.getUid());
    }

    @Override
    public void delete(String uid) {
        // 簡易な入力チェック
        if (uid == null) {
            throw new IllegalArgumentException();
        }

        int count = userRepository.deleteByPrimaryKey(uid);
        if (count == 0) {
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND);
        }

    }

    /**
     * 取得できない場合は、ResourceNotFoundExceptionをスローする。
     */
    private User findOne(String uid) {
        User user = userRepository.selectByPrimaryKey(uid);
        if (user == null) {
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND);
        } else {
            return user;
        }
    }

}