package com.example.simplecrud.domain.service;

import com.example.simplecrud.common.exception.DuplicateKeyBusinessException;
import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.model.UserExample;
import com.example.simplecrud.domain.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * このテストはサービスとリポジトリーの結合テストです。(サービス単体のモックテストは行いません。)
 * そのため、テスト実行にはローカルDBが必要です。
 * 毎テスト毎にUserエンティティを全件削除するため、多量のデータが保存されている場合パフォーマンスが悪化します。
 */
@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceImplTest {

    @Autowired
    UserServiceImpl target;

    @Autowired
    UserRepository userRepository;

    /**
     * テーブルにデータを挿入する。
     *
     * @param users Userエンティティ(カンマ区切りで複数指定可)
     */
    private void insertIntoTable(User... users) {
        for (User user : users) {
            userRepository.insert(user);
        }
    }

    /**
     * テストエンティティの作成(各フィールドに最大桁数のダミーデータをセット)
     *
     * @param id テストデータを一意に特定する番号
     * @return Userエンティティ
     */
    private User createEntity(String id) {
        User user = new User();
        user.setUid(rightPad(id, 60, "0"));
        user.setName(rightPad("name" + id, 60, "0"));
        user.setPass(rightPad("pass" + id, 255, "0"));
        user.setMail(rightPad("mail" + id, 254, "0"));
        user.setStatus(false);
        user.setCreatedAt(LocalDateTime.of(2021, 4, 1, 12, 34, 56));
        user.setChangedAt(LocalDateTime.of(2021, 4, 1, 12, 34, 56));
        user.setComment(rightPad("mail" + id, 1000, "0"));
        return user;
    }

    /**
     * Userエンティティを全件削除する。
     */
    private void deleteUserAll() {
        userRepository.deleteByExample(new UserExample());
    }


    @BeforeEach
    void setUp() {
        // 毎回データを全件削除する。(テスト後にロールバックする)
        deleteUserAll();
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    class findOneByPrimaryKey {
        @Test
        @DisplayName("[正]主キーでデータを取得できる")
        void test001() {
            // 準備
            User expected = createEntity("1");
            insertIntoTable(expected);

            // 実行
            User actual = target.findOneByPrimaryKey(expected.getUid());

            // 検証
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("[異]指定したIDのデータが見つからない場合、ResourceNotFound例外を投げる")
        void test101() {
            // 準備

            // 実行
            assertThatThrownBy(() -> target.findOneByPrimaryKey("not exist"))
                    // 検証
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("[異]引数にNullを渡すと、IllegalArgumentExceptionを投げる")
        void test102() {
            // 準備

            // 実行
            assertThatThrownBy(() -> target.findOneByPrimaryKey(null))
                    // 検証
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class create {
        @Test
        @DisplayName("[正]データを新規登録できる(最大桁数)")
        void test001() {
            // 準備
            User expected = createEntity("1");

            // 実行
            User actual = target.create(expected);

            // 検証
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("[正]データを新規登録できる(必須項目以外Null)")
        void test002() {
            // 準備
            User expected = createEntity("1");
            expected.setComment(null);

            // 実行
            User actual = target.create(expected);

            // 検証
            assertThat(actual).isEqualTo(expected);
        }


        @Test
        @DisplayName("[異]キーの重複があると、DuplicateKeyBusinessExceptionを投げる")
        void test101() {
            // 準備
            User expected = createEntity("1");
            insertIntoTable(expected);

            assertThatThrownBy(() -> {
                // 実行
                target.create(expected);
            })
                    // 検証
                    .isInstanceOf(DuplicateKeyBusinessException.class);
        }

        @Test
        @DisplayName("[異]Nullを渡すと、IllegalArgumentExceptionを投げる")
        void test102() {
            assertThatThrownBy(() -> {
                // 実行
                target.create(null);
            })
                    // 検証
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[異]主キーをNullで渡すと、IllegalArgumentExceptionを投げる")
        void test103() {
            User expected = createEntity("1");
            expected.setUid(null);

            assertThatThrownBy(() -> {
                // 実行
                target.create(expected);
            })
                    // 検証
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[異]テーブルの制約違反を起こすと、DataIntegrityViolationExceptionを投げる(NOT NULL制約)")
        void test104() {
            User expected = createEntity("1");
            expected.setName(null);

            assertThatThrownBy(() -> {
                // 実行
                target.create(expected);
            })
                    // 検証
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("[異]テーブルの制約違反を起こすと、DataIntegrityViolationExceptionを投げる(桁数制約)")
        void test105() {
            User expected = createEntity("1");
            expected.setName(expected.getName() + "1");

            assertThatThrownBy(() -> {
                // 実行
                target.create(expected);
            })
                    // 検証
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }


    @Nested
    class update {
        @Test
        @DisplayName("[正]データを更新できる(最大桁数)")
        void test001() {
            // 準備
            User expected = createEntity("1");
            insertIntoTable(expected);
            expected.setName("changed");

            // 実行
            User actual = target.update(expected);

            // 検証
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("[異]存在しないIDを指定するとResourceNotFoundExceptionを投げる")
        void test101() {
            // 準備
            User notExist = createEntity("not exist");

            assertThatThrownBy(() -> {
                // 実行
                target.update(notExist);
            })
                    // 検証
                    .isInstanceOf(ResourceNotFoundException.class);
        }


        @Test
        @DisplayName("[異]Nullを渡すと、IllegalArgumentExceptionを投げる")
        void test102() {
            assertThatThrownBy(() -> {
                // 実行
                target.update(null);
            })
                    // 検証
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[異]主キーをNullで渡すと、IllegalArgumentExceptionを投げる")
        void test103() {
            User expected = createEntity("1");
            expected.setUid(null);

            assertThatThrownBy(() -> {
                // 実行
                target.update(expected);
            })
                    // 検証
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[異]テーブルの制約違反を起こすと、DataIntegrityViolationExceptionを投げる(NOT NULL制約)")
        void test104() {
            User expected = createEntity("1");
            insertIntoTable(expected);
            expected.setName(null);

            assertThatThrownBy(() -> {
                // 実行
                target.update(expected);
            })
                    // 検証
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("[異]テーブルの制約違反を起こすと、DataIntegrityViolationExceptionを投げる(桁数制約)")
        void test105() {
            User expected = createEntity("1");
            insertIntoTable(expected);
            expected.setName(expected.getName() + "1");

            assertThatThrownBy(() -> {
                // 実行
                target.update(expected);
            })
                    // 検証
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class delete {
        @Test
        @DisplayName("[正]データを削除できる")
        void test001() {
            // 準備
            User expected = createEntity("1");
            insertIntoTable(expected);

            // 実行
            target.delete(expected.getUid());

            // 検証
            User actual = userRepository.selectByPrimaryKey(expected.getUid());
            assertThat(actual).isEqualTo(null);
        }

        @Test
        @DisplayName("[異]存在しないIDを指定するとResourceNotFoundExceptionを投げる")
        void test101() {
            // 準備

            assertThatThrownBy(() -> {
                // 実行
                target.delete("not exist");
            })
                    // 検証
                    .isInstanceOf(ResourceNotFoundException.class);
        }


        @Test
        @DisplayName("[異]Nullを渡すと、IllegalArgumentExceptionを投げる")
        void test102() {
            assertThatThrownBy(() -> {
                // 実行
                target.delete(null);
            })
                    // 検証
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Nested
    class findAllByExample {
        // ExampleクラスはMyBatisGeneratorにより自動生成されているので、精緻な動作検証は行わない。

        @Test
        @DisplayName("[正]検索条件を指定しない場合、テーブルから全件取得できる")
        void test001() {
            // 準備
            insertIntoTable(
                    createEntity("1"),
                    createEntity("2"),
                    createEntity("3")
            );

            // 実行
            List<User> actual = target.findAllByExample(new UserExample());

            // 検証
            assertThat(actual).size().isEqualTo(3);
        }

        @Test
        @DisplayName("[正]取得件数が0件の場合、空の配列を返す。")
        void test002() {
            // 準備

            // 実行
            List<User> actual = target.findAllByExample(new UserExample());

            // 検証
            assertThat(actual).size().isEqualTo(0);
        }

        @Test
        @DisplayName("[異]Nullを渡すと、IllegalArgumentExceptionを投げる")
        void test101() {
            assertThatThrownBy(() -> {
                // 実行
                target.findAllByExample(null);
            })
                    // 検証
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

}