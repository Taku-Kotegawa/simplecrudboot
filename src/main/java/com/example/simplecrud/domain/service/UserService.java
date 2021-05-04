package com.example.simplecrud.domain.service;

import com.example.simplecrud.common.exception.DuplicateKeyBusinessException;
import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.model.UserExample;
import org.springframework.dao.DataIntegrityViolationException;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import java.util.List;

public interface UserService {

    /**
     * ユーザを一件取得する
     *
     * @param uid ユーザID
     * @return Userエンティティ
     * @throws ResourceNotFoundException 指定されたユーザIDが存在しない場合
     * @throws IllegalArgumentException  引数がnull
     */
    User findOneByPrimaryKey(String uid);

    /**
     * ユーザの一覧を取得する。
     *
     * @param UserExample Exampleクラス
     * @return Userエンティティのリスト(0件の場合は空のリスト)
     * @throws IllegalArgumentException 引数がnull
     */
    List<User> findAllByExample(UserExample UserExample);

    /**
     * Userエンティティを新規に登録する。
     *
     * @param user 登録するUserエンティティ
     * @return 登録後のUserエンティティ(自動設定項目に値がセットされた状態)
     * @throws DuplicateKeyBusinessException   既に登録済みのユーザIDで保存しようとした場合
     * @throws IllegalArgumentException        引数がnull
     * @throws DataIntegrityViolationException DB更新に失敗した場合
     */
    User create(User user);

    /**
     * Userエンティティを更新する。
     *
     * @param user 更新するUserエンティティ
     * @return 更新後のUserエンティティ
     * @throws IllegalArgumentException        引数がnull, 主キーがnull
     * @throws DataIntegrityViolationException DB更新に失敗した場合
     */
    User update(User user);

    /**
     * Userエンティティを削除する。
     *
     * @param uid ユーザID
     * @throws IllegalArgumentException 引数がnull
     */
    void delete(String uid);

}
