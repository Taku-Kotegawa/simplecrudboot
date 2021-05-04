package com.example.simplecrud.app.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * このテストにはローカルDBが必要です。
 * 細かなエラー制御のテストは「UserControllerMockTest」の方で実施。このテストでは主に正常系の結合テストを行う。
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    UserController target;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    public void contextLoads() throws Exception {
        assertThat(target).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    /**
     * 対象: /usr/list
     * 引数
     *  - メソッド
     *  - パラメータ
     *  - ポストデータ
     *
     * 検証項目:
     *  - ビュー(もしくはリダイレクト先)
     *  - HTTPステータス
     *  - エラーの有無
     *  - モデルの内容
     */

    @Nested
    class list {

        @Test
        @DisplayName("[正]一覧画面の表示(GET)")
        void test001() throws Exception {
            mockMvc.perform(get("/user/list"))
                    .andExpect(status().is(200))
                    .andExpect(view().name("user/list"))
                    .andExpect(model().hasNoErrors());
        }

        @Test
        @DisplayName("[正]一覧画面の表示(POST)")
        void test002() throws Exception {
            mockMvc.perform(post("/user/list"))
                    .andExpect(status().is(200))
                    .andExpect(view().name("user/list"))
                    .andExpect(model().hasNoErrors());
        }
    }

    @Nested
    class createForm {
        @Test
        @DisplayName("[正]一覧画面の表示(GET)")
        void test001() throws Exception {
            mockMvc.perform(get("/user/create?form"))
                    .andExpect(status().is(200))
                    .andExpect(view().name("user/createForm"))
                    .andExpect(model().hasNoErrors());
        }

        @Test
        @DisplayName("[正]一覧画面の表示(POST)")
        void test002() throws Exception {
            mockMvc.perform(post("/user/create?form"))
                    .andExpect(status().is(200))
                    .andExpect(view().name("user/createForm"))
                    .andExpect(model().hasNoErrors());
        }


    }

    @Nested
    class createConfirm {
    }

    @Nested
    class createRedo {
    }

    @Nested
    class create {
    }

    @Nested
    class createComplete {
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