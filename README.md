# Seckill-Project

## 一、项目概述
本项目聚焦秒杀场景下的高可用、高性能与数据一致性问题，通过 SpringBoot + MySQL + Redis + RabbitMQ + MyBatis-Plus 等技术栈，构建从用户登录、商品展示到秒杀下单的完整流程，解决并发读 / 写优化、分布式 Session 共享、超卖 / 复购、接口限流等核心问题，可支撑万级并发请求，保障秒杀活动稳定运行。

## 二、技术栈
- 后端框架：SpringBoot 2.5.4	快速搭建项目，简化配置
- 数据存储：	MySQL 、Redis	MySQL 存储业务数据；Redis 实现缓存（页面 / 对象）、预减库存
- 消息队列：	RabbitMQ	秒杀请求削峰填谷，异步处理下单流程
- 构建工具：	Maven	项目依赖管理与构建
- 测试工具：Jmeter	并发压测，验证系统性能

## 三、环境启动
1、修改application.yml相关配置。

2、执行SeckillApplication.java的main方法，启动 SpringBoot 项目。

3、访问`http://localhost:8080/login/toLogin`，输入测试账号（如手机号13300000000，密码12345），登录后进入商品列表页。
