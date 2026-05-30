package com.example.todo.facade;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.todo.dto.LoginDTO;
import com.example.todo.dto.RegisterDTO;
import com.example.todo.entity.User;
import com.example.todo.service.UserService;
import com.example.todo.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final UserService userService;

    /**
     * 用户登录
     */
    public UserVO login(LoginDTO dto) {
        User user = userService.getOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        StpUtil.login(user.getId());
        return toUserVO(user);
    }

    /**
     * 用户注册
     */
    public void register(RegisterDTO dto) {
        Long count = userService.count(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User(dto.getUsername(), dto.getPassword());
        userService.save(user);
    }

    /**
     * 退出登录
     */
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 获取当前登录用户VO
     */
    public UserVO getCurrentUserVO() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        return toUserVO(user);
    }

    /**
     * Entity → VO
     */
    private UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        return new UserVO(user.getId(), user.getUsername(), user.getCreatedAt());
    }
}
