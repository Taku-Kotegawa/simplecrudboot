package com.example.simplecrud.app.user;

import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.model.UserExample;
import com.example.simplecrud.domain.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * このテストにはローカルDBが必要です。
 * チュートリアルのため、テストケースは必要十分なケースは実装されていません。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    UserController target;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

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


    @Test
    @DisplayName("[正]DIされるべきオブジェクトが準備できている")
    public void contextLoads() throws Exception {
        assertThat(target).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    /**
     * POSTデータの準備
     *
     * @param id 主キーに設定する値
     * @return Map
     */
    private MultiValueMap makePostData(String id) {
        MultiValueMap<String, String> post = new LinkedMultiValueMap<>();
        post.add("uid", id);
        post.add("name", "name" + id);
        post.add("pass", "pass" + id);
        post.add("mail", "test@stnet.co.jp");
        post.add("comment", "comment" + id);

        return post;
    }

    /**
     * 対象: /usr/list
     * 引数
     * - メソッド
     * - パラメータ
     * - ポストデータ
     * <p>
     * 検証項目:
     * - ビュー(もしくはリダイレクト先)
     * - HTTPステータス
     * - エラーの有無
     * - モデルの内容
     */

    @Nested
    class list {

        @Test
        @DisplayName("[正]一覧画面が表示される(GET)")
        void test001() throws Exception {
            mockMvc.perform(get("/user/list"))
                    .andExpect(status().isOk()) //200
                    .andExpect(view().name("user/list"))
                    .andExpect(model().hasNoErrors());
        }

        @Test
        @DisplayName("[異]POSTはエラー")
        void test101() throws Exception {
            mockMvc.perform(post("/user/list"))
                    .andExpect(status().isOk()) //200
                    .andExpect(view().name("user/list"))
                    .andExpect(model().hasNoErrors());
        }
    }

    @Nested
    class createForm {
        @Test
        @DisplayName("[正]新規登録画面が表示される(GET)")
        void test001() throws Exception {
            mockMvc.perform(get("/user/create?form"))
                    .andExpect(status().isOk()) //200
                    .andExpect(view().name("user/createForm"))
                    .andExpect(model().hasNoErrors());
        }

        @Test
        @DisplayName("[正]新規登録画面が表示される(POST)")
        void test101() throws Exception {
            mockMvc.perform(post("/user/create?form"))
                    .andExpect(status().isOk()) //200
                    .andExpect(view().name("user/createForm"))
                    .andExpect(model().hasNoErrors());
        }
    }

    @Nested
    class createConfirm {

        @Test
        @DisplayName("[正]確認画面が表示される")
        void test001() throws Exception {
            // 実行
            MvcResult result = mockMvc.perform(post("/user/create?confirm")
                    .params(makePostData("1")))
                    // 検証
                    .andExpect(status().isOk()) //200
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("user/createConfirm"))
                    .andReturn();
        }

        @Test
        @DisplayName("[異]入力チェックでエラーになる(ユーザID=必須)")
        void test101() throws Exception {

            MultiValueMap<String, String> postData = makePostData("1");
            postData.remove("uid");

            // 実行
            MvcResult result = mockMvc.perform(post("/user/create?confirm").params(postData))
                    // 検証
                    .andExpect(model().hasErrors())
                    .andExpect(status().isOk()) //200
                    .andExpect(view().name("user/createForm"))
                    .andReturn();

            System.out.println(result.getModelAndView().getModel().values());
        }
    }

    @Nested
    class createRedo {
        @Test
        @DisplayName("[正](戻るボタン押下など)確認画面から登録画面に戻ることができる")
        void test001() throws Exception {
            // 実行
            MvcResult result = mockMvc.perform(post("/user/create?redo").params(makePostData("1")))
                    // 検証
                    .andExpect(status().isOk()) //200
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("user/createForm"))
                    .andReturn();
        }
    }

    @Nested
    class create {
        @Test
        @DisplayName("[正]登録処理が行われ、完了画面にリダイレクトされる")
        void test001() throws Exception {
            // 実行
            MvcResult result = mockMvc.perform(post("/user/create").params(makePostData("1")))
                    // 検証
                    .andExpect(status().isFound()) //302
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("redirect:/user/create?complete"))
                    .andReturn();
        }
    }

    @Nested
    class createComplete {
        @Test
        @DisplayName("[正]登録完了画面が表示される")
        void test001() throws Exception {
            // 実行
            MvcResult result = mockMvc.perform(get("/user/create?complete"))
                    // 検証
                    .andExpect(status().isOk()) //200
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("user/createComplete"))
                    .andReturn();
        }
    }

    @Nested
    class updateForm {
    }

    @Nested
    class updateConfirm {
    }

    @Nested
    class updateRedo {
    }

    @Nested
    class update {
    }

    @Nested
    class updateComplete {
    }

    @Nested
    class delete {
    }

    @Nested
    class deleteComplete {
    }

    @Nested
    class detail {
    }
}