# 开发环境配置指南

## 开发工具推荐

### IDE
- **后端**: IntelliJ IDEA Ultimate / Eclipse
- **前端**: Visual Studio Code / WebStorm
- **数据库**: Navicat / DBeaver / MySQL Workbench

### 必需插件（VS Code）
```json
{
  "recommendations": [
    "Vue.volar",
    "Vue.vscode-typescript-vue-plugin",
    "bradlc.vscode-tailwindcss",
    "esbenp.prettier-vscode",
    "ms-vscode.vscode-eslint"
  ]
}
```

### 必需插件（IntelliJ IDEA）
- Lombok
- MyBatis X
- Maven Helper
- Database Tools and SQL

## 开发环境搭建

### 1. 安装基础环境

#### Java 21
```bash
# Windows (使用Chocolatey)
choco install openjdk21

# macOS (使用Homebrew)
brew install openjdk@21

# Linux (Ubuntu)
sudo apt install openjdk-21-jdk
```

#### Node.js 18+
```bash
# Windows
winget install OpenJS.NodeJS

# macOS
brew install node

# Linux
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

#### MySQL 8.0
```bash
# Windows
winget install Oracle.MySQL

# macOS
brew install mysql

# Linux
sudo apt install mysql-server
```

### 2. 配置开发环境

#### 配置Maven镜像（可选）
编辑 `~/.m2/settings.xml`：
```xml
<mirrors>
  <mirror>
    <id>alimaven</id>
    <name>aliyun maven</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
    <mirrorOf>central</mirrorOf>
  </mirror>
</mirrors>
```

#### 配置npm镜像（可选）
```bash
npm config set registry https://registry.npmmirror.com
```

### 3. 项目初始化

#### 后端项目
```bash
cd CDspringboot
mvn clean install
```

#### 前端项目
```bash
cd CD_vue
npm install
```

#### 数据库初始化
```sql
-- 1. 创建数据库
CREATE DATABASE course_design_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 创建用户（可选）
CREATE USER 'cdp_user'@'localhost' IDENTIFIED BY 'cdp_password';
GRANT ALL PRIVILEGES ON course_design_platform.* TO 'cdp_user'@'localhost';
FLUSH PRIVILEGES;

-- 3. 导入数据
USE course_design_platform;
SOURCE database_schema_v2.sql;
```

## 开发规范

### 代码规范

#### Java代码规范
- 使用Google Java Style Guide
- 类名使用PascalCase
- 方法名和变量名使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 包名使用小写字母

#### JavaScript/Vue代码规范
- 使用ESLint + Prettier
- 组件名使用PascalCase
- 方法名和变量名使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 文件名使用kebab-case

### Git提交规范
```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Type类型
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

#### 示例
```
feat(user): 添加用户登录功能

- 实现用户名密码登录
- 添加JWT token验证
- 增加登录状态管理

Closes #123
```

### 数据库规范

#### 命名规范
- 表名使用复数形式，如`users`, `courses`
- 字段名使用snake_case，如`user_id`, `created_at`
- 索引名使用`idx_`前缀
- 外键名使用`fk_`前缀

#### 字段规范
- 主键统一使用`BIGINT AUTO_INCREMENT`
- 时间字段使用`TIMESTAMP`类型
- 状态字段使用`ENUM`类型
- 文本字段根据长度选择`VARCHAR`或`TEXT`

### API设计规范

#### RESTful API
```
GET    /api/users          # 获取用户列表
GET    /api/users/{id}     # 获取单个用户
POST   /api/users          # 创建用户
PUT    /api/users/{id}     # 更新用户
DELETE /api/users/{id}     # 删除用户
```

#### 响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 响应数据
  }
}
```

#### 错误码规范
- `200`: 成功
- `400`: 请求参数错误
- `401`: 未认证
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器内部错误

## 调试技巧

### 后端调试

#### 日志配置
```properties
# application-dev.properties
logging.level.com.lyp.cdspringboot=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### 常用调试命令
```bash
# 查看应用状态
curl http://localhost:8080/actuator/health

# 查看配置信息
curl http://localhost:8080/actuator/configprops

# 查看环境变量
curl http://localhost:8080/actuator/env
```

### 前端调试

#### Vue DevTools
安装浏览器插件：Vue.js devtools

#### 常用调试技巧
```javascript
// 1. 使用console.log
console.log('调试信息:', data)

// 2. 使用debugger
debugger

// 3. 使用Vue的调试方法
import { getCurrentInstance } from 'vue'
const instance = getCurrentInstance()
console.log('组件实例:', instance)
```

### 数据库调试

#### 慢查询日志
```sql
-- 开启慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;

-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query%';
```

#### 性能分析
```sql
-- 分析查询计划
EXPLAIN SELECT * FROM users WHERE username = 'admin';

-- 查看表状态
SHOW TABLE STATUS LIKE 'users';
```

## 测试指南

### 单元测试

#### 后端测试
```java
@SpringBootTest
@Transactional
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    void testCreateUser() {
        // 测试代码
    }
}
```

#### 前端测试
```javascript
import { mount } from '@vue/test-utils'
import Login from '@/views/Login.vue'

describe('Login.vue', () => {
  it('renders login form', () => {
    const wrapper = mount(Login)
    expect(wrapper.find('form').exists()).toBe(true)
  })
})
```

### 集成测试

#### API测试
使用Postman或者编写测试脚本：
```javascript
// test/api.test.js
const axios = require('axios')

describe('API Tests', () => {
  test('用户登录', async () => {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: 'admin',
      password: '123456'
    })
    expect(response.status).toBe(200)
    expect(response.data.code).toBe(200)
  })
})
```

## 性能优化

### 后端优化
1. **数据库优化**
   - 添加适当索引
   - 优化SQL查询
   - 使用连接池

2. **缓存策略**
   - Redis缓存
   - 应用级缓存
   - 数据库查询缓存

3. **异步处理**
   - 使用@Async注解
   - 消息队列
   - 线程池配置

### 前端优化
1. **代码分割**
   - 路由懒加载
   - 组件懒加载
   - 第三方库按需引入

2. **资源优化**
   - 图片压缩
   - 文件压缩
   - CDN加速

3. **渲染优化**
   - 虚拟滚动
   - 防抖节流
   - 组件缓存

## 部署指南

### 开发环境部署
```bash
# 启动所有服务
./start.bat  # Windows
./start.sh   # Linux/macOS
```

### 生产环境部署
参考README.md中的部署说明。

## 常见问题解决

### 端口占用
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -i :8080
kill -9 <PID>
```

### 依赖冲突
```bash
# Maven
mvn dependency:tree
mvn dependency:resolve

# npm
npm ls
npm audit fix
```

### 数据库连接问题
1. 检查MySQL服务状态
2. 验证用户名密码
3. 检查防火墙设置
4. 查看错误日志

---

更多问题请参考项目Wiki或提交Issue。
