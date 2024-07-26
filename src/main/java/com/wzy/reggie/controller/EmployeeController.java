package com.wzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzy.reggie.common.R;
import com.wzy.reggie.pojo.Employee;
import com.wzy.reggie.service.EmployeeService;

import com.wzy.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeServiceImpl employeeServiceImpl;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        //MD5转码
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        /*
        这段代码使用了 MyBatis-Plus 框架中的 LambdaQueryWrapper 类，用于构建数据库查询条件。让我解释一下：
        LambdaQueryWrapper 是 MyBatis-Plus 提供的一个查询条件构造器，它允许你使用 Lambda 表达式来构建查询条件，使代码更加简洁和易读。
        在你的代码中，queryWrapper.eq(Employee::getUsername, employee.getUsername()) 表示查询条件为：Employee 实体类的 username 字段等于 employee 对象的 username 值。
         */
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //employee数据库表 拥有唯一约束，故使用getone方法
        Employee emp=employeeService.getOne(queryWrapper);


        //3、如果没有查询到则返回登录失败结果
        if(emp==null){
            return R.error("帐号不存在，登录失败");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误，登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus()==0){
            return R.error("此账号已被封号");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }
    //用户退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> addsave(HttpServletRequest request,@RequestBody Employee employee){
       log.info("新增员工：{}",employee.toString());
       //设置初始密码为123456，但需要md5码加密处理
       employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//       employee.setCreateTime(LocalDateTime.now());
//       employee.setUpdateTime(LocalDateTime.now());
//
//       Long empID=(Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);
//        mybagis-plus的sava方法
        employeeService.save(employee);
       return R.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     *@param
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")//前端请求方式是get请求
    public R<Page> page( int page, int pageSize,String name){
        log.info("page{},pagesize={},nanme{}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo =new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Employee::getCreateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        long id=Thread.currentThread().getId();
        log.info("线程id为:{}",id);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);

        return R.success("员工信息更新成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getByid(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee==null){
            return R.error("没有找到对应员工");
        }else
        return R.success(employee);
    }

}
