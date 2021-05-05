package com.example.simplecrud.app.user;

import com.example.simplecrud.common.exception.DuplicateKeyBusinessException;
import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.model.UserExample;
import com.example.simplecrud.domain.service.UserService;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;

@RequestMapping("user")
@Controller
public class UserController {

    @Autowired
    protected Mapper beanMapper;

    @Autowired
    UserService userService;

    // modelに自動的に追加(attributeNameは「userForm」(クラス名の先頭を小文字)で登録される。)
    @ModelAttribute
    public UserForm setUpUserForm() {
        return new UserForm();
    }

    // ---------------- 一覧 -----------------------------------------------------

    @RequestMapping("list")
    public String list(Model model) {

        // ページネーションは未実装です。
        model.addAttribute("userList", userService.findAllByExample(new UserExample()));

        return "user/list";
    }

    // ---------------- 新規登録 -----------------------------------------------------

    @RequestMapping(value = "create", params = "form")
    public String createForm(UserForm form, Model model) {
        return "user/createForm";
    }

    @PostMapping(value = "create", params = "confirm")
    public String createConfirm(@Validated UserForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return createRedo(form, model);
        }

        model.addAttribute("users", form);
        return "user/createConfirm";
    }

    @PostMapping(value = "create", params = "redo")
    public String createRedo(UserForm form, Model model) {
        return "user/createForm";
    }

    @PostMapping(value = "create")
    public String create(@Validated UserForm form, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return createRedo(form, model);
        }

        try {
            User user = beanMapper.map(form, User.class);
            userService.create(user);
        } catch (DuplicateKeyBusinessException e) {
            // 画面でもチェックしているが、念の為テーブル更新時のエラーを画面表示する。
            model.addAttribute(ResultMessages.error().add("user.duplicateKey", form.getUid()));
            return createRedo(form, model);
        } catch (DataIntegrityViolationException e) {
            // 本来ならば、SQLエラーは個別にTry-Catchせず、共通処理でエラー画面に遷移させたい。
            model.addAttribute(ResultMessages.error().add("common.sqlError", e.getMessage()));
            return createRedo(form, model);
        }

        return "redirect:/user/create?complete";
    }

    @RequestMapping(value = "create", params = "complete")
    public String createComplete(Model model) {
        return "user/createComplete";
    }

    // ---------------- 編集 ---------------------------------------------------------

    @PostMapping(value = "update", params = "form")
    public String updateForm(UserForm form, Model model) {

        User user = userService.findOneByPrimaryKey(form.getUid());
        beanMapper.map(user, form);

        return "user/updateForm";
    }

    @PostMapping(value = "update", params = "confirm")
    public String updateConfirm(@Validated UserForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return updateRedo(form, model);
        }

        model.addAttribute("users", form);
        return "user/updateConfirm";
    }

    @PostMapping(value = "update", params = "redo")
    public String updateRedo(UserForm form, Model model) {
        return "user/updateForm";
    }

    @PostMapping(value = "update")
    public String update(@Validated UserForm form, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return updateRedo(form, model);
        }

        try {
            User user = beanMapper.map(form, User.class);
            userService.update(user);
        } catch (DataIntegrityViolationException e) {
            // 本来ならば、SQLエラーは個別にTry-Catchせず、共通処理でエラー画面に遷移させたい。
            model.addAttribute(ResultMessages.error().add("common.sqlError", e.getMessage()));
            return updateRedo(form, model);
        }

        return "redirect:/user/update?complete";
    }

    @RequestMapping(value = "update", params = "complete")
    public String updateComplete(Model model) {
        return "user/updateComplete";
    }

    // ---------------- 削除 ---------------------------------------------------------

    @PostMapping(value = "delete")
    public String delete(UserForm form, BindingResult result, Model model, RedirectAttributes redirect) {
        // 削除時にはバリデーションを行わない。(idしか必要ないので)

        try {
            userService.delete(form.getUid());
        } catch (DataIntegrityViolationException e) {
            // 本来ならば、SQLエラーは個別にTry-Catchせず、共通処理でエラー画面に遷移させたい。
            model.addAttribute(ResultMessages.error().add("common.sqlError", e.getMessage()));
            return updateRedo(form, model);
        }

        return "redirect:/user/delete?complete";
    }

    @RequestMapping(value = "delete", params = "complete")
    public String deleteComplete(Model model) {
        return "user/deleteComplete";
    }

    // ---------------- 参照 ---------------------------------------------------------

    @RequestMapping(value = "detail/{uid}")
    public String detail(UserForm form, Model model, @PathVariable("uid") String uid) {
        // 参照時にはバリデーションを行わない。(idしか必要ないので)

        try {
            User user = userService.findOneByPrimaryKey(uid);
            model.addAttribute("user", user);
            form.setUid(uid);
        } catch (ResourceNotFoundException e) {
            model.addAttribute(ResultMessages.error().add("user.notFound", uid));
            return list(model);
        }
        return "user/detail";
    }

}