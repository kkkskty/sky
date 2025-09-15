package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @ApiOperation("员工新增")
    @PostMapping
    public Result<EmployeeLoginVO> insert(@RequestBody EmployeeDTO employeeDTO) {
        log.info("员工新增：{}", employeeDTO);
        employeeService.insert(employeeDTO);
        //登录成功后，生成jwt令牌
        return Result.success();
    }

    @ApiOperation("员工查询")

    //Query与Json的格式区别


    @GetMapping("/page")
    public Result<PageResult> list(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工查询：{}", employeePageQueryDTO);
         PageResult pageResult= employeeService.list(employeePageQueryDTO);
        //登录成功后，生成jwt令牌
        return Result.success(pageResult);
    }


    ///路径参数要在路径中写明 并PathVariable注释
    @PostMapping("/status/{status}")
    @ApiOperation("员工状态启用/禁用")
    public Result list(@PathVariable Integer status,Long id) {
        log.info("员工状态：{}", status);
        employeeService.setStatus(status,id);
        //登录成功后，生成jwt令牌
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("员工详细信息展示")
    public Result<Employee> getEmpInfo(@PathVariable Long id) {
        log.info("员工id：{}", id);
         Employee employee=employeeService.getEmpInfo(id);
        //登录成功后，生成jwt令牌
        return Result.success(employee);
    }

    @PutMapping
    @ApiOperation("员工信息修改")
    public Result uodateEmpInfo(@RequestBody EmployeeDTO employeeDTO) {
        log.info("员工信息修改：{}", employeeDTO);
        employeeService.uodateEmpInfo(employeeDTO);
        //登录成功后，生成jwt令牌
        return Result.success();
    }

}
