package com.example.provider.service.user;

import com.example.common.annotations.DataSource;
import com.example.common.config.mysql.DataSourceType;
import com.example.common.entity.User;
import com.example.provider.mapper.user.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @DataSource(DataSourceType.SLAVE)
    public User getUserById(BigInteger id) {
        return userMapper.getUserById(id);
    }

    @DataSource(DataSourceType.MASTER)
    @Transactional
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @DataSource(DataSourceType.MASTER)
    @Transactional
    public int update(User user) {
        return userMapper.update(user);
    }

    @DataSource(DataSourceType.SLAVE)
    public User getUserByPhone(String phone) {
        if (phone == null) {
            throw new RuntimeException("手机号不能为空");
        }
        return userMapper.getUserByPhone(phone);
    }

    @DataSource(DataSourceType.SLAVE)
    public User login(String phone, String password) {
        if (phone == null || password == null) {
            throw new RuntimeException("手机号或密码不能为空");
        }
        User user = userMapper.getUserByPhone(phone);
        if (user == null) {
            throw new RuntimeException("手机号不存在");
        }
        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    @DataSource(DataSourceType.MASTER)
    @Transactional
    public int register(String phone, String password, String name, String avatar) {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (phone == null || password == null || name == null || avatar == null) {
            throw new RuntimeException("参数不能为空");
        }
        if (userMapper.getUserByPhone(phone) != null) {
            throw new RuntimeException("手机号已存在");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        User user = new User().setPhone(phone)
                .setPassword(password)
                .setName(name)
                .setAvatar(avatar)
                .setCreateTime(time)
                .setUpdateTime(time);

        System.out.println(user);
        int result = insert(user);
        if (result == 0) {
            throw new RuntimeException("注册失败");
        }
        return result;

    }

    @DataSource(DataSourceType.MASTER)
    @Transactional
    public int updateInfo(BigInteger id, String phone, String password, String name, String avatar) {
        int time = (int) (System.currentTimeMillis() / 1000);

        if (id == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 获取现有用户信息
        User existingUser = getUserById(id);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 只更新非null的字段
        User user = new User().setId(id).setUpdateTime(time);

        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone.trim());
        } else {
            user.setPhone(existingUser.getPhone());
        }

        if (password != null && !password.trim().isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(password.trim()));
        } else {
            user.setPassword(existingUser.getPassword());
        }

        if (name != null) {
            user.setName(name);
        } else {
            user.setName(existingUser.getName());
        }

        if (avatar != null) {
            user.setAvatar(avatar);
        } else {
            user.setAvatar(existingUser.getAvatar());
        }

        int result = update(user);
        if (result == 0) {
            throw new RuntimeException("更新失败");
        }
        return result;
    }
}
