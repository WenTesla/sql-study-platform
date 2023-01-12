package com.example.demo.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.LoginBean;
import com.example.demo.bean.UserBean;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.enc.base.annotation.CurrentUser;
import com.example.enc.base.bean.CaptchaBean;
import com.example.enc.base.bean.LoginInfoBean;
import com.example.enc.base.component.ActiveComponent;
import com.example.enc.base.constant.Constant;
import com.example.enc.base.exception.BizException;
import com.example.enc.base.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 登录
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoginController {

    private final UserMapper userMapper;

    private final ActiveComponent activeComponent;


    @GetMapping
    public LoginInfoBean getLogin(HttpSession session) {

        return (LoginInfoBean) session.getAttribute(Constant.LOGIN_INFO);
    }

    @GetMapping("/captcha")
    public CaptchaBean getCaptcha(HttpSession session) {

        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(80, 40, 4, 2);
        session.setAttribute(Constant.CAPTCHA, captcha);

        return new CaptchaBean().setBase64(captcha.getImageBase64());
    }

    @PostMapping
    public LoginInfoBean register(@RequestBody @Valid UserBean param) {

        var entity = userMapper.selectOne(
                Wrappers.<UserEntity>lambdaQuery()
                        .eq(UserEntity::getUsername, param.getUsername())
        );
        if (entity != null) {
            throw new BizException("用户已经存在");
        }
        entity = BeanUtil.to(param, new UserEntity());
        userMapper.insert(entity);

        return BeanUtil.to(entity, new LoginInfoBean());
    }

    @PutMapping
    public LoginInfoBean login(@RequestBody LoginBean param, HttpSession session) {

        LineCaptcha captcha = (LineCaptcha) session.getAttribute(Constant.CAPTCHA);
        if (activeComponent.isProd() || "NONE".equals(param.getCaptcha())) {
            if (captcha == null || !captcha.verify(param.getCaptcha())) {
                throw new BizException("验证码错误");
            }
        }

        var entity = userMapper.selectOne(
                Wrappers.<UserEntity>lambdaQuery()
                        .eq(UserEntity::getUsername, param.getUsername())
        );
        if (entity == null) {
            throw new BizException("用户不存在");
        }
        if (!entity.getPassword().equals(param.getPassword())) {
            throw new BizException("密码错误");
        }

        var loginInfo = BeanUtil.to(entity, new LoginInfoBean());
        session.setAttribute(Constant.LOGIN_INFO, loginInfo);

        return loginInfo;
    }

    @DeleteMapping
    public LoginInfoBean logout(@CurrentUser LoginInfoBean loginInfoBean, HttpSession session) {

        session.removeAttribute(Constant.LOGIN_INFO);
        return loginInfoBean;
    }

}
