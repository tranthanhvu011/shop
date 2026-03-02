/*
 Navicat Premium Data Transfer

 Source Server         : thanhvu
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3306
 Source Schema         : accountshop_db

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 02/03/2026 09:45:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `display_order` int NULL DEFAULT NULL,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKt8o6pivur7nn124jehx7cygw5`(`name` ASC) USING BTREE,
  UNIQUE INDEX `UKoul14ho7bctbefv8jywp5v3i2`(`slug` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------

-- ----------------------------
-- Table structure for conversations
-- ----------------------------
DROP TABLE IF EXISTS `conversations`;
CREATE TABLE `conversations`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `admin_unread_count` int NULL DEFAULT NULL,
  `last_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `last_message_at` datetime(6) NULL DEFAULT NULL,
  `user_unread_count` int NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKpltqvfcbkql9svdqwh0hw4g1d`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKpltqvfcbkql9svdqwh0hw4g1d` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conversations
-- ----------------------------
INSERT INTO `conversations` VALUES (1, '2026-02-26 11:08:34.705236', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 17:39:10.785483', 'vutranorhilsun@gmail.com', 16, 1, 'sao vậy cha', '2026-02-26 17:39:10.757202', 0, 2);
INSERT INTO `conversations` VALUES (2, '2026-02-26 17:39:52.100414', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:44:38.410735', 'lethicuba1004@gmail.com', 3, 3, '[Hỗ trợ đơn hàng #ORD-20260228-78BFD791] Tôi cần hỗ trợ về đơn hàng này.', '2026-02-28 20:44:38.386426', 0, 3);

-- ----------------------------
-- Table structure for coupons
-- ----------------------------
DROP TABLE IF EXISTS `coupons`;
CREATE TABLE `coupons`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `discount_type` enum('FIXED_AMOUNT','PERCENTAGE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `discount_value` decimal(12, 2) NOT NULL,
  `end_date` datetime(6) NULL DEFAULT NULL,
  `max_discount` decimal(12, 0) NULL DEFAULT NULL,
  `max_usage` int NULL DEFAULT NULL,
  `min_order_amount` decimal(12, 0) NULL DEFAULT NULL,
  `start_date` datetime(6) NULL DEFAULT NULL,
  `used_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKeplt0kkm9yf2of2lnx6c1oy9b`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupons
-- ----------------------------

-- ----------------------------
-- Table structure for digital_accounts
-- ----------------------------
DROP TABLE IF EXISTS `digital_accounts`;
CREATE TABLE `digital_accounts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `account_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `allocated_to_user_id` bigint NULL DEFAULT NULL,
  `order_id` bigint NULL DEFAULT NULL,
  `status` enum('ALLOCATED','AVAILABLE','SOLD') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `variant_pricing_id` bigint NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKkswdmeynqcgu0lbu8lafdeiwo`(`variant_pricing_id` ASC) USING BTREE,
  CONSTRAINT `FKkswdmeynqcgu0lbu8lafdeiwo` FOREIGN KEY (`variant_pricing_id`) REFERENCES `variant_pricing` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of digital_accounts
-- ----------------------------
INSERT INTO `digital_accounts` VALUES (1, '2026-02-26 12:05:34.268026', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:53:45.318974', 'vutranorhilsun@gmail.com', 1, 'dqwufasfas:fasbfasvfasf:fasjlbfashkvfas:fbasfhjvasf', NULL, 1, 'AVAILABLE', 1, NULL);
INSERT INTO `digital_accounts` VALUES (2, '2026-02-26 12:05:34.287667', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:53:45.318974', 'vutranorhilsun@gmail.com', 1, 'fasjbfhkasbf:fasjbfjkbas:fasbfbhas:fasfbashjkfbas', NULL, 1, 'AVAILABLE', 1, NULL);

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `message_type` enum('FILE','IMAGE','TEXT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sender_id` bigint NOT NULL,
  `sender_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sender_type` enum('ADMIN','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `conversation_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKt492th6wsovh1nush5yl5jj8e`(`conversation_id` ASC) USING BTREE,
  CONSTRAINT `FKt492th6wsovh1nush5yl5jj8e` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, '2026-02-26 11:08:38.910944', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 11:08:38.910944', 'vutranorhilsun@gmail.com', 0, 'admin', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'USER', 1);
INSERT INTO `messages` VALUES (2, '2026-02-26 11:09:59.651863', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 11:09:59.651863', 'vutranorhilsun@gmail.com', 0, 'hello', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (3, '2026-02-26 12:43:56.553292', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:43:56.553292', 'vutranorhilsun@gmail.com', 0, 'sao em', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (4, '2026-02-26 12:43:59.590691', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:43:59.590691', 'vutranorhilsun@gmail.com', 0, 'nhanh mẹ đi', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (5, '2026-02-26 12:44:02.088542', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:44:02.088542', 'vutranorhilsun@gmail.com', 0, 'dmm', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (6, '2026-02-26 12:50:44.563978', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:50:44.563978', 'vutranorhilsun@gmail.com', 0, 'fasfasfas', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (7, '2026-02-26 12:50:46.616167', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:50:46.616167', 'vutranorhilsun@gmail.com', 0, 'fasfasf', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (8, '2026-02-26 12:50:47.940322', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:50:47.940322', 'vutranorhilsun@gmail.com', 0, 'fasfas', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (9, '2026-02-26 12:50:49.685403', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:50:49.685403', 'vutranorhilsun@gmail.com', 0, 'fasfas', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (10, '2026-02-26 12:50:51.181378', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:50:51.181378', 'vutranorhilsun@gmail.com', 0, 'fasfas', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (11, '2026-02-26 12:50:56.079832', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:50:56.079832', 'vutranorhilsun@gmail.com', 0, 'fasfas', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'ADMIN', 1);
INSERT INTO `messages` VALUES (12, '2026-02-26 17:39:10.757202', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 17:39:10.757202', 'vutranorhilsun@gmail.com', 0, 'sao vậy cha', NULL, NULL, b'0', 'TEXT', 2, 'thanhvu', 'USER', 1);
INSERT INTO `messages` VALUES (13, '2026-02-27 14:06:05.036199', 'lethicuba1004@gmail.com', b'0', '2026-02-27 14:06:05.036199', 'lethicuba1004@gmail.com', 0, 'Admin ơi', NULL, NULL, b'0', 'TEXT', 3, 'thanhvu123', 'USER', 2);
INSERT INTO `messages` VALUES (14, '2026-02-28 20:43:14.600572', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:43:14.600572', 'lethicuba1004@gmail.com', 0, '[Hỗ trợ đơn hàng #ORD-20260228-78BFD791] Tôi cần hỗ trợ về đơn hàng này.', NULL, NULL, b'0', 'TEXT', 3, 'thanhvu123', 'USER', 2);
INSERT INTO `messages` VALUES (15, '2026-02-28 20:44:38.386426', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:44:38.386426', 'lethicuba1004@gmail.com', 0, '[Hỗ trợ đơn hàng #ORD-20260228-78BFD791] Tôi cần hỗ trợ về đơn hàng này.', NULL, NULL, b'0', 'TEXT', 3, 'thanhvu123', 'USER', 2);

-- ----------------------------
-- Table structure for order_complaints
-- ----------------------------
DROP TABLE IF EXISTS `order_complaints`;
CREATE TABLE `order_complaints`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `admin_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `resolved_at` datetime(6) NULL DEFAULT NULL,
  `status` enum('CLOSED','OPEN','RESOLVED','RESPONDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK4y0neyxcq1baj9jnbgftdnell`(`order_id` ASC) USING BTREE,
  INDEX `FKd4d39nd3ilnwqgfmcsnb7nup2`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK4y0neyxcq1baj9jnbgftdnell` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKd4d39nd3ilnwqgfmcsnb7nup2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_complaints
-- ----------------------------

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `account_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `duration_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `login_guide` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `pricing_id` bigint NULL DEFAULT NULL,
  `product_id` bigint NULL DEFAULT NULL,
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `product_slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `quantity` int NOT NULL,
  `seller_contact_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `subtotal` decimal(12, 0) NULL DEFAULT NULL,
  `two_factor_guide` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `unit_price` decimal(12, 0) NULL DEFAULT NULL,
  `variant_id` bigint NULL DEFAULT NULL,
  `variant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKbioxgbv59vetrxe0ejfubep1w`(`order_id` ASC) USING BTREE,
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, '2026-02-26 12:53:39.770444', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:53:45.315835', 'vutranorhilsun@gmail.com', 1, '[{\"accountInfo\":\"dqwufasfas:fasbfasvfasf:fasjlbfashkvfas:fbasfhjvasf\"},{\"accountInfo\":\"fasjbfhkasbf:fasjbfjkbas:fasbfbhas:fasfbashjkfbas\"}]', '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 10, NULL, 5000000, NULL, 500000, 1, 'Plus', 1);
INSERT INTO `order_items` VALUES (2, '2026-02-27 15:07:29.187486', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:07:29.187486', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 2);
INSERT INTO `order_items` VALUES (3, '2026-02-27 15:10:40.499824', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:10:40.499824', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 3);
INSERT INTO `order_items` VALUES (4, '2026-02-27 15:18:22.996588', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:22.996588', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 4);
INSERT INTO `order_items` VALUES (5, '2026-02-27 15:18:26.025723', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:26.025723', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 5);
INSERT INTO `order_items` VALUES (6, '2026-02-27 15:18:37.677892', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:37.677892', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 6);
INSERT INTO `order_items` VALUES (7, '2026-02-27 15:18:39.401109', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:39.401109', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 7);
INSERT INTO `order_items` VALUES (8, '2026-02-27 15:18:40.479987', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:40.479987', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 8);
INSERT INTO `order_items` VALUES (9, '2026-02-27 15:18:41.543853', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:41.543853', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 9);
INSERT INTO `order_items` VALUES (10, '2026-02-27 15:18:42.947918', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:42.947918', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 10);
INSERT INTO `order_items` VALUES (11, '2026-02-27 15:24:12.246364', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:24:12.246364', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 500000, NULL, 500000, 1, 'Plus', 11);
INSERT INTO `order_items` VALUES (12, '2026-02-28 17:53:49.475725', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:53:49.475725', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 12);
INSERT INTO `order_items` VALUES (13, '2026-02-28 17:54:06.031681', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:54:06.031681', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 13);
INSERT INTO `order_items` VALUES (14, '2026-02-28 17:56:10.503534', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:56:10.503534', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 14);
INSERT INTO `order_items` VALUES (15, '2026-02-28 17:58:21.706938', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:58:21.706938', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 15);
INSERT INTO `order_items` VALUES (16, '2026-02-28 18:00:08.203724', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:00:08.203724', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 16);
INSERT INTO `order_items` VALUES (17, '2026-02-28 18:08:52.197852', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:08:52.197852', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 17);
INSERT INTO `order_items` VALUES (18, '2026-02-28 18:09:11.159116', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:09:11.159116', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 18);
INSERT INTO `order_items` VALUES (19, '2026-02-28 18:10:03.842924', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:10:03.842924', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 19);
INSERT INTO `order_items` VALUES (20, '2026-02-28 18:11:13.309218', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:11:13.309218', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 20);
INSERT INTO `order_items` VALUES (21, '2026-02-28 18:16:19.087829', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:16:19.087829', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 3, NULL, 1500000, NULL, 500000, 1, 'Plus', 21);
INSERT INTO `order_items` VALUES (22, '2026-02-28 18:18:05.682519', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:18:05.682519', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 22);
INSERT INTO `order_items` VALUES (23, '2026-02-28 18:22:57.576880', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:22:57.576880', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 23);
INSERT INTO `order_items` VALUES (24, '2026-02-28 19:39:45.796374', 'lethicuba1004@gmail.com', b'0', '2026-02-28 19:39:45.796374', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 24);
INSERT INTO `order_items` VALUES (25, '2026-02-28 19:58:39.667742', 'lethicuba1004@gmail.com', b'0', '2026-02-28 19:58:39.667742', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 25);
INSERT INTO `order_items` VALUES (26, '2026-02-28 20:03:28.076008', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:03:28.076008', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 26);
INSERT INTO `order_items` VALUES (27, '2026-02-28 20:20:19.825483', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:20:19.825483', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 27);
INSERT INTO `order_items` VALUES (28, '2026-02-28 20:21:33.841946', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:21:33.841946', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 28);
INSERT INTO `order_items` VALUES (29, '2026-02-28 20:23:14.830490', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:23:14.830490', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 29);
INSERT INTO `order_items` VALUES (30, '2026-02-28 20:23:59.029608', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:23:59.029608', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 30);
INSERT INTO `order_items` VALUES (31, '2026-02-28 20:26:11.197546', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:26:11.197546', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 31);
INSERT INTO `order_items` VALUES (32, '2026-02-28 20:33:06.060639', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:33:06.060639', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 32);
INSERT INTO `order_items` VALUES (33, '2026-02-28 20:33:36.154650', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:33:36.154650', 'lethicuba1004@gmail.com', 0, NULL, '1 tháng', NULL, 1, 1, '', 'ChatGPT Plus', 'chatgpt-plus', 1, NULL, 2000, NULL, 2000, 1, 'Plus', 33);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `coupon_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `discount_amount` decimal(12, 0) NULL DEFAULT NULL,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `order_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `payment_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `payment_status` enum('PAID','PENDING','REFUNDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('CANCELLED','COMPLETED','PENDING','PROCESSING','REFUNDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `subtotal` decimal(12, 0) NULL DEFAULT NULL,
  `total_amount` decimal(12, 0) NULL DEFAULT NULL,
  `user_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKnthkiu7pgmnqnu86i2jyoe2v7`(`order_number` ASC) USING BTREE,
  INDEX `FK32ql8ubntj5uh44ph9659tiih`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, '2026-02-26 12:53:39.733299', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:53:45.315835', 'vutranorhilsun@gmail.com', 1, '', 0, '', 'ORD-20260226-1EF240C6', 'BANK_TRANSFER', 'PAID', 'COMPLETED', 5000000, 5000000, 'vutranorhilsun@gmail.com', 2);
INSERT INTO `orders` VALUES (2, '2026-02-27 15:07:29.169214', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:07:29.169214', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-93753CC8', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (3, '2026-02-27 15:10:40.499302', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:10:40.499302', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-657837D4', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (4, '2026-02-27 15:18:22.994032', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:22.994032', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-08D82FEF', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (5, '2026-02-27 15:18:26.019684', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:26.019684', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-C41A590A', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (6, '2026-02-27 15:18:37.669701', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:37.669701', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-E4DF5C9F', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (7, '2026-02-27 15:18:39.397720', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:39.397720', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-9C07EAF7', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (8, '2026-02-27 15:18:40.477781', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:40.477781', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-59ED89CC', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (9, '2026-02-27 15:18:41.540339', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:41.540339', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-4D87160C', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (10, '2026-02-27 15:18:42.946904', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:18:42.946904', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-2771F6CE', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (11, '2026-02-27 15:24:12.215510', 'lethicuba1004@gmail.com', b'0', '2026-02-27 15:24:12.215510', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260227-E8E74881', 'BANK_TRANSFER', 'PENDING', 'PENDING', 500000, 500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (12, '2026-02-28 17:53:49.444612', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:53:49.444612', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-A7FF01C9', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (13, '2026-02-28 17:54:06.030657', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:54:06.030657', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-2AF145B6', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (14, '2026-02-28 17:56:10.465724', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:56:10.465724', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-38DF5904', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (15, '2026-02-28 17:58:21.676233', 'lethicuba1004@gmail.com', b'0', '2026-02-28 17:58:21.676233', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-438C6AB2', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (16, '2026-02-28 18:00:08.169933', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:00:08.169933', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-D5B2D6CC', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (17, '2026-02-28 18:08:52.191844', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:08:52.191844', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-D56916A9', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (18, '2026-02-28 18:09:11.124123', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:09:11.124123', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-1DE0AB3F', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (19, '2026-02-28 18:10:03.840630', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:10:03.840630', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-5281DE25', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (20, '2026-02-28 18:11:13.271665', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:11:13.271665', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-09429737', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (21, '2026-02-28 18:16:19.053414', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:16:19.053414', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-4273B0E9', 'BANK_TRANSFER', 'PENDING', 'PENDING', 1500000, 1500000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (22, '2026-02-28 18:18:05.679306', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:18:05.679306', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-E8B59731', 'BANK_TRANSFER', 'PENDING', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (23, '2026-02-28 18:22:57.546473', 'lethicuba1004@gmail.com', b'0', '2026-02-28 18:22:57.546473', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-6166E620', 'BANK_TRANSFER', 'PENDING', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (24, '2026-02-28 19:39:45.756969', 'lethicuba1004@gmail.com', b'0', '2026-02-28 19:39:45.756969', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-96CF13BE', 'BANK_TRANSFER', 'PENDING', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (25, '2026-02-28 19:58:39.624595', 'lethicuba1004@gmail.com', b'0', '2026-02-28 19:58:39.624595', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-A8A303B3', 'BANK_TRANSFER', 'PENDING', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (26, '2026-02-28 20:03:28.039280', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:03:41.411489', 'system', 1, '', 0, '', 'ORD-20260228-BFBB6C20', 'BANK_TRANSFER', 'PAID', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (27, '2026-02-28 20:20:19.788745', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:20:34.889549', 'system', 1, '', 0, '', 'ORD-20260228-6AA317F2', 'BANK_TRANSFER', 'PAID', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (28, '2026-02-28 20:21:33.810114', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:21:46.895400', 'system', 1, '', 0, '', 'ORD-20260228-E9A055CD', 'BANK_TRANSFER', 'PAID', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (29, '2026-02-28 20:23:14.789992', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:23:27.797521', 'system', 1, '', 0, '', 'ORD-20260228-F533ABD3', 'BANK_TRANSFER', 'PAID', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (30, '2026-02-28 20:23:59.027547', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:23:59.027547', 'lethicuba1004@gmail.com', 0, '', 0, '', 'ORD-20260228-A9A8253F', 'BANK_TRANSFER', 'PENDING', 'PENDING', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (31, '2026-02-28 20:26:11.163922', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:26:24.973935', 'system', 1, '', 0, '', 'ORD-20260228-ED6E079A', 'BANK_TRANSFER', 'PAID', 'COMPLETED', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (32, '2026-02-28 20:33:06.027414', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:33:22.990822', 'system', 1, '', 0, '', 'ORD-20260228-FB08F181', 'BANK_TRANSFER', 'PAID', 'COMPLETED', 2000, 2000, 'lethicuba1004@gmail.com', 3);
INSERT INTO `orders` VALUES (33, '2026-02-28 20:33:36.152481', 'lethicuba1004@gmail.com', b'0', '2026-02-28 20:33:46.206163', 'system', 1, '', 0, '', 'ORD-20260228-78BFD791', 'BANK_TRANSFER', 'PAID', 'COMPLETED', 2000, 2000, 'lethicuba1004@gmail.com', 3);

-- ----------------------------
-- Table structure for product_images
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `display_order` int NULL DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_primary` bit(1) NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKqnq71xsohugpqwf3c9gxmsuy`(`product_id` ASC) USING BTREE,
  CONSTRAINT `FKqnq71xsohugpqwf3c9gxmsuy` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_images
-- ----------------------------

-- ----------------------------
-- Table structure for product_questions
-- ----------------------------
DROP TABLE IF EXISTS `product_questions`;
CREATE TABLE `product_questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `answered_at` datetime(6) NULL DEFAULT NULL,
  `answered_by_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `asker_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK7f2xyt2llt94j7f2eh61flj1g`(`asker_id` ASC) USING BTREE,
  INDEX `FKmb74hgsft9ibnqeb5kqile518`(`product_id` ASC) USING BTREE,
  CONSTRAINT `FK7f2xyt2llt94j7f2eh61flj1g` FOREIGN KEY (`asker_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKmb74hgsft9ibnqeb5kqile518` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_questions
-- ----------------------------

-- ----------------------------
-- Table structure for product_reviews
-- ----------------------------
DROP TABLE IF EXISTS `product_reviews`;
CREATE TABLE `product_reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `order_id` bigint NULL DEFAULT NULL,
  `order_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `rating` int NOT NULL,
  `buyer_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKt4rbippqp3w09yvpsph8tokww`(`buyer_id` ASC) USING BTREE,
  INDEX `FK35kxxqe2g9r4mww80w9e3tnw9`(`product_id` ASC) USING BTREE,
  CONSTRAINT `FK35kxxqe2g9r4mww80w9e3tnw9` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKt4rbippqp3w09yvpsph8tokww` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_reviews
-- ----------------------------

-- ----------------------------
-- Table structure for product_variants
-- ----------------------------
DROP TABLE IF EXISTS `product_variants`;
CREATE TABLE `product_variants`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `login_guide` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seller_contact_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `two_factor_guide` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKosqitn4s405cynmhb87lkvuau`(`product_id` ASC) USING BTREE,
  CONSTRAINT `FKosqitn4s405cynmhb87lkvuau` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_variants
-- ----------------------------
INSERT INTO `product_variants` VALUES (1, '2026-02-26 11:28:20.010256', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 11:28:20.010256', 'vutranorhilsun@gmail.com', 0, 'Xịn xò con bò', NULL, 'Plus', NULL, NULL, 1);

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `detail_description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `min_price` decimal(38, 2) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `rating_avg` double NULL DEFAULT NULL,
  `rating_count` int NULL DEFAULT NULL,
  `short_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `total_sold` int NULL DEFAULT NULL,
  `category_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKostq1ec3toafnjok09y9l7dox`(`slug` ASC) USING BTREE,
  INDEX `FKog2rp4qthbtt2lfyhfo32lsw9`(`category_id` ASC) USING BTREE,
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, '2026-02-26 11:27:31.725306', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:00:39.387079', 'vutranorhilsun@gmail.com', 5, b'1', 'Chatgpt plus đẳng cấp vãi l', 500000.00, 'ChatGPT Plus', 0, 0, 'Đẹp không mày', 'chatgpt-plus', 0, NULL);

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` bigint NOT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  INDEX `FKhfh9dx7w3ubf1co1vdev94g3f`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (1, 'ADMIN');
INSERT INTO `user_roles` VALUES (1, 'USER');
INSERT INTO `user_roles` VALUES (2, 'USER');
INSERT INTO `user_roles` VALUES (2, 'ADMIN');
INSERT INTO `user_roles` VALUES (3, 'USER');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email_verified` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `first_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `last_login_at` datetime(6) NULL DEFAULT NULL,
  `last_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `reset_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reset_token_expiry` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK6dotkott2kjsp8vw4d0m25fb7`(`email` ASC) USING BTREE,
  UNIQUE INDEX `UKr43af9ap4edm43mmtq01oddj6`(`username` ASC) USING BTREE,
  UNIQUE INDEX `UKkpeyao30ym7l5vf8wsterwase`(`reset_token` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '2026-02-26 09:50:27.880338', 'system', b'0', '2026-02-26 09:50:27.880338', 'system', 0, NULL, 'admin@accountshop.com', b'1', b'1', 'Admin', NULL, 'Shop', '$2a$12$xLN.oqtwDYkK5lWndzcgL.wVeoqgdn5QMBTum91Rm3/8ZOHfEGPMi', NULL, 'admin', NULL, NULL);
INSERT INTO `users` VALUES (2, '2026-02-26 09:51:10.294983', 'system', b'0', '2026-02-26 12:05:08.764233', 'vutranorhilsun@gmail.com', 5, NULL, 'vutranorhilsun@gmail.com', b'1', b'1', NULL, NULL, NULL, '$2a$12$kGOHhRnd1r0NA3lSnOxoc.1qKY550zvhjH.OyeFRKOw7sL/s72jt6', NULL, 'thanhvu', NULL, NULL);
INSERT INTO `users` VALUES (3, '2026-02-26 17:39:41.733202', 'system', b'0', '2026-02-26 22:18:03.238544', 'lethicuba1004@gmail.com', 5, NULL, 'lethicuba1004@gmail.com', b'1', b'1', 'n0lyminh', NULL, 'sv2', '$2a$12$eVff/cZUhaQ3IhcrVZVqhOz0IF.8oXfaMMhUKbZhLpIT6tsrcemJW', '412412412', 'thanhvu123', NULL, NULL);

-- ----------------------------
-- Table structure for variant_pricing
-- ----------------------------
DROP TABLE IF EXISTS `variant_pricing`;
CREATE TABLE `variant_pricing`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `version` bigint NULL DEFAULT NULL,
  `available_stock` int NOT NULL,
  `duration_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `duration_months` int NULL DEFAULT NULL,
  `original_price` decimal(12, 0) NULL DEFAULT NULL,
  `price` decimal(12, 0) NOT NULL,
  `warranty_policy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `variant_id` bigint NOT NULL,
  `account_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK3oqll4cmn28dy8jyy78f3h6ri`(`variant_id` ASC) USING BTREE,
  CONSTRAINT `FK3oqll4cmn28dy8jyy78f3h6ri` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of variant_pricing
-- ----------------------------
INSERT INTO `variant_pricing` VALUES (1, '2026-02-26 11:28:20.014364', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 12:05:34.298312', 'vutranorhilsun@gmail.com', 1, 502, '1 tháng', 1, 1000000, 2000, NULL, 1, NULL);
INSERT INTO `variant_pricing` VALUES (2, '2026-02-26 11:28:20.017393', 'vutranorhilsun@gmail.com', b'0', '2026-02-26 11:28:20.017393', 'vutranorhilsun@gmail.com', 0, 10000, '3 tháng', 3, 1000000, 5000, NULL, 1, NULL);

SET FOREIGN_KEY_CHECKS = 1;
