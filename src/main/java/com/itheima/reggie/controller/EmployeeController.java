package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
password=  DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);
        if (one==null) {
            return R.error("登录失败");
        }
        if (!one.getPassword().equals(password)) {
            return R.error("密码有误");
        }
        if (one.getStatus()==0) {
            return R.error("用户被禁用");
        }
request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }



    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
request.getSession().removeAttribute("employee");

return R.success("退出成功");
    }



@PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));


        employeeService.save(employee);
        return R.success("新增用户成功");
    }



    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
log.info("{},{},{}",page,pageSize,name);
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getUsername,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);
        return  R.success(pageInfo);
    }

@PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
log.info(employee.toString());
employee.setUpdateTime(LocalDateTime.now());
employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
employeeService.updateById(employee);
return R.success("修改成功");
    }


@GetMapping("/{id}")
public R<Employee> getById(@PathVariable Long id){
log.info("id查询");
    Employee byId = employeeService.getById(id);
    if (byId!=null) {
        return R.success(byId);
    }
    return R.error("未查询到");

}



}
