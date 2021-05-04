package com.example.simplecrud.domain.repository;
import java.time.LocalDateTime;

import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.model.UserExample;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * このテストはローカルDBが必要です。
 * また、各テストの前にテーブルのデータを削除するため、多量のデータがあるとパフォーマンスが著しく低下します。
 * なお、テスト終了後にロールバックするため、データは消えません。
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//application.propertiesの設定に従う
class UserRepositoryTest {

    @Autowired
    UserRepository target;

    /**
     * テーブルにデータを挿入する。
     * @param users Userエンティティ(カンマ区切りで複数指定可)
     */
    private void insertIntoTable(User... users) {
        for (User user : users) {
            target.insert(user);
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
        user.setCreatedAt(LocalDateTime.of(2021,4,1,12,34,56));
        user.setChangedAt(LocalDateTime.of(2021,4,1,12,34,56));
        user.setComment(rightPad("mail" + id, 1000, "0"));
        return user;
    }

    /**
     * Userエンティティを全件削除する。
     */
    private void deleteUserAll() {
        target.deleteByExample(new UserExample());
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
    class insert {
        @Test
        @DisplayName("[正]１件のデータを挿入できる")
        void test001() {
            // 準備
            User expected = createEntity("1");

            // 実行
            int actual = target.insert(expected);

            // 検証
            assertThat(actual).isEqualTo(1);
            User actualUser = target.selectByPrimaryKey(expected.getUid());
            System.out.println(actualUser);

        }
    }

    @Nested
    class selectByPrimaryKey {
        @Test
        @DisplayName("[正]主キーでデータを取得できる")
        void test001() {
            // 準備
            User expected = createEntity("2");
            insertIntoTable(
                    createEntity("1"),
                    expected,
                    createEntity("3")
            );

            // 実行
            User actual = target.selectByPrimaryKey(expected.getUid());

            // 検証
            assertThat(actual.getUid()).isEqualTo(expected.getUid());
            assertThat(actual.getName()).isEqualTo(expected.getName());
            assertThat(actual.getPass()).isEqualTo(expected.getPass());
            assertThat(actual.getMail()).isEqualTo(expected.getMail());
            assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
            assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
            assertThat(actual.getChangedAt()).isEqualTo(expected.getChangedAt());
            assertThat(actual.getComment()).isEqualTo(expected.getComment());

            // 多分上と同じ結果が得られる。
            assertThat(actual).isEqualTo(expected);
        }
    }



    @Nested
    class updateByPrimaryKey {
        @Test
        @DisplayName("[正]主キーでデータを更新できる")
        void test001() {
            // 準備
            User expected = createEntity("4");
            insertIntoTable(
                    expected
            );

            // 実行
            expected.setName("changed");
            int count = target.updateByPrimaryKey(expected);

            // 検証
            assertThat(count).isEqualTo(1);
            User actual = target.selectByPrimaryKey(expected.getUid());
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class deleteByPrimaryKey {
        @Test
        @DisplayName("[正]主キーでデータを削除できる")
        void test001() {
            // 準備
            User expected = createEntity("5");
            insertIntoTable(
                    expected
            );

            // 実行
            int count = target.deleteByPrimaryKey(expected.getUid());

            // 検証
            User actual = target.selectByPrimaryKey(expected.getUid());
            assertThat(count).isEqualTo(1);
            assertThat(actual).isEqualTo(null);
        }
    }

    // 以下のメソッドのテスト実装を省略(MyBatisGeneratorをある程度信用しよう)

    @Nested
    class countByExample {
    }

    @Nested
    class selectByExample {
    }

    @Nested
    class deleteByExample {
    }

    @Nested
    class insertSelective {
    }

    @Nested
    class selectByExampleWithRowbounds {
    }

    @Nested
    class updateByExampleSelective {
    }

    @Nested
    class updateByExample {
    }

    @Nested
    class updateByPrimaryKeySelective {
    }

}