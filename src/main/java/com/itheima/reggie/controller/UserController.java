package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController {
@Autowired
    private UserService userService;

@PostMapping("/sendMsg")
public R<String> sendMsg(@RequestBody User user, HttpSession session){
    String phone = user.getPhone();
    if (StringUtils.isNotEmpty(phone)) {
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
       // SMSUtils.sendMessage("","",phone,code);
  log.info("code={}",code);
  session.setAttribute(phone,code);
  return R.success("手机验证码短信发送成功");
    }

    return R.error("短信发送失败");
}




    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object attribute = session.getAttribute(phone);
        if (attribute!=null && attribute.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user==null) {
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
return R.success(user);
        }
return R.error("登录失败");
    }




}
