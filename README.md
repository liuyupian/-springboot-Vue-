# 课程设计报告管理系统

## 项目简介

课程设计报告管理系统是一个基于前后端分离架构的Web应用，用于管理学生课程设计报告的提交、评审和成绩管理。系统支持学生、教师、管理员三种角色，提供完整的课程设计管理流程。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.x
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis Plus 3.5.x
- **安全**: Spring Security + JWT
- **构建工具**: Maven

### 前端
- **框架**: Vue 3.3.x
- **构建工具**: Vite 4.x
- **UI组件库**: Element Plus 2.3.x
- **状态管理**: Pinia 2.1.x
- **路由**: Vue Router 4.2.x
- **HTTP客户端**: Axios 1.5.x

### 数据库
- **数据库**: MySQL 8.0
- **字符集**: utf8mb4
- **存储引擎**: InnoDB

## 功能特性

### 用户管理
- 用户注册、登录、登出
- 角色权限管理（学生、教师、管理员）
- 个人信息管理
- 头像上传

### 课程管理
- 课程信息管理
- 班级管理
- 师生关联

### 任务管理
- 课程设计任务发布
- 任务要求设置
- 截止时间管理
- 文件类型限制

### 报告提交
- 在线报告提交
- 多文件上传支持
- 提交历史记录
- 迟交检测

### 评审管理
- 报告评审
- 评分管理
- 评语反馈
- 状态跟踪

## 项目结构

```
JavawebCourseDesign/
├── CDspringboot/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/lyp/cdspringboot/
│   │       ├── entity/           # 实体类
│   │       ├── mapper/           # 数据访问层
│   │       ├── service/          # 业务逻辑层
│   │       ├── controller/       # 控制器层
│   │       ├── config/           # 配置类
│   │       ├── common/           # 通用类
│   │       ├── dto/              # 数据传输对象
│   │       └── util/             # 工具类
│   ├── src/main/resources/
│   │   ├── application.properties # 应用配置
│   │   └── mapper/               # MyBatis映射文件
│   └── pom.xml                   # Maven配置
├── CD_vue/                       # 前端项目
│   ├── src/
│   │   ├── api/                  # API接口
│   │   ├── components/           # 组件
│   │   ├── router/               # 路由配置
│   │   ├── stores/               # 状态管理
│   │   ├── views/                # 页面组件
│   │   ├── App.vue               # 根组件
│   │   └── main.js               # 入口文件
│   ├── package.json              # 依赖配置
│   └── vite.config.js            # Vite配置
├── database_schema_v2.sql        # 数据库建表脚本
└── README.md                     # 项目说明
```

## 快速开始

### 环境要求

- Java 21+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 数据库配置

1. 创建数据库
```sql
CREATE DATABASE course_design_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入数据库结构
```bash
mysql -u root -p course_design_platform < database_schema_v2.sql
```

3. 修改数据库连接配置
编辑 `CDspringboot/src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/course_design_platform?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 后端启动

1. 进入后端项目目录
```bash
cd CDspringboot
```

2. 安装依赖并启动
```bash
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. 进入前端项目目录
```bash
cd CD_vue
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动

## 默认账号

系统已预置以下测试账号：

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 系统管理员 |
| 教师 | teacher001 | 123456 | 张教授 |
| 教师 | teacher002 | 123456 | 李副教授 |
| 学生 | student001 | 123456 | 王同学 |
| 学生 | student002 | 123456 | 李同学 |

## 主要功能页面

### 学生端
- **仪表盘**: 查看待完成任务和提交状态
- **任务列表**: 查看所有课程设计任务
- **我的提交**: 管理报告提交和查看评审结果
- **个人信息**: 管理个人资料

### 教师端
- **仪表盘**: 查看教学统计和待评审任务
- **任务管理**: 创建、编辑、发布课程设计任务
- **提交管理**: 查看学生提交并进行评审
- **个人信息**: 管理个人资料和教学信息

### 管理员端
- **系统管理**: 用户管理、权限配置
- **数据统计**: 系统使用情况统计
- **系统配置**: 全局参数设置

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 用户管理
- `GET /api/users` - 获取用户列表
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户

### 任务管理
- `GET /api/tasks` - 获取任务列表
- `POST /api/tasks` - 创建任务
- `PUT /api/tasks/{id}` - 更新任务
- `DELETE /api/tasks/{id}` - 删除任务

### 提交管理
- `GET /api/submissions` - 获取提交列表
- `POST /api/submissions` - 创建提交
- `PUT /api/submissions/{id}` - 更新提交
- `POST /api/submissions/{id}/review` - 评审提交

## 部署说明

### 生产环境配置

1. **后端部署**
```bash
# 打包
mvn clean package -Dmaven.test.skip=true

# 运行
java -jar target/CDspringboot-0.0.1-SNAPSHOT.jar
```

2. **前端部署**
```bash
# 构建
npm run build

# 部署到Web服务器
cp -r dist/* /var/www/html/
```

3. **数据库优化**
- 创建适当的索引
- 配置数据库连接池
- 设置备份策略

4. **安全配置**
- 修改默认密码
- 配置HTTPS
- 设置防火墙规则

## 开发指南

### 后端开发
1. 遵循RESTful API设计规范
2. 使用统一的响应格式
3. 添加适当的日志记录
4. 编写单元测试

### 前端开发
1. 遵循Vue 3组合式API规范
2. 使用TypeScript增强类型安全
3. 组件化开发
4. 响应式设计

## 常见问题

### Q: 数据库连接失败
A: 检查数据库服务是否启动，用户名密码是否正确，防火墙设置是否允许连接。

### Q: 前端页面空白
A: 检查后端API是否正常启动，浏览器控制台是否有错误信息。

### Q: 文件上传失败
A: 检查上传目录权限，文件大小限制，文件类型限制。

### Q: JWT Token过期
A: 重新登录获取新的Token，或者配置Token自动刷新机制。

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码
4. 发起Pull Request

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

如有问题或建议，请联系：
- 邮箱: yige.ss@foxmail.com
- 项目地址: ***

---

**注意**: 这是一个课程设计项目，仅用于学习和教学目的。在生产环境使用前，请进行充分的安全测试和性能优化。

