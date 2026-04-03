/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : digitalrecycle

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 03/04/2026 13:37:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NOT NULL COMMENT '所属用户',
  `sourceRecycleId` bigint NOT NULL COMMENT '来源回收订单ID',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '券码',
  `amount` decimal(10, 2) NOT NULL COMMENT '抵扣金额',
  `status` enum('UNUSED','USED','EXPIRED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNUSED' COMMENT '未使用/已使用/过期',
  `expireTime` datetime NOT NULL COMMENT '过期时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `useTime` datetime NULL DEFAULT NULL COMMENT '实际使用时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `fkCouponRecycle`(`sourceRecycleId` ASC) USING BTREE,
  INDEX `idxCouponUser`(`userId` ASC) USING BTREE,
  INDEX `idxCouponStatus`(`status` ASC) USING BTREE,
  CONSTRAINT `fkCouponRecycle` FOREIGN KEY (`sourceRecycleId`) REFERENCES `recycleorder` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkCouponUser` FOREIGN KEY (`userId`) REFERENCES `sysuser` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3240 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '以旧换新抵扣券' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (3239, 6, 3267, 'EQUAL-2039927212480638976', 2000.00, 'UNUSED', '2026-04-18 12:45:22', '2026-04-03 12:45:22', NULL);

-- ----------------------------
-- Table structure for exchangeorder
-- ----------------------------
DROP TABLE IF EXISTS `exchangeorder`;
CREATE TABLE `exchangeorder`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orderNo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '以旧换新订单编号',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `recycleId` bigint NOT NULL COMMENT '对应的回收订单ID',
  `couponId` bigint NULL DEFAULT NULL COMMENT '使用的抵扣券ID',
  `newProductId` bigint NOT NULL COMMENT '购买的新机商品ID',
  `addressId` bigint NOT NULL COMMENT '收货地址ID',
  `productPrice` decimal(10, 2) NOT NULL COMMENT '新机原价',
  `discountAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '抵扣金额',
  `payAmount` decimal(10, 2) NOT NULL COMMENT '实际支付金额',
  `status` enum('UNPAID','PAID','SHIPPED','FINISHED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNPAID' COMMENT '未支付/已支付/已发货/已完成/已取消',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `orderNo`(`orderNo` ASC) USING BTREE,
  INDEX `fkExRecycle`(`recycleId` ASC) USING BTREE,
  INDEX `fkExCoupon`(`couponId` ASC) USING BTREE,
  INDEX `fkExProduct`(`newProductId` ASC) USING BTREE,
  INDEX `fkExAddress`(`addressId` ASC) USING BTREE,
  INDEX `idxExUser`(`userId` ASC) USING BTREE,
  INDEX `idxExStatus`(`status` ASC) USING BTREE,
  CONSTRAINT `fkExAddress` FOREIGN KEY (`addressId`) REFERENCES `useraddress` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkExCoupon` FOREIGN KEY (`couponId`) REFERENCES `coupon` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkExProduct` FOREIGN KEY (`newProductId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkExRecycle` FOREIGN KEY (`recycleId`) REFERENCES `recycleorder` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkExUser` FOREIGN KEY (`userId`) REFERENCES `sysuser` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 399 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '以旧换新订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exchangeorder
-- ----------------------------
INSERT INTO `exchangeorder` VALUES (398, 'E2039927212791017472', 6, 3267, 3239, 40, 17, 999.00, 2000.00, 0.00, 'CANCELLED', '2026-04-03 12:45:22', '2026-04-03 12:46:13');

-- ----------------------------
-- Table structure for forumcomment
-- ----------------------------
DROP TABLE IF EXISTS `forumcomment`;
CREATE TABLE `forumcomment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `postId` bigint NOT NULL COMMENT '所属帖子',
  `userId` bigint NOT NULL COMMENT '评论用户',
  `parentId` bigint NULL DEFAULT NULL COMMENT '父评论ID(楼中楼)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1正常 0屏蔽',
  `likeCount` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fkCommentParent`(`parentId` ASC) USING BTREE,
  INDEX `idxCommentPost`(`postId` ASC) USING BTREE,
  INDEX `idxCommentUser`(`userId` ASC) USING BTREE,
  CONSTRAINT `fkCommentParent` FOREIGN KEY (`parentId`) REFERENCES `forumcomment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fkCommentPost` FOREIGN KEY (`postId`) REFERENCES `forumpost` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fkCommentUser` FOREIGN KEY (`userId`) REFERENCES `sysuser` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 308 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of forumcomment
-- ----------------------------

-- ----------------------------
-- Table structure for forumpost
-- ----------------------------
DROP TABLE IF EXISTS `forumpost`;
CREATE TABLE `forumpost`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NOT NULL COMMENT '发帖用户',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `viewCount` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `replyCount` int NOT NULL DEFAULT 0 COMMENT '回复数',
  `likeCount` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1正常 0屏蔽',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idxPostUser`(`userId` ASC) USING BTREE,
  INDEX `idxPostStatus`(`status` ASC) USING BTREE,
  CONSTRAINT `fkPostUser` FOREIGN KEY (`userId`) REFERENCES `sysuser` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 279 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛帖子' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of forumpost
-- ----------------------------
INSERT INTO `forumpost` VALUES (278, 6, 'nihao', '2nihaonihaonihao', 1, 1, 1, 1, '2026-04-03 11:45:56', '2026-04-03 11:50:05');

-- ----------------------------
-- Table structure for orderoperationlog
-- ----------------------------
DROP TABLE IF EXISTS `orderoperationlog`;
CREATE TABLE `orderoperationlog`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orderType` enum('RECYCLE','EXCHANGE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型(回收/交换)',
  `orderId` bigint NOT NULL COMMENT '对应订单ID',
  `operatorId` bigint NOT NULL COMMENT '操作人(管理员/系统)',
  `operateRole` enum('USER','ADMIN','SYSTEM') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人角色',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作名称',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作详情',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idxLogOrder`(`orderType` ASC, `orderId` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14487 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单操作日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orderoperationlog
-- ----------------------------
INSERT INTO `orderoperationlog` VALUES (14446, 'RECYCLE', 3260, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1600元，成色：B', '2026-04-03 12:24:00');
INSERT INTO `orderoperationlog` VALUES (14447, 'RECYCLE', 3260, 6, 'ADMIN', '开始质检', '订单进入质检状态', '2026-04-03 12:24:00');
INSERT INTO `orderoperationlog` VALUES (14448, 'RECYCLE', 3260, 6, 'ADMIN', '完成质检', '完成质检，最终价格：2000元，备注：检测结果：机身完整，电池健康度为85%', '2026-04-03 12:24:00');
INSERT INTO `orderoperationlog` VALUES (14449, 'RECYCLE', 3260, 6, 'USER', '订单确认完成', '订单确认完成，支付方式：优惠券，金额：2000.00元', '2026-04-03 12:24:00');
INSERT INTO `orderoperationlog` VALUES (14450, 'EXCHANGE', 391, 6, 'USER', '创建订单', '创建换新订单，应付金额：0元', '2026-04-03 12:24:00');
INSERT INTO `orderoperationlog` VALUES (14451, 'RECYCLE', 3261, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1600元，成色：B', '2026-04-03 12:27:42');
INSERT INTO `orderoperationlog` VALUES (14452, 'RECYCLE', 3261, 6, 'ADMIN', '开始质检', '订单进入质检状态', '2026-04-03 12:27:42');
INSERT INTO `orderoperationlog` VALUES (14453, 'RECYCLE', 3261, 6, 'ADMIN', '完成质检', '完成质检，最终价格：2000元，备注：检测结果：机身完整，电池健康度为85%', '2026-04-03 12:27:42');
INSERT INTO `orderoperationlog` VALUES (14454, 'RECYCLE', 3261, 6, 'USER', '订单确认完成', '订单确认完成，支付方式：优惠券，金额：2000.00元', '2026-04-03 12:27:42');
INSERT INTO `orderoperationlog` VALUES (14455, 'EXCHANGE', 392, 6, 'USER', '创建订单', '创建换新订单，应付金额：0元', '2026-04-03 12:27:42');
INSERT INTO `orderoperationlog` VALUES (14456, 'RECYCLE', 3262, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1600元，成色：B', '2026-04-03 12:28:36');
INSERT INTO `orderoperationlog` VALUES (14457, 'RECYCLE', 3262, 6, 'ADMIN', '开始质检', '订单进入质检状态', '2026-04-03 12:28:36');
INSERT INTO `orderoperationlog` VALUES (14458, 'RECYCLE', 3262, 6, 'ADMIN', '完成质检', '完成质检，最终价格：2000元，备注：检测结果：机身完整，电池健康度为85%', '2026-04-03 12:28:36');
INSERT INTO `orderoperationlog` VALUES (14459, 'RECYCLE', 3262, 6, 'USER', '订单确认完成', '订单确认完成，支付方式：优惠券，金额：2000.00元', '2026-04-03 12:28:36');
INSERT INTO `orderoperationlog` VALUES (14460, 'EXCHANGE', 393, 6, 'USER', '创建订单', '创建换新订单，应付金额：0元', '2026-04-03 12:28:36');
INSERT INTO `orderoperationlog` VALUES (14461, 'RECYCLE', 3263, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1200元，成色：B', '2026-04-03 12:31:46');
INSERT INTO `orderoperationlog` VALUES (14462, 'EXCHANGE', 394, 6, 'USER', '创建订单', '创建换新订单，应付金额：6799.00元', '2026-04-03 12:31:51');
INSERT INTO `orderoperationlog` VALUES (14463, 'EXCHANGE', 394, 6, 'USER', '支付订单', '支付订单，支付金额：6799.00元', '2026-04-03 12:31:54');
INSERT INTO `orderoperationlog` VALUES (14464, 'EXCHANGE', 393, 6, 'USER', '支付订单', '支付订单，支付金额：0.00元', '2026-04-03 12:31:58');
INSERT INTO `orderoperationlog` VALUES (14465, 'EXCHANGE', 392, 6, 'USER', '支付订单', '支付订单，支付金额：0.00元', '2026-04-03 12:32:01');
INSERT INTO `orderoperationlog` VALUES (14466, 'RECYCLE', 3264, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1600元，成色：B', '2026-04-03 12:33:55');
INSERT INTO `orderoperationlog` VALUES (14467, 'RECYCLE', 3264, 6, 'ADMIN', '开始质检', '订单进入质检状态', '2026-04-03 12:33:56');
INSERT INTO `orderoperationlog` VALUES (14468, 'RECYCLE', 3264, 6, 'ADMIN', '完成质检', '完成质检，最终价格：2000元，备注：检测结果：机身完整，电池健康度为85%', '2026-04-03 12:33:56');
INSERT INTO `orderoperationlog` VALUES (14469, 'RECYCLE', 3264, 6, 'USER', '订单确认完成', '订单确认完成，支付方式：优惠券，金额：2000.00元', '2026-04-03 12:33:56');
INSERT INTO `orderoperationlog` VALUES (14470, 'EXCHANGE', 395, 6, 'USER', '创建订单', '创建换新订单，应付金额：0元', '2026-04-03 12:33:56');
INSERT INTO `orderoperationlog` VALUES (14471, 'RECYCLE', 3265, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1600元，成色：B', '2026-04-03 12:35:19');
INSERT INTO `orderoperationlog` VALUES (14472, 'RECYCLE', 3265, 6, 'ADMIN', '开始质检', '订单进入质检状态', '2026-04-03 12:35:19');
INSERT INTO `orderoperationlog` VALUES (14473, 'RECYCLE', 3265, 6, 'ADMIN', '完成质检', '完成质检，最终价格：2000元，备注：检测结果：机身完整，电池健康度为85%', '2026-04-03 12:35:19');
INSERT INTO `orderoperationlog` VALUES (14474, 'RECYCLE', 3265, 6, 'USER', '订单确认完成', '订单确认完成，支付方式：优惠券，金额：2000.00元', '2026-04-03 12:35:19');
INSERT INTO `orderoperationlog` VALUES (14475, 'EXCHANGE', 396, 6, 'USER', '创建订单', '创建换新订单，应付金额：0元', '2026-04-03 12:35:19');
INSERT INTO `orderoperationlog` VALUES (14476, 'RECYCLE', 3266, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1600元，成色：B', '2026-04-03 12:38:50');
INSERT INTO `orderoperationlog` VALUES (14477, 'RECYCLE', 3266, 6, 'ADMIN', '开始质检', '订单进入质检状态', '2026-04-03 12:38:50');
INSERT INTO `orderoperationlog` VALUES (14478, 'RECYCLE', 3266, 6, 'ADMIN', '完成质检', '完成质检，最终价格：2000元，备注：检测结果：机身完整，电池健康度为85%', '2026-04-03 12:38:50');
INSERT INTO `orderoperationlog` VALUES (14479, 'RECYCLE', 3266, 6, 'USER', '订单确认完成', '订单确认完成，支付方式：优惠券，金额：2000.00元', '2026-04-03 12:38:50');
INSERT INTO `orderoperationlog` VALUES (14480, 'EXCHANGE', 397, 6, 'USER', '创建订单', '创建换新订单，应付金额：0元', '2026-04-03 12:38:50');
INSERT INTO `orderoperationlog` VALUES (14481, 'RECYCLE', 3267, 6, 'USER', '提交回收订单', '提交回收订单，预估价格：1600元，成色：B', '2026-04-03 12:45:22');
INSERT INTO `orderoperationlog` VALUES (14482, 'RECYCLE', 3267, 6, 'ADMIN', '开始质检', '订单进入质检状态', '2026-04-03 12:45:22');
INSERT INTO `orderoperationlog` VALUES (14483, 'RECYCLE', 3267, 6, 'ADMIN', '完成质检', '完成质检，最终价格：2000元，备注：检测结果：机身完整，电池健康度为85%', '2026-04-03 12:45:22');
INSERT INTO `orderoperationlog` VALUES (14484, 'RECYCLE', 3267, 6, 'USER', '订单确认完成', '订单确认完成，支付方式：优惠券，金额：2000.00元', '2026-04-03 12:45:22');
INSERT INTO `orderoperationlog` VALUES (14485, 'EXCHANGE', 398, 6, 'USER', '创建订单', '创建换新订单，应付金额：0元', '2026-04-03 12:45:22');
INSERT INTO `orderoperationlog` VALUES (14486, 'EXCHANGE', 398, 6, 'USER', '取消订单', '取消订单，订单号：E2039927212791017472', '2026-04-03 12:46:13');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `categoryId` bigint NOT NULL COMMENT '分类ID',
  `brandId` bigint NOT NULL COMMENT '品牌ID',
  `modelName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '型号名称(如 iPhone 15 128G)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `imgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `isNew` tinyint NOT NULL DEFAULT 1 COMMENT '是否新机:1新机 0 旧机',
  `marketPrice` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '新机售价(商城价格)',
  `recycleBasePrice` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '回收基准价(九成新参考价)',
  `stock` int NULL DEFAULT 0 COMMENT '库存(新机用)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idxProductCategory`(`categoryId` ASC) USING BTREE,
  INDEX `idxProductBrand`(`brandId` ASC) USING BTREE,
  INDEX `idxProductStatus`(`status` ASC) USING BTREE,
  CONSTRAINT `fkProductBrand` FOREIGN KEY (`brandId`) REFERENCES `productbrand` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkProductCategory` FOREIGN KEY (`categoryId`) REFERENCES `productcategory` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品/型号表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 20, 10, '华为 Mate 60 Pro+', '麒麟9000S芯片，第二代昆仑玻璃，北斗卫星', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 1, 7003.00, 3498.00, 92, 1, '2026-02-28 11:28:16', '2026-04-02 21:11:00');
INSERT INTO `product` VALUES (2, 20, 10, '华为 Pura 70 Ultra', '伸缩镜头设计，超聚光伸缩主摄，鸿蒙4.2系统', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 1, 9999.00, 5000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:50:19');
INSERT INTO `product` VALUES (3, 20, 10, '华为 nova 12 Pro', '前置双目立体视觉影像，鸿蒙智慧通信', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 0, 3499.00, 1500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:50:19');
INSERT INTO `product` VALUES (4, 20, 10, '华为 Mate X5', '玄武钢化昆仑玻璃，灵犀通信，轻薄大屏', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 1, 12999.00, 6500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:50:19');
INSERT INTO `product` VALUES (5, 20, 10, '华为 Mate 50 Pro', '昆仑玻璃，北斗卫星消息，应急模式', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 0, 5999.00, 2500.00, 101, 1, '2026-02-28 11:28:16', '2026-04-02 16:24:19');
INSERT INTO `product` VALUES (6, 20, 10, '华为 Pura 70 Pro', '超聚光伸缩主摄，风驰闪拍，长焦微距', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 1, 6499.00, 3200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:50:19');
INSERT INTO `product` VALUES (7, 20, 10, '华为 MatePad Pro 11', 'OLED全面屏，星闪连接技术，多设备协同', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 0, 4299.00, 2000.00, 104, 1, '2026-02-28 11:28:16', '2026-03-31 09:40:06');
INSERT INTO `product` VALUES (8, 20, 10, '华为畅享 60X', '7000mAh大电池，66W快充，超大音量', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 0, 1799.00, 800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:50:19');
INSERT INTO `product` VALUES (9, 20, 10, '华为 Mate 40 Pro', '麒麟9000，88°超曲环幕屏，电影镜头', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 0, 4899.00, 1800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:50:19');
INSERT INTO `product` VALUES (10, 20, 10, '华为 P50 Pocket', '万象双环设计，超光谱影像系统', '/uploads/product/2026/02/28/bb2be54d-d8d6-4fe3-bc19-327f906d14fd.png', 0, 8988.00, 3800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:50:19');
INSERT INTO `product` VALUES (11, 21, 11, 'iPhone 15 Pro Max', '钛金属边框，A17 Pro芯片，5倍光学变焦', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 1, 9999.00, 5000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (12, 21, 11, 'iPhone 15 Plus', 'A16仿生芯片，灵动岛，USB-C接口', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 1, 6999.00, 3500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (13, 21, 11, 'iPhone 14 Pro', '灵动岛，A16芯片，4800万主摄', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 0, 5999.00, 2800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (14, 21, 11, 'iPhone 13', 'A15芯片，超视网膜XDR显示屏，IP68防水', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 0, 3999.00, 1800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (15, 21, 11, 'iPhone 15', 'A16芯片，融色玻璃背板，USB-C接口', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 1, 5999.00, 2900.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (16, 21, 11, 'iPhone 14 Plus', '6.7英寸大屏，A15芯片，长续航', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 0, 4999.00, 2400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (17, 21, 11, 'iPhone 12 Pro Max', 'A14芯片，激光雷达，6.7英寸屏幕', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 0, 4599.00, 2000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (18, 21, 11, 'iPhone SE 3', 'A15芯片，Home键，性价比小钢炮', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 0, 2999.00, 1300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (19, 21, 11, 'iPhone 11', 'A13芯片，双摄系统，多彩配色', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 0, 2499.00, 1000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (20, 21, 11, 'iPhone 14 Pro Max', 'A16芯片，全天候显示，车祸检测', '/uploads/product/2026/02/28/dcc8a9c0-aebf-4195-a6bf-93b3d17c399f.jpg', 0, 7999.00, 3800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (21, 22, 12, 'Galaxy S24 Ultra', '钛合金边框，AI助手，2亿像素主摄', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 1, 9699.00, 4800.00, 3324, 1, '2026-02-28 11:28:16', '2026-04-03 12:45:22');
INSERT INTO `product` VALUES (22, 22, 12, 'Galaxy Z Fold 6', '超薄柔性玻璃，IPX8防水，多任务处理', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 1, 13999.00, 6800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (23, 22, 12, 'Galaxy S23 Ultra', '2亿像素，超视觉夜拍，S Pen', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 0, 7299.00, 3500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (24, 22, 12, 'Galaxy Z Flip 6', '掌心折叠设计，大外屏，轻便时尚', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 1, 8999.00, 4400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (25, 22, 12, 'Galaxy A54', '120Hz高刷屏，5000万像素，性价比之选', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 0, 2499.00, 1100.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (26, 22, 12, 'Galaxy S22 Ultra', '方正设计，S Pen，钛金属装甲', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 0, 5899.00, 2800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (27, 22, 12, 'Galaxy Note 20 Ultra', '120Hz自适应刷新率，激光对焦', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 0, 4599.00, 2000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (28, 22, 12, 'Galaxy S21 FE', '多彩配色，骁龙888，高性价比', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 0, 2999.00, 1300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (29, 22, 12, 'Galaxy Z Fold 5', '水滴铰链，轻薄设计，多任务视窗', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 0, 11999.00, 5800.00, 101, 1, '2026-02-28 11:28:16', '2026-03-01 18:30:31');
INSERT INTO `product` VALUES (30, 22, 12, 'Galaxy A14', '入门级5G，大电池，长续航', '/uploads/product/2026/02/28/d9e31759-906f-4206-b23a-d3a6717e5462.png', 0, 1299.00, 500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (31, 23, 13, '小米 14 Ultra', '徕卡四摄，龙晶陶瓷，卫星通信', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 1, 6499.00, 3200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (32, 23, 13, '小米 14 Pro', '全等深微曲屏，光影猎人900，澎湃OS', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 1, 4999.00, 2400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (33, 23, 13, 'Redmi K70 Pro', '第二代骁龙8，2K中国屏，性能旗舰', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 1, 3299.00, 1500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (34, 23, 13, '小米 Civi 4 Pro', '徕卡影像，轻薄机身，自拍神器', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 1, 2999.00, 1400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (35, 23, 13, 'Redmi Note 13 Pro+', '2亿像素，曲面屏，120W快充', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 1, 1999.00, 900.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (36, 23, 13, '小米 13', '经典小尺寸，直屏设计，徕卡影像', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 0, 3499.00, 1600.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (37, 23, 13, 'Redmi K60', '2K屏幕，骁龙8+，无线充电', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 0, 2299.00, 1000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (38, 23, 13, 'Redmi Note 12 Turbo', '第二代骁龙7+，轻薄设计，高性价比', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 0, 1699.00, 700.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (39, 23, 13, '小米 MIX Fold 3', '龙骨转轴，四摄全焦段，轻薄折叠屏', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 0, 8999.00, 4200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (40, 23, 13, 'Redmi 12', '入门级5G，大电池，实用主义', '/uploads/product/2026/02/28/f1d4b9f8-d36d-4c4f-9653-38d3252d00d9.png', 0, 999.00, 400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (41, 24, 10, '华为 MatePad Pro 13.2', '13.2英寸柔性OLED，星闪技术，PC级应用引擎', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 1, 7999.00, 3800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (42, 24, 10, '华为 MatePad Air', '11.5英寸，3.1K高刷屏，多屏同色', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 1, 3999.00, 1800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (43, 24, 10, '华为 MatePad 11', '120Hz高刷屏，鸿蒙系统，轻办公首选', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 0, 2499.00, 1100.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (44, 24, 10, '华为 MatePad SE', '入门级平板，大电池，适合儿童网课', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 0, 1099.00, 450.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (45, 24, 10, '华为 MatePad Pro 11 2024', 'OLED全面屏，星闪连接，手写笔支持', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 1, 4299.00, 2000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (46, 24, 10, '华为 MatePad Paper', '10.3英寸墨水平板，类纸质感，护眼阅读', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 0, 2999.00, 1300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (47, 24, 10, '华为 MatePad 10.4', '2K全面屏，M-Pen lite手写笔', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 0, 1799.00, 750.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (48, 24, 10, '华为 MatePad Pro 12.6', 'OLED屏幕，多设备协同，专业生产力', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 0, 5999.00, 2800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (49, 24, 10, '华为 MatePad T10s', '入门影音娱乐，大音量，适合老人小孩', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 0, 899.00, 350.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (50, 24, 10, '华为 MatePad Pro 10.8', 'M-Pencil，智能磁吸键盘，移动办公', '/uploads/product/2026/02/28/b140c788-d0a1-443c-be82-fa806cccd92c.png', 0, 3299.00, 1500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (51, 25, 11, 'iPad Pro 13 (M4)', 'M4芯片，双层OLED屏，超薄设计', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 1, 11999.00, 5800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (52, 25, 11, 'iPad Air 13 (M2)', 'M2芯片，13英寸大屏，性价比高', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 1, 5999.00, 2800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (53, 25, 11, 'iPad 10', 'A14芯片，全面屏设计，USB-C接口', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 0, 3599.00, 1600.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (54, 25, 11, 'iPad mini 6', 'A15芯片，8.3英寸，游戏娱乐神器', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 0, 3299.00, 1500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (55, 25, 11, 'iPad Pro 11 (M2)', 'M2芯片，Liquid视网膜屏，ProMotion', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 1, 6999.00, 3300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (56, 25, 11, 'iPad Air 11 (M2)', 'M2芯片，11英寸，轻薄便携', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 1, 4799.00, 2200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (57, 25, 11, 'iPad 9', 'A13芯片，10.2英寸，教育优惠首选', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 0, 2499.00, 1000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (58, 25, 11, 'iPad Pro 12.9 (M1)', 'M1芯片，XDR显示屏，极致生产力', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 0, 8999.00, 4200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (59, 25, 11, 'iPad mini 5', 'A15芯片，经典小屏，便携性强', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 0, 2999.00, 1300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (60, 25, 11, 'iPad Air 4', 'A14芯片，全面屏，多彩配色', '/uploads/product/2026/02/28/30a6164f-34d0-42ee-bd25-d57cb328a86f.jpg', 0, 3999.00, 1800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (61, 26, 12, 'Galaxy Tab S9 Ultra', '14.6英寸AMOLED，S Pen，骁龙8 Gen 2', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 8999.00, 4200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (62, 26, 12, 'Galaxy Tab S9 FE+', '12.4英寸，Exynos 1380，性价比之选', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 1, 4299.00, 2000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (63, 26, 12, 'Galaxy Tab S8 Ultra', '14.6英寸，S Pen，办公利器', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 7299.00, 3300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (64, 26, 12, 'Galaxy Tab A8', '入门级平板，大电池，影音娱乐', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 1299.00, 550.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (65, 26, 12, 'Galaxy Tab S9', '11英寸，骁龙8 Gen 2，轻薄设计', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 4999.00, 2300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (66, 26, 12, 'Galaxy Tab S7 FE', '12.4英寸，大屏办公，性价比高', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 2999.00, 1300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (67, 26, 12, 'Galaxy Tab S6 Lite', 'S Pen included，轻办公，网课首选', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 1999.00, 850.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (68, 26, 12, 'Galaxy Tab A9+', '入门级，高性价比，适合家庭使用', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 1, 1499.00, 600.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (69, 26, 12, 'Galaxy Tab S8+', '12.4英寸，骁龙8 Gen 1，多任务处理', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 5999.00, 2700.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (70, 26, 12, 'Galaxy Tab Active 4 Pro', '三防平板，军工级标准，户外专用', '/uploads/product/2026/02/28/5cf66a2a-5ebf-492d-bd7e-be4fd042161e.jpg', 0, 4599.00, 2000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (71, 27, 13, '小米平板 6S Pro', '12.4英寸，骁龙8 Gen 2，PC级应用引擎', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 1, 3499.00, 1600.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (72, 27, 13, '小米平板 6 Max', '14英寸巨屏，办公影音，分屏协作', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 1, 3999.00, 1800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (73, 27, 13, '小米平板 6', '11英寸，2.8K屏幕，骁龙870', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 0, 1999.00, 850.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (74, 27, 13, 'Redmi Pad Pro', '12.1英寸，大电池，影音娱乐', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 1, 1699.00, 700.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (75, 27, 13, '小米平板 5 Pro', '11英寸，骁龙870，哈曼卡顿调音', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 0, 2499.00, 1000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (76, 27, 13, 'Redmi Pad', '入门级平板，全金属一体机身，护眼屏', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 0, 1199.00, 500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (77, 27, 13, '小米平板 6 Pro', '11英寸，骁龙8+，2.8K超清屏', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 0, 2699.00, 1100.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (78, 27, 13, '小米平板 5', '11英寸，骁龙870，33W快充', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 0, 1799.00, 700.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (79, 27, 13, 'Redmi Pad SE', '入门级，高性价比，适合学生网课', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 1, 999.00, 400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (80, 27, 13, '小米平板 4 Plus', '经典老款，大屏看剧，价格实惠', '/uploads/product/2026/02/28/961bfc6b-7dbe-40fc-9f5e-2c5995c04cdb.png', 0, 1299.00, 450.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (81, 28, 10, '华为 MateBook X Pro', '3.1K原色触控屏，3:2生产力比例，轻薄本', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 1, 8999.00, 4200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (82, 28, 10, '华为 MateBook 16s', '16英寸大屏，标压处理器，高性能创作本', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 1, 6999.00, 3200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (83, 28, 10, '华为 MateBook 14', '2K触控屏，多屏协同，商务办公首选', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 0, 5499.00, 2500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (84, 28, 10, '华为 MateBook D 16', '16英寸大屏，高性价比，家用娱乐', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 1, 4299.00, 1800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (85, 28, 10, '华为 MateBook GT 14', '首款鸿蒙游戏本，双翼散热，性能强劲', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 1, 6999.00, 3200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (86, 28, 10, '华为 MateBook E Go', '二合一笔记本，平板+键盘，轻便移动办公', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 0, 4999.00, 2200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (87, 28, 10, '华为 MateBook 13s', '2.5K触控屏，标压处理器，轻薄全能本', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 0, 5999.00, 2700.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (88, 28, 10, '华为 MateBook D 14', '入门级轻薄本，高性价比，学生首选', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 0, 3999.00, 1600.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (89, 28, 10, '华为 MateBook X', '极致轻薄，无风扇设计，高端商务', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 0, 12999.00, 5800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (90, 28, 10, '华为 MateStation X', '一体机，28.2英寸触控屏，高端创作', '/uploads/product/2026/02/28/d69cf73e-11b4-4331-b4c0-a3918d9b1a12.png', 0, 9999.00, 4500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (91, 29, 11, 'MacBook Pro 16 (M4 Max)', 'M4 Max芯片，Liquid视网膜XDR屏，极致性能', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 1, 21999.00, 9800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (92, 29, 11, 'MacBook Air 13 (M2)', 'M2芯片，无风扇设计，极致轻薄', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 0, 7999.00, 3500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (93, 29, 11, 'MacBook Pro 14 (M3 Pro)', 'M3 Pro芯片，120Hz高刷屏，专业创作', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 1, 13999.00, 6200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (94, 29, 11, 'MacBook Air 15 (M2)', '15.3英寸大屏，M2芯片，便携大屏', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 1, 9499.00, 4200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (95, 29, 11, 'MacBook Pro 13 (M2)', 'M2芯片，Touch Bar，经典商务本', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 0, 9999.00, 4400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (96, 29, 11, 'MacBook Air 13 (M1)', 'M1芯片，性价比高，适合学生和办公', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 0, 6499.00, 2800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (97, 29, 11, 'MacBook Pro 16 (M1 Max)', 'M1 Max芯片，大屏高性能，老款经典', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 0, 16999.00, 7500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:55:00');
INSERT INTO `product` VALUES (98, 29, 11, 'MacBook Air 13 (M3)', 'M3芯片，性能提升，轻薄便携', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 1, 8999.00, 3900.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (99, 29, 11, 'MacBook Pro 14 (M2)', 'M2芯片，14英寸，全能生产力工具', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 0, 11999.00, 5300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (100, 29, 11, 'MacBook Pro 13 (M1)', 'M1芯片，经典款，长续航', '/uploads/product/2026/02/28/966bdeb1-c6cd-484a-b8a4-8bce417ef1fd.png', 0, 8999.00, 3800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (101, 30, 12, 'Galaxy Book4 Ultra', 'RTX4050显卡，AMOLED屏，创作本', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 1, 10999.00, 4800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (102, 30, 12, 'Galaxy Book4 Pro', '2.8K AMOLED屏，轻薄本，S Pen支持', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 1, 7999.00, 3500.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (103, 30, 12, 'Galaxy Book3 Pro 360', '翻转触控屏，S Pen，二合一设计', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 0, 8999.00, 3800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (104, 30, 12, 'Galaxy Book2 Pro', 'AMOLED屏，轻薄设计，商务办公', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 0, 6499.00, 2800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (105, 30, 12, 'Galaxy Book Odyssey', '游戏本，RTX3050，高刷新率屏幕', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 0, 6999.00, 3000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (106, 30, 12, 'Galaxy Book Flex 2', '翻转本，AMOLED触控屏，轻薄便携', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 0, 7499.00, 3200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (107, 30, 12, 'Galaxy Book Go', '入门级笔记本，骁龙处理器，长续航', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 0, 3999.00, 1600.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (108, 30, 12, 'Galaxy Book Ion', '超轻薄本，AMOLED屏，高端商务', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 0, 8999.00, 3800.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (109, 30, 12, 'Galaxy Book Pro 360', '经典翻转本，S Pen，全能办公', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 0, 7999.00, 3300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (110, 30, 12, 'Galaxy Book4 360', '二合一设计，AMOLED屏，多模式使用', '/uploads/product/2026/02/28/cd8b6f49-5a1c-42a3-bb2e-f01ffed6b08b.png', 1, 6999.00, 3000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (111, 31, 13, '小米笔记本 Pro 16', '16英寸3.2K屏，标压处理器，高性能轻薄本', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 1, 5999.00, 2600.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (112, 31, 13, '小米笔记本 Pro X 15', 'RTX3050Ti，3.5K OLED屏，创作本', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 0, 7499.00, 3200.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (113, 31, 13, 'Redmi Book Pro 14', '2.8K高刷屏，轻薄本，性价比高', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 1, 4499.00, 1900.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (114, 31, 13, 'Redmi Book 14', '入门级轻薄本，高性价比，学生办公', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 0, 3299.00, 1300.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (115, 31, 13, '小米笔记本 Air 13.3', '经典轻薄本，金属机身，便携性强', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 0, 4999.00, 2000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (116, 31, 13, 'Redmi Book Pro 16', '16英寸大屏，标压处理器，全能本', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 1, 4999.00, 2100.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (117, 31, 13, 'Redmi G 游戏本', 'RTX4060，165Hz高刷屏，硬核游戏本', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 1, 6999.00, 3000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (118, 31, 13, '小米笔记本 Pro 14', '2.8K屏，标压处理器，轻薄全能', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 0, 4699.00, 2000.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (119, 31, 13, 'Redmi Air 13', '极致轻薄，入门级，高性价比', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 0, 2999.00, 1100.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (120, 31, 13, '小米游戏本 2019', '经典老款游戏本，性价比高，适合入门游戏', '/uploads/product/2026/02/28/73809b41-4d3d-405d-bb5b-6330fe1239f0.png', 0, 5999.00, 2400.00, 100, 1, '2026-02-28 11:28:16', '2026-03-01 10:54:59');
INSERT INTO `product` VALUES (121, 20, 10, '华为nova9', '修改后的商品描述', NULL, 1, 6999.00, 4000.00, 40, 0, '2026-03-02 12:52:21', '2026-03-31 11:35:03');
INSERT INTO `product` VALUES (123, 20, 10, 'iPhone 15 128G', '苹果最新款智能手机，128GB存储空间', 'https://example.com/images/iphone15.jpg', 1, 5999.00, 3500.00, 100, 1, '2026-03-31 11:27:50', '2026-03-31 11:27:50');
INSERT INTO `product` VALUES (124, 20, 10, 'iPhone 15 128G', '苹果最新款智能手机，128GB存储空间', 'https://example.com/images/iphone15.jpg', 1, 5999.00, 3500.00, 100, 1, '2026-03-31 11:29:40', '2026-03-31 11:29:40');
INSERT INTO `product` VALUES (125, 20, 10, 'iPhone 15 128G', '苹果最新款智能手机，128GB存储空间', 'https://example.com/images/iphone15.jpg', 1, 5999.00, 3500.00, 100, 1, '2026-03-31 11:29:51', '2026-03-31 11:29:51');

-- ----------------------------
-- Table structure for productbrand
-- ----------------------------
DROP TABLE IF EXISTS `productbrand`;
CREATE TABLE `productbrand`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '品牌名称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌logo',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  `sort` int NOT NULL DEFAULT 0,
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品品牌表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of productbrand
-- ----------------------------
INSERT INTO `productbrand` VALUES (10, '华为', '/uploads/brand/2026/02/28/faf0d383-944b-4865-9a3c-2dc79d6935fb.png', 1, 2, '2026-02-28 11:05:29', '2026-02-28 13:31:42');
INSERT INTO `productbrand` VALUES (11, '苹果', '/uploads/brand/2026/02/28/97414808-6cd5-4bdb-896c-40206afe2649.png', 1, 1, '2026-02-28 11:05:29', '2026-02-28 13:31:50');
INSERT INTO `productbrand` VALUES (12, '三星', '/uploads/brand/2026/02/28/cdfc00b7-a17e-4dcb-a045-e289838018d2.png', 1, 4, '2026-02-28 11:05:29', '2026-02-28 13:31:57');
INSERT INTO `productbrand` VALUES (13, '小米', '/uploads/brand/2026/02/28/ff0f50d2-d518-4a9a-81f1-6ff50ce8995c.png', 1, 3, '2026-02-28 11:05:29', '2026-02-28 13:32:04');

-- ----------------------------
-- Table structure for productcategory
-- ----------------------------
DROP TABLE IF EXISTS `productcategory`;
CREATE TABLE `productcategory`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parentId` bigint NULL DEFAULT NULL COMMENT '父级ID, 顶级为NULL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idxCategoryParent`(`parentId` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of productcategory
-- ----------------------------
INSERT INTO `productcategory` VALUES (17, '手机', NULL, 1, 0, '2026-02-28 11:07:11', '2026-03-31 12:07:16');
INSERT INTO `productcategory` VALUES (18, '平板', NULL, 1, 0, '2026-02-28 11:07:11', '2026-02-28 11:07:11');
INSERT INTO `productcategory` VALUES (19, '笔记本', NULL, 1, 0, '2026-02-28 11:07:11', '2026-02-28 11:07:11');
INSERT INTO `productcategory` VALUES (20, '华为手机', 17, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (21, '苹果手机', 17, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (22, '三星手机', 17, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (23, '小米手机', 17, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (24, '华为平板', 18, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (25, '苹果平板', 18, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (26, '三星平板', 18, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (27, '小米平板', 18, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (28, '华为笔记本', 19, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (29, '苹果笔记本', 19, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (30, '三星笔记本', 19, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (31, '小米笔记本', 19, 1, 0, '2026-02-28 11:11:12', '2026-02-28 11:11:12');
INSERT INTO `productcategory` VALUES (32, '智能手表', NULL, 1, 0, '2026-03-31 11:49:24', '2026-03-31 11:49:24');
INSERT INTO `productcategory` VALUES (33, '智能穿戴', NULL, 1, 0, '2026-03-31 11:50:12', '2026-03-31 11:54:03');
INSERT INTO `productcategory` VALUES (34, '智能手表', NULL, 1, 0, '2026-03-31 11:51:02', '2026-03-31 11:51:02');

-- ----------------------------
-- Table structure for recycleorder
-- ----------------------------
DROP TABLE IF EXISTS `recycleorder`;
CREATE TABLE `recycleorder`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orderNo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回收订单编号',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `productId` bigint NOT NULL COMMENT '回收的型号ID',
  `addressId` bigint NULL DEFAULT NULL COMMENT '上门/寄回地址快照ID',
  `productSnapshot` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '型号快照(防止后续改名)',
  `grade` enum('A','B','C','D') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'B' COMMENT '成色等级',
  `appearanceDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外观情况描述',
  `functionDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '功能情况描述',
  `estimatePrice` decimal(10, 2) NOT NULL COMMENT '线上估价金额',
  `finalPrice` decimal(10, 2) NULL DEFAULT NULL COMMENT '质检后最终价格',
  `status` enum('SUBMITTED','WAITING_INSPECTION','INSPECTING','WAIT_CONFIRM','FINISHED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SUBMITTED' COMMENT '已提交/等待质检/质检中/等待确认/已完成/已取消',
  `payType` enum('CASH','COUPON') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'CASH' COMMENT '打款方式',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `orderNo`(`orderNo` ASC) USING BTREE,
  INDEX `fkRecycleProduct`(`productId` ASC) USING BTREE,
  INDEX `fkRecycleAddress`(`addressId` ASC) USING BTREE,
  INDEX `idxRecycleUser`(`userId` ASC) USING BTREE,
  INDEX `idxRecycleStatus`(`status` ASC) USING BTREE,
  CONSTRAINT `fkRecycleAddress` FOREIGN KEY (`addressId`) REFERENCES `useraddress` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkRecycleProduct` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fkRecycleUser` FOREIGN KEY (`userId`) REFERENCES `sysuser` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3268 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回收订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recycleorder
-- ----------------------------
INSERT INTO `recycleorder` VALUES (3267, 'R2039927212065402880', 6, 21, 17, 'Galaxy S24 Ultra', 'B', '屏幕有轻微划痕，机身轻微使用痕迹', '所有功能正常，电池健康度80%', 1600.00, 2000.00, 'FINISHED', 'COUPON', '检测结果：机身完整，电池健康度为85%', '2026-04-03 12:45:22', '2026-04-03 12:45:22');

-- ----------------------------
-- Table structure for sysuser
-- ----------------------------
DROP TABLE IF EXISTS `sysuser`;
CREATE TABLE `sysuser`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录名(可用作帐号)',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码(加密后)',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `role` enum('USER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '角色:普通用户/管理员',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:1正常 0禁用',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 343 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户/管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sysuser
-- ----------------------------
INSERT INTO `sysuser` VALUES (6, 'admin', '$2a$10$c6GjsqfgOL023J5qJKNmXuhkGLwS2boo6PlCBjlyZnTJ/FuCi/4PK', '管理员', '/uploads/avatar/2026/03/31/606d577b-91f1-4811-be2f-0d1ddd07da98.jpg', 1069597.00, 'ADMIN', 1, '2026-02-28 10:57:06', '2026-04-03 12:31:54');
INSERT INTO `sysuser` VALUES (7, 'wild', '$2a$10$x.60o44zK6t8Lu6EG/d1M.UsyW74Qxf1.qDy1BiZNLYAD0CJHA0T2', '大只切', '/uploads/avatar/2026/02/28/0c08250b-5402-43a0-8f0a-13fe2a9f965d.jpg', 994201.00, 'USER', 1, '2026-02-28 10:58:44', '2026-04-02 21:18:18');
INSERT INTO `sysuser` VALUES (342, 'wild1', '$2a$10$O7gCPOTAGftXTuUwnPTO2uUYTFPQNCcjLufQiFoW3TTapaTvQrzky', '漂泊', NULL, 0.00, 'ADMIN', 1, '2026-04-03 12:45:18', '2026-04-03 12:45:18');

-- ----------------------------
-- Table structure for useraddress
-- ----------------------------
DROP TABLE IF EXISTS `useraddress`;
CREATE TABLE `useraddress`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `userId` bigint NOT NULL COMMENT '所属用户',
  `receiverName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiverPhone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `isDefault` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认地址 1是 0否',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fkAddressUser`(`userId` ASC) USING BTREE,
  CONSTRAINT `fkAddressUser` FOREIGN KEY (`userId`) REFERENCES `sysuser` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收货地址' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of useraddress
-- ----------------------------
INSERT INTO `useraddress` VALUES (17, 6, '倪哥', '12311112222', '江西省', '吉安市', '吉安县', '网易大厦', 1, '2026-02-28 20:27:18', '2026-03-31 08:45:12');
INSERT INTO `useraddress` VALUES (18, 6, 'nige1', '17879618746', '内蒙古自治区', '通辽市', '科尔沁左翼后旗', 'jindsasf', 0, '2026-02-28 20:40:37', '2026-03-30 14:12:19');
INSERT INTO `useraddress` VALUES (19, 7, '欧阳', '18799819211', '安徽省', '蚌埠市', '禹会区', '我的老家\n', 1, '2026-03-02 10:29:40', '2026-03-02 10:29:42');
INSERT INTO `useraddress` VALUES (23, 6, '扣克', '17878712341', '广东省', '深圳市', '南山区', '网易大厦D区1号楼216', 0, '2026-03-31 09:05:06', '2026-03-31 09:05:06');
INSERT INTO `useraddress` VALUES (24, 6, '扣克', '17878712341', '广东省', '深圳市', '南山区', '网易大厦D区1号楼216', 0, '2026-03-31 09:05:55', '2026-03-31 09:05:55');

SET FOREIGN_KEY_CHECKS = 1;
