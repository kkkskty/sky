package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        // TODO 密码进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

     public void insert(EmployeeDTO employeeDTO)
    {
        Employee employee= new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);//前拷贝后 要求属性名已知
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setCreateTime(LocalDateTime.now()) ;
        employee.setUpdateTime(LocalDateTime.now()) ;
        employee.setStatus(StatusConstant.ENABLE) ;
        // ToDO Threadlocal
        employee.setCreateUser(BaseContext.getCurrentId()) ;//long类型的1
        employee.setUpdateUser(BaseContext.getCurrentId()) ;
        employeeMapper.insert(employee);


    }


    @Override
    public PageResult list(EmployeePageQueryDTO employeePageQueryDTO) {
        //TODO pagehelper和page是要一起使用的 应该看看原理
        //pagehelper在sql发送到数据库前 由MyBatis 拦截器利用Threadlocal的page的参数对sql进行加工，然后我写的page只是一个返回结果的承接
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.list(employeePageQueryDTO.getName());
      //  List<Employee> list = employeeMapper.list(employeePageQueryDTO.getName());
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void setStatus(Integer status,Long id) {
        Employee employee=Employee.builder()
                .status(status).
                id(id)
                .build();
        employeeMapper.update(employee);
    }


    @Override
    public Employee getEmpInfo(Long id) {
        Employee employee = employeeMapper.getEmpInfo(id);
        employee.setPassword("****");
        return employee;
    }

    @Override
    public void uodateEmpInfo(EmployeeDTO employeeDTO) {
//        Employee employee= Employee.builder()
//                .name(employeeDTO.getName())
//                .phone(employeeDTO.getPhone())
//                .username(employeeDTO.getUsername())
//                .sex(employeeDTO.getSex())
//                .idNumber(employeeDTO.getIdNumber())
//                .updateTime(LocalDateTime.now())
//                .build();
        Employee employee =new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setUpdateTime(LocalDateTime.now()) ;
        employee.setUpdateUser(BaseContext.getCurrentId()) ;
        employeeMapper.update(employee);
    }
}
