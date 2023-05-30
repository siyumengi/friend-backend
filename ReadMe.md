# friend-backend

## 技术选型

* Java 8
* Spring + SpringMVC + SpringBoot 2.7.X 框架
* MyBatis + MyBatis Plus 数据访问框架
* MySQL 数据库
* Junit 单元测试库
* Redis 缓存 （Spring Data Redis 等多种实现方式）
* Redisson 分布式锁
* Easy Excel 数据导入
* MyBatis X 自动生成
* Spring Scheduler 定时任务
* Swagger + Knife4j 接口文档
* Gson：JSON 序列化库
* 相似匹配算法

## 第一天

直接在 IDEA 开发工具中初始化了

## 第二天

### 编写了 common 包

定制了 **通用返回类**

```java
/**
 * 通用返回类
 * @author siyumeng
 * @param <T>
 */

@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
```

定制了 **错误码**

```java
/**
 * 错误码
 * @author siyumeng
 */
public enum ErrorCode {

    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    FORBIDDEN(40301, "禁止操作", ""),
    SYSTEM_ERROR(50000, "系统内部异常", "");

    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}

```

定制了 通用 **返回类**

```java
/**
 * 返回工具类
 * @author siyumeng
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), null, message, description);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description);
    }
}

```

编写了 application.yml

``````java
spring:
  profiles:
#      运行环境
    active: dev
  application:
    name: friend-backend
  # DataSource Config
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/partner?serverTimezone=UTC
    username: ********
    password: ********
  # session 失效时间（分钟）
  session:
    timeout: 86400
#    设置缓存在 Redis 中
    store-type: redis
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
  # redis 配置
  #  redis:
  #    port: 6379
  #    host: localhost
  #    database: 1
  redis:
    port: 6379
    host: **********
    database: 0
    password: ******
  mail:
    host: smtp.qq.com
    # QQ邮箱
    username: ************@qq.com
    # QQ邮箱授权码
    password: ************
server:
  port: 8081
  servlet:
    context-path: /api
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: false
  #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: isDelete # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)

``````

### 编写了一些常用的 utils

AllUtils 的功能有：

1. *判断字符串类型*
2. *Jacquard* *相似度匹配*
3. *将字符串集合中的中文剔除*
4. *余弦相似度算法*
5. *编辑距离*
6. *提取中文字符*

MessageUtils 的功能是将消息以 JSON 格式封装和传输

QiniuOssUtils 是*七牛**OSS**管理工具*

ValidateCodeUtils 是*随机生成验证码工具类*

### 编写了一些配置类 config

GetHttpSessionConfigurator、MybatisPlusConfig、WebSocketConfig、RedissonConfig、RedisTemplateConfig、SwaggerConfig、WebMvcConfig

### 编写了必备包

controller、service、model、mapper

### 五月二十九日上午10点小总结

编写了一些基础的包，接下来两个小时可以设计一下用户表和考虑会用到什么

### 用户表

设计了用户表，用 Mybatis X 生成了user 的 mapper、controller、service。

```java
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `userPassword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(0) NOT NULL DEFAULT 0 COMMENT '性别：0-未知；1-男；2-女',
  `age` tinyint(0) NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `userStatus` int(0) NOT NULL DEFAULT 0 COMMENT '状态 0 - 正常',
  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签 json 列表',
  `userRole` int(0) NOT NULL DEFAULT 0 COMMENT '用户角色 0 - 普通用户 1 - 管理员',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

```

### 编写了自定义异常和全局异常处理器

自定义异常

```java
/**
 * 自定义异常类
 * @author siyumengi
 */
public class BusinessException extends RuntimeException {

    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

```

全局异常处理器

```java
/**
 * 全局异常处理器
 *
 * @author siyumengi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}

```

### 五月二十九日上午两节课小结

总结以往的编写经验，可以得出 common，utils，config，exception 这四个包以后在开发新项目可以直接copy。

开了 userController 的小头，编写了注册的接口，下午争取能把 userController 的逻辑编写完，能写一下前台页面，好像还少了后台的管理页面。

### 五月二十九日晚上

#### 编写注册接口

封装一个注册的 userRegisterRequest 类

```java
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 验证码
     */
    private String code;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 检验密码
     */
    private String checkPassword;
```

1. 前台传递的参数是否为空
2. 参数校验
   1. 不能为空
   2. 账户长度不少于 4 位
   3. 邮箱填写正确
   4. 验证码应为 6 位
   5. 密码长度不少于 8 位
   6. 用户账号不能存在特殊字符
3. 用户密码和校验密码相同
4. 验证码是否相同
   1. 获取验证码
   2. 比较验证码
5. 密码加盐
6. 保存数据

#### 编写验证码

用 QQ 邮箱作为注册选项

```java
            //发送一个六位数的验证码,把验证码变成String类型
            code = ValidateCodeUtils.generateValidateCode(6).toString();
            String text = "【friend 系统】您好，您的验证码为：" + code + "，请在5分钟内使用";
            log.info("验证码为：" + code);
            //发送短信
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(userEmail);
            message.setSubject(subject);
            message.setText(text);
            //发送邮件
            javaMailSender.send(message);
            UserSendMessage userSendMessage = new UserSendMessage();
            userSendMessage.setUserEmail(userEmail);
            userSendMessage.setCode(code);
```

虽然中途出了点问题，只写了这两个，明天得加速了，冲

## 第三天

### 30日下午

#### 编写用户登录接口

1. 定义用户登录请求体
2. 参数校验
   1. 是否为空
   2. 账户长度是否小于 4 位
   3. 密码长度是否小于 4 位
   4. 账户是否存在特殊字符
3. 密码加盐
4. 查表判断是否相同
5. 写入reids
6. 脱敏
7. 记录用户态
8. 返回脱敏用户

#### 编写用户退出接口

1. 删除 req
2. 删除 redis 缓存

#### 编写获取当前用户接口

1. 从缓存中取
2. 是否存在
3. 脱敏
4. 返回脱敏后数据

#### 编写更新用户信息接口

1. 校验参数
   1. username 长度不小于 4 位
   2. userAccount 长度不小于 4 位
   3. gender 不能设置其他值
   4. age 不能小于 0 不能超过 200
2. 与旧数据相同就不更新
3. 判断是谁更新用户
   1. 如果是管理员可以随意更改
   2. 如果是用户本身只能更改自己
4. 