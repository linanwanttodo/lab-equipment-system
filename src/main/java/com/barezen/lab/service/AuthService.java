package com.barezen.lab.service;

import com.barezen.lab.dao.UserDao;
import com.barezen.lab.entity.User;
import com.barezen.lab.exception.ServiceException;
import org.mindrot.jbcrypt.BCrypt;

/** 认证业务：登录校验、注册校验。密码使用 BCrypt 哈希存储与比对 */
public class AuthService {

    private final UserDao userDao = new UserDao();

    /** 登录：用户名 + 密码（BCrypt 比对），成功返回用户 */
    public User login(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new ServiceException("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new ServiceException("密码不能为空");
        }
        User user = userDao.findByUsername(username);
        boolean ok;
        try {
            ok = user != null && BCrypt.checkpw(password, user.getPassword());
        } catch (IllegalArgumentException e) {
            // 库中哈希格式异常（如版本前缀不兼容）按凭据错误处理，避免 500
            ok = false;
        }
        if (!ok) {
            throw new ServiceException("用户名或密码错误");
        }
        user.setPassword(null);
        return user;
    }

    /** 注册：默认学生角色，用户名唯一，密码 6 位以上，姓名选填（逾期提醒联系人显示用） */
    public void register(String username, String name, String password, String password2, String contact) {
        if (username == null || username.trim().length() < 3 || username.trim().length() > 20) {
            throw new ServiceException("用户名长度须为 3-20 个字符");
        }
        if (password == null || password.length() < 6) {
            throw new ServiceException("密码长度至少 6 位");
        }
        if (!password.equals(password2)) {
            throw new ServiceException("两次输入的密码不一致");
        }
        if (userDao.findByUsername(username.trim()) != null) {
            throw new ServiceException("用户名已存在");
        }
        User u = new User();
        u.setUsername(username.trim());
        u.setName(name == null ? null : name.trim());
        u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        u.setRole("STUDENT");
        u.setContact(contact);
        userDao.save(u);
    }
}
