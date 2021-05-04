package com.example.simplecrud.app.user;

import com.example.simplecrud.common.exception.DuplicateKeyBusinessException;
import com.example.simplecrud.domain.model.User;
import com.example.simplecrud.domain.model.UserExample;
import com.example.simplecrud.domain.service.UserService;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "create", params = "confirm", method = RequestMethod.POST)
    public String createConfirm(@Validated UserForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return createRedo(form, model);
        }

        model.addAttribute("users", form);
        return "user/createConfirm";
    }

    @RequestMapping(value = "create", params = "redo", method = RequestMethod.POST)
    public String createRedo(UserForm form, Model model) {
        return "user/createForm";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@Validated UserForm form, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return createRedo(form, model);
        }

        try {
            User user = beanMapper.map(form, User.class);
            userService.create(user);
        } catch (DuplicateKeyBusinessException e) {
            // 本来はカスタムバリデーションを作ってキー重複をチェックすべき(確認ボタン押下時にチェック)だが、カスタムバリデーション
            // は後ほどの演習で学習するので、ひとまず、保存ボタン押下時にキー重複を検出して表示する仕組みで実装
            model.addAttribute(ResultMessages.error().add("user.duplicateKey"));
            return createRedo(form, model);
        }

        return "redirect:/user/create?complete";
    }

    @RequestMapping(value = "create", params = "complete")
    public String createComplete(Model model) {
        return "user/createComplete";
    }

    // ---------------- 編集 ---------------------------------------------------------

    @RequestMapping(value = "update", params = "form")
    public String updateForm(UserForm form, Model model) {

        User user = userService.findOneByPrimaryKey(form.getUid());
        beanMapper.map(user, form);

        return "user/updateForm";
    }

    @RequestMapping(value = "update", params = "confirm", method = RequestMethod.POST)
    public String updateConfirm(@Validated UserForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return updateRedo(form, model);
        }

        model.addAttribute("users", form);
        return "user/updateConfirm";
    }

    @RequestMapping(value = "update", params = "redo", method = RequestMethod.POST)
    public String updateRedo(UserForm form, Model model) {
        return "user/updateForm";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@Validated UserForm form, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return updateRedo(form, model);
        }

        User user = beanMapper.map(form, User.class);
        userService.update(user);

        return "redirect:/user/update?complete";
    }

    @RequestMapping(value = "update", params = "complete")
    public String updateComplete(Model model) {
        return "user/updateComplete";
    }

    // ---------------- 削除 ---------------------------------------------------------

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(UserForm form, BindingResult result, Model model, RedirectAttributes redirect) {
        // 削除時にはバリデーションを行わない。(idしか必要ないので)

        userService.delete(form.getUid());

        return "redirect:/user/delete?complete";
    }

    @RequestMapping(value = "delete", params = "complete")
    public String deleteComplete(Model model) {
        return "user/deleteComplete";
    }

    // ---------------- 参照 ---------------------------------------------------------

    @RequestMapping(value = "detail")
    public String detail(UserForm form, Model model) {
        // 参照時にはバリデーションを行わない。(idしか必要ないので)

        try {
            User user = userService.findOneByPrimaryKey(form.getUid());
            model.addAttribute("user", user);
        } catch (ResourceNotFoundException e) {
            model.addAttribute(e.getResultMessages());
            return list(model);
        }
        return "user/detail";
    }

}