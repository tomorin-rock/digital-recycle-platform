# 数字回收平台系统

## 项目简介

数字回收平台系统是一个用于回收和以旧换新的在线平台，支持用户提交回收订单、以旧换新订单，以及管理员进行订单管理、产品管理等功能。

## 系统展示

![登录](https://github.com/tomorin-rock/digital-recycle-platform/blob/main/login.png)
![商品](https://github.com/tomorin-rock/digital-recycle-platform/blob/main/products.png)

## 技术栈

### 前端
- Vue 3 + TypeScript
- Vite 6.0.5
- Element Plus 2.9.1
- Axios 1.7.9
- Pinia 2.3.0
- Vue Router 4.5.0
- ECharts 6.0.0
- Dayjs 1.11.13

### 后端
- Java 17
- Spring Boot 3.x
- MySQL 8.0+
- Redis 7.0+

## 环境要求

### 前端
- Node.js 18.0+
- npm 9.0+ 或 yarn 1.22+

### 后端
- JDK 17
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+

## 安装步骤

### 1. 克隆仓库

```bash
git clone https://github.com/tomorin-rock/digital-recycle-platform.git
cd digital-recycle-platform
```

### 2. 前端安装

```bash
# 进入前端目录
cd platform

# 安装依赖
npm install
# 或使用 yarn
yarn install
```

### 3. 后端安装

```bash
# 进入后端目录
cd backend

# 安装依赖
mvn install
```

## 数据库配置

1. 启动 MySQL 服务
2. 创建数据库 `digitalRecycle`
3. 配置 `backend/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/digitalRecycle
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root  # 替换为你的数据库用户名
    password: root  # 替换为你的数据库密码
```

## Redis配置

启动 Redis 服务，确保默认端口 6379 可用。

## 启动命令

### 前端启动

```bash
# 进入前端目录
cd platform

# 启动开发服务器
npm run dev
# 或使用 yarn
yarn dev
```

前端服务将在 http://localhost:5000 启动。

### 后端启动

```bash
# 进入后端目录
cd backend

# 启动后端服务
mvn spring-boot:run
```

后端服务将在 http://localhost:1000 启动。

## 构建命令

### 前端构建

```bash
# 进入前端目录
cd platform

# 构建生产版本
npm run build
# 或使用 yarn
yarn build
```

构建产物将生成在 `platform/dist` 目录。

### 后端构建

```bash
# 进入后端目录
cd backend

# 构建生产版本
mvn clean package
```

构建产物将生成在 `backend/target` 目录。
