package com.lyp.cdspringboot.controller;

import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.entity.Student;
import com.lyp.cdspringboot.entity.Teacher;
import com.lyp.cdspringboot.entity.User;
import com.lyp.cdspringboot.mapper.StudentMapper;
import com.lyp.cdspringboot.mapper.TeacherMapper;
import com.lyp.cdspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取当前用户的详细信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("未登录或登录已失效");
        }

        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("user", currentUser);

        // 根据用户类型获取详细信息
        if (User.UserType.STUDENT.equals(currentUser.getUserType())) {
            Student student = studentMapper.selectById(currentUser.getUserId());
            profileData.put("student", student);
        } else if (User.UserType.TEACHER.equals(currentUser.getUserType())) {
            Teacher teacher = teacherMapper.selectById(currentUser.getUserId());
            profileData.put("teacher", teacher);
        }

        return Result.success(profileData);
    }

    /**
     * 更新用户基本信息
     */
    @PutMapping("/update")
    public Result<String> updateProfile(@RequestBody Map<String, Object> updateData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("未登录或登录已失效");
        }

        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        try {
            // 更新用户基本信息
            if (updateData.containsKey("realName")) {
                currentUser.setRealName((String) updateData.get("realName"));
            }
            if (updateData.containsKey("email")) {
                currentUser.setEmail((String) updateData.get("email"));
            }
            if (updateData.containsKey("phone")) {
                currentUser.setPhone((String) updateData.get("phone"));
            }

            userService.updateById(currentUser);

            // 根据用户类型更新详细信息
            if (User.UserType.TEACHER.equals(currentUser.getUserType()) && updateData.containsKey("teacher")) {
                Map<String, Object> teacherData = (Map<String, Object>) updateData.get("teacher");
                Teacher teacher = teacherMapper.selectById(currentUser.getUserId());
                if (teacher != null) {
                    if (teacherData.containsKey("title")) {
                        teacher.setTitle((String) teacherData.get("title"));
                    }
                    if (teacherData.containsKey("researchArea")) {
                        teacher.setResearchArea((String) teacherData.get("researchArea"));
                    }
                    if (teacherData.containsKey("officeLocation")) {
                        teacher.setOfficeLocation((String) teacherData.get("officeLocation"));
                    }
                    teacherMapper.updateById(teacher);
                }
            }

            return Result.success("信息更新成功");
        } catch (Exception e) {
            return Result.error("信息更新失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestBody Map<String, String> passwordData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("未登录或登录已失效");
        }

        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return Result.error("密码不能为空");
        }

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            return Result.error("当前密码错误");
        }

        try {
            // 更新密码
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userService.updateById(currentUser);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("密码修改失败：" + e.getMessage());
        }
    }
}
