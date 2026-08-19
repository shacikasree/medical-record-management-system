-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: medical_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `patient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `patient_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `patient_age` int DEFAULT NULL,
  `patient_gender` enum('male','female','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `doctor_id` int NOT NULL,
  `doctor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `department_id` int DEFAULT NULL,
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `appointment_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `chief_complaint` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('scheduled','pending','completed','cancelled','rescheduled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'scheduled',
  `consultation_type` enum('checkup','followup','consultation','emergency') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'checkup',
  `is_emergency` tinyint(1) DEFAULT '0',
  `cancellation_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `cancelled_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cancelled_at` timestamp NULL DEFAULT NULL,
  `completed_at` timestamp NULL DEFAULT NULL,
  `consultation_fee` decimal(10,2) DEFAULT '0.00',
  `payment_status` enum('pending','paid','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `appointment_number` (`appointment_number`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_doctor` (`doctor_id`),
  KEY `idx_date` (`appointment_date`),
  KEY `idx_status` (`status`),
  KEY `idx_doctor_date` (`doctor_id`,`appointment_date`),
  KEY `idx_patient_date` (`patient_id`,`appointment_date`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,7,'shacika Sree','shacikasree760@gmil.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-01-16','19:04:00',NULL,'Pain\r\n',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-01-15 13:59:47',0.00,'pending','2026-01-15 13:34:55','2026-01-15 13:59:47'),(2,7,'shacika Sree','shacikasree760@gmil.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-01-16','19:07:00',NULL,'Pain',NULL,NULL,NULL,'cancelled','checkup',0,NULL,NULL,'2026-01-15 14:00:07',NULL,0.00,'pending','2026-01-15 13:37:24','2026-01-15 14:00:07'),(3,7,'shacika Sree','shacikasree760@gmil.com','7904993255',19,'female',5,'Dr. Emily Davis',NULL,'Pediatrics','2026-01-18','05:00:00',NULL,'Pain\r\n',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-01-16 16:36:13',0.00,'pending','2026-01-15 14:35:19','2026-01-16 16:36:13'),(4,7,'shacika Sree','shacikasree760@gmil.com','7904993255',19,'female',3,'Dr. Sarah Johnson',NULL,'Neurology','2026-01-17','15:00:00',NULL,'Pain\r\n',NULL,NULL,NULL,'cancelled','checkup',0,NULL,NULL,'2026-01-16 16:35:57',NULL,0.00,'pending','2026-01-16 09:18:36','2026-01-16 16:35:57'),(5,7,'shacika Sree','shacikasree760@gmil.com','7904993255',19,'female',3,'Dr. Sarah Johnson',NULL,'Neurology','2026-01-16','19:00:00',NULL,'pain',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-01-16 09:42:18',0.00,'pending','2026-01-16 09:30:27','2026-01-16 09:42:18'),(6,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-01-19','10:30:00',NULL,'Pain\r\n',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-01-20 17:29:49',0.00,'pending','2026-01-19 01:00:22','2026-01-20 17:29:49'),(7,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-01-19','10:00:00',NULL,'pain',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-01-19 04:20:01',0.00,'pending','2026-01-19 04:12:19','2026-01-19 04:20:01'),(8,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',4,'Dr. Michael Chen',NULL,'Orthopedics','2026-01-21','09:00:00',NULL,'Pain',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-01-24 14:16:32',0.00,'pending','2026-01-20 17:30:22','2026-01-24 14:16:32'),(9,11,'sathya','sathya12@gmail.com','9791831810',20,'male',6,'Dr. Robert Wilson',NULL,'General Medicine','2026-01-27','19:00:00',NULL,'Cold via running nose',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-02-01 07:30:50',0.00,'pending','2026-01-24 14:25:15','2026-02-01 07:30:50'),(10,2,'Dr. John Smith','john02@gmail.com','+1234567801',46,'male',2,'Dr. John Smith',NULL,'Cardiology','2026-01-26','14:30:00',NULL,'Medicine',NULL,NULL,NULL,'cancelled','checkup',0,NULL,NULL,'2026-02-03 12:55:53',NULL,0.00,'pending','2026-01-25 07:19:37','2026-02-03 12:55:53'),(11,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-01-26','15:00:00',NULL,'pain\r\n',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-02-01 07:28:00',0.00,'pending','2026-01-25 07:39:40','2026-02-01 07:28:00'),(12,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',5,'Dr. Emily Davis',NULL,'Pediatrics','2026-02-02','02:00:00',NULL,'Pain\r\n',NULL,NULL,NULL,'cancelled','checkup',0,NULL,NULL,'2026-02-05 13:07:11',NULL,0.00,'pending','2026-02-01 14:43:59','2026-02-05 13:07:11'),(13,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',3,'Dr. Sarah Johnson',NULL,'Neurology','2026-02-08','10:00:00',NULL,'pain',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-02-11 15:33:01',0.00,'pending','2026-02-07 15:49:56','2026-02-11 15:33:01'),(14,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',3,'Dr. Sarah Johnson',NULL,'Neurology','2026-02-07','22:33:00',NULL,'No symptoms provided',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-02-07 16:54:44',0.00,'pending','2026-02-07 16:03:10','2026-02-07 16:54:44'),(15,20,'Kiruthika','kiruthika@gmail.com','9342464951',19,'female',6,'Dr. Robert Wilson',NULL,'General Medicine','2026-02-08','12:00:00',NULL,'Fever',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-02-08 05:43:23',0.00,'pending','2026-02-08 05:35:56','2026-02-08 05:43:23'),(16,20,'Kiruthika','kiruthika@gmail.com','9342464951',19,'female',3,'Dr. Sarah Johnson',NULL,'Neurology','2026-02-09','04:00:00',NULL,'checkup',NULL,NULL,NULL,'cancelled','checkup',0,NULL,NULL,'2026-02-08 15:16:26',NULL,0.00,'pending','2026-02-08 14:03:45','2026-02-08 15:16:26'),(17,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-02-12','03:00:00',NULL,'checkup\r\n',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-02-14 08:04:59',0.00,'pending','2026-02-11 15:36:40','2026-02-14 08:04:59'),(18,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',3,'Dr. Sarah Johnson',NULL,'Neurology','2026-02-14','15:30:00',NULL,'Checkup\r\n',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-02-14 08:02:04',0.00,'pending','2026-02-14 08:01:49','2026-02-14 08:02:04'),(19,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-02-14','15:30:00',NULL,'Medicine',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-04-02 13:30:50',0.00,'pending','2026-02-14 08:05:43','2026-04-02 13:30:50'),(20,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',3,'Dr. Sarah Johnson',NULL,'Neurology','2026-02-17','16:04:00',NULL,'pain',NULL,NULL,NULL,'cancelled','checkup',0,NULL,NULL,'2026-04-02 13:52:40',NULL,0.00,'pending','2026-02-17 07:34:12','2026-04-02 13:52:40'),(21,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-02-26','20:00:00',NULL,'Check up',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-04-02 14:00:54',0.00,'pending','2026-02-26 13:44:16','2026-04-02 14:00:54'),(22,7,'shacika Sree','shacikasree760@gmail.com','7904993255',19,'female',2,'Dr. John Smith',NULL,'Cardiology','2026-04-02','20:26:00',NULL,'pain',NULL,NULL,NULL,'completed','checkup',0,NULL,NULL,NULL,'2026-04-02 13:59:39',0.00,'pending','2026-04-02 13:56:31','2026-04-02 13:59:39');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `head_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `head_id` (`head_id`),
  CONSTRAINT `departments_ibfk_1` FOREIGN KEY (`head_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (1,'Health','Health',2,'2026-02-04 04:51:49','2026-02-05 13:08:21'),(2,'Neurology','Brain and nervous system',2,'2026-02-04 04:51:49','2026-02-04 04:52:03'),(3,'Orthopedics','Bone and joint care',3,'2026-02-04 04:51:49','2026-02-04 04:52:03'),(4,'Pediatrics','Children\'s healthcare',4,'2026-02-04 04:51:49','2026-02-04 04:52:03'),(5,'General Medicine','General health and wellness',5,'2026-02-04 04:51:49','2026-02-04 04:52:03'),(6,'Cardiology','Heart and cardiovascular system care',2,'2026-02-05 13:10:36','2026-02-05 13:11:23');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_schedule`
--

DROP TABLE IF EXISTS `doctor_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_schedule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `doctor_id` int NOT NULL,
  `day_name` varchar(20) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_doctor_day` (`doctor_id`,`day_name`),
  CONSTRAINT `doctor_schedule_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_schedule`
--

LOCK TABLES `doctor_schedule` WRITE;
/*!40000 ALTER TABLE `doctor_schedule` DISABLE KEYS */;
INSERT INTO `doctor_schedule` VALUES (1,2,'Monday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(2,3,'Monday',1,'09:00:00','18:00:00','2026-02-07 13:52:55','2026-02-08 10:09:49'),(3,4,'Monday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(4,5,'Monday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(5,6,'Monday',1,'09:00:00','18:00:00','2026-02-07 13:52:55','2026-02-08 15:11:28'),(8,2,'Tuesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(9,3,'Tuesday',1,'09:00:00','19:00:00','2026-02-07 13:52:55','2026-02-08 10:09:49'),(10,4,'Tuesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(11,5,'Tuesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(12,6,'Tuesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(15,2,'Wednesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(16,3,'Wednesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(17,4,'Wednesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(18,5,'Wednesday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(19,6,'Wednesday',1,'09:00:00','19:00:00','2026-02-07 13:52:55','2026-02-08 15:11:28'),(22,2,'Thursday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(23,3,'Thursday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(24,4,'Thursday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(25,5,'Thursday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(26,6,'Thursday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(29,2,'Friday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(30,3,'Friday',1,'09:00:00','18:00:00','2026-02-07 13:52:55','2026-02-09 16:53:09'),(31,4,'Friday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(32,5,'Friday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(33,6,'Friday',1,'09:00:00','17:00:00','2026-02-07 13:52:55','2026-02-07 13:52:55'),(36,2,'Saturday',1,'09:00:00','13:00:00','2026-02-07 13:52:55','2026-02-14 08:06:49'),(37,3,'Saturday',1,'09:00:00','13:00:00','2026-02-07 13:52:55','2026-02-08 10:09:49'),(38,4,'Saturday',0,NULL,NULL,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(39,5,'Saturday',0,NULL,NULL,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(40,6,'Saturday',1,'09:00:00','13:00:00','2026-02-07 13:52:55','2026-02-08 15:11:28'),(43,2,'Sunday',1,'09:00:00','12:00:00','2026-02-07 13:52:55','2026-02-14 08:06:49'),(44,3,'Sunday',0,NULL,NULL,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(45,4,'Sunday',0,NULL,NULL,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(46,5,'Sunday',0,NULL,NULL,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(47,6,'Sunday',1,'09:00:00','12:00:00','2026-02-07 13:52:55','2026-02-08 15:11:28');
/*!40000 ALTER TABLE `doctor_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_settings`
--

DROP TABLE IF EXISTS `doctor_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_settings` (
  `doctor_id` int NOT NULL,
  `emergency_available` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`doctor_id`),
  CONSTRAINT `doctor_settings_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_settings`
--

LOCK TABLES `doctor_settings` WRITE;
/*!40000 ALTER TABLE `doctor_settings` DISABLE KEYS */;
INSERT INTO `doctor_settings` VALUES (2,1,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(3,1,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(4,1,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(5,1,'2026-02-07 13:52:55','2026-02-07 13:52:55'),(6,1,'2026-02-07 13:52:55','2026-02-07 13:52:55');
/*!40000 ALTER TABLE `doctor_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_unavailable_dates`
--

DROP TABLE IF EXISTS `doctor_unavailable_dates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_unavailable_dates` (
  `id` int NOT NULL AUTO_INCREMENT,
  `doctor_id` int NOT NULL,
  `unavailable_date` date NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_doctor_date` (`doctor_id`,`unavailable_date`),
  CONSTRAINT `doctor_unavailable_dates_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_unavailable_dates`
--

LOCK TABLES `doctor_unavailable_dates` WRITE;
/*!40000 ALTER TABLE `doctor_unavailable_dates` DISABLE KEYS */;
INSERT INTO `doctor_unavailable_dates` VALUES (1,2,'2026-02-08','Personal ','2026-02-07 13:59:13'),(4,6,'2026-02-11','Personal Leave','2026-02-08 15:12:14'),(5,3,'2026-02-12','Personal Leave','2026-02-10 12:57:30'),(6,2,'2026-02-14','Personal leave','2026-02-12 09:29:11');
/*!40000 ALTER TABLE `doctor_unavailable_dates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `holidays`
--

DROP TABLE IF EXISTS `holidays`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `holidays` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `date` date NOT NULL,
  `description` text,
  `is_active` enum('yes','no') DEFAULT 'yes',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_holiday_date` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `holidays`
--

LOCK TABLES `holidays` WRITE;
/*!40000 ALTER TABLE `holidays` DISABLE KEYS */;
INSERT INTO `holidays` VALUES (1,'New Year\'s Day','2026-01-01','New Year celebration','yes','2026-01-26 11:21:41','2026-01-26 11:21:41'),(2,'Republic Day','2026-01-26','Indian Republic Day','yes','2026-01-26 11:21:41','2026-01-26 11:21:41'),(3,'Holi','2026-03-08','Festival of Colors','yes','2026-01-26 11:21:41','2026-01-26 11:21:41'),(4,'Good Friday','2026-04-03','Christian holiday','yes','2026-01-26 11:21:41','2026-01-26 11:21:41'),(5,'Independence Day','2026-08-15','Indian Independence Day','yes','2026-01-26 11:21:41','2026-01-26 11:21:41'),(6,'Gandhi Jayanti','2026-10-02','Birth of Mahatma Gandhi','yes','2026-01-26 11:21:41','2026-01-26 11:21:41'),(7,'Diwali','2026-11-11','Festival of Lights','yes','2026-01-26 11:21:41','2026-01-26 11:21:41'),(8,'Christmas','2026-12-25','Christmas Day','yes','2026-01-26 11:21:41','2026-01-26 11:21:41');
/*!40000 ALTER TABLE `holidays` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hospital_settings`
--

DROP TABLE IF EXISTS `hospital_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hospital_settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hospital_name` varchar(200) NOT NULL,
  `address` text NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `opening_time` time NOT NULL DEFAULT '08:00:00',
  `closing_time` time NOT NULL DEFAULT '20:00:00',
  `appointment_duration` int NOT NULL DEFAULT '30',
  `logo` varchar(255) DEFAULT NULL,
  `website` varchar(200) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hospital_settings`
--

LOCK TABLES `hospital_settings` WRITE;
/*!40000 ALTER TABLE `hospital_settings` DISABLE KEYS */;
INSERT INTO `hospital_settings` VALUES (1,'City Hospital','123 Medical Street,trichy-102','+1234567890','hrs@hospital.com','09:00:00','12:00:00',15,NULL,NULL,'2026-01-26 11:21:41','2026-04-02 13:50:18');
/*!40000 ALTER TABLE `hospital_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescriptions`
--

DROP TABLE IF EXISTS `prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescriptions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int DEFAULT NULL,
  `patient_id` int NOT NULL,
  `patient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `doctor_id` int NOT NULL,
  `doctor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `prescription_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `medicine_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `medicine_type` enum('tablet','capsule','syrup','injection','ointment','drops','inhaler','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'tablet',
  `dosage` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `frequency` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `instructions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `side_effects` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `prescribed_date` date NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `followup_date` date DEFAULT NULL,
  `status` enum('active','completed','expired','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'active',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `prescription_number` (`prescription_number`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_doctor` (`doctor_id`),
  KEY `idx_date` (`prescribed_date`),
  KEY `idx_status` (`status`),
  KEY `idx_appointment` (`appointment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescriptions`
--

LOCK TABLES `prescriptions` WRITE;
/*!40000 ALTER TABLE `prescriptions` DISABLE KEYS */;
INSERT INTO `prescriptions` VALUES (1,NULL,7,'shacika Sree',3,'Dr. Sarah Johnson','RX-1768569325018','Paracetamol','tablet','500mg','Twice daily','4 days',NULL,'Eat well',NULL,NULL,'hhhh',NULL,'2026-01-16','2026-01-16','2026-01-20','2026-01-19','active',NULL,'2026-01-16 13:15:25','2026-01-16 13:15:25'),(2,NULL,7,'shacika Sree',3,'Dr. Sarah Johnson','RX-1768569325070','Paracetamol','tablet','500mg','Twice daily','4 days',NULL,'Eat well',NULL,NULL,'hhhh',NULL,'2026-01-16','2026-01-16','2026-01-20','2026-01-19','active',NULL,'2026-01-16 13:15:25','2026-01-16 13:15:25'),(3,NULL,7,'shacika Sree',3,'Dr. Sarah Johnson','RX-1768581094822','ffff','tablet','hhhh','Three times daily','6',NULL,'eat',NULL,NULL,'bbhh',NULL,'2026-01-16','2026-01-16',NULL,'2026-01-18','active',NULL,'2026-01-16 16:31:34','2026-01-16 16:31:34'),(4,NULL,7,'shacika Sree',2,'Dr. John Smith','RX-1768784737038','Aspirin','tablet','100mg','Once daily','4 days',NULL,'Take rest',NULL,NULL,'Hypertension',NULL,'2026-01-19','2026-01-19','2026-01-23','2026-01-23','active',NULL,'2026-01-19 01:05:37','2026-01-19 01:05:37'),(5,NULL,7,'shacika Sree',2,'Dr. John Smith','RX-1768784737130','Aspirin','tablet','100mg','Once daily','4 days',NULL,'Take rest',NULL,NULL,'Hypertension',NULL,'2026-01-19','2026-01-19','2026-01-23','2026-01-23','active',NULL,'2026-01-19 01:05:37','2026-01-19 01:05:37'),(6,NULL,7,'shacika Sree',2,'Dr. John Smith','RX-1768796074800','Paracetamol','tablet','50mg','Once daily','3 days',NULL,'Take Rest',NULL,NULL,'Hypertnsion',NULL,'2026-01-19','2026-01-19','2026-01-22','2026-01-22','active',NULL,'2026-01-19 04:14:34','2026-01-19 04:14:34'),(7,NULL,7,'shacika Sree',4,'Dr. Michael Chen','RX-1768930752207','para','tablet','100mg','Once daily','6 days',NULL,'',NULL,NULL,'tension',NULL,'2026-01-20','2026-01-20','2026-01-26','2026-01-23','active',NULL,'2026-01-20 17:39:12','2026-01-20 17:39:12'),(8,NULL,7,'shacika Sree',3,'Dr. Sarah Johnson','RX-1770484863365','Not specified','tablet','','','',NULL,'rrrr',NULL,NULL,'vvv',NULL,'2026-02-07','2026-02-07',NULL,'2026-02-09','active',NULL,'2026-02-07 17:21:03','2026-02-07 17:21:03'),(9,NULL,7,'shacika Sree',3,'Dr. Sarah Johnson','RX-1770485444832','eeee','tablet','500','Once daily','4',NULL,'rrttyy',NULL,NULL,'kkkkf',NULL,'2026-02-07','2026-02-07',NULL,'2026-02-11','active',NULL,'2026-02-07 17:30:44','2026-02-07 17:30:44'),(10,NULL,20,'Kiruthika',6,'Dr. Robert Wilson','RX-1770529349185','Paracetamol','tablet','500mg','Once daily','3 days',NULL,'Take Rest',NULL,NULL,'Normal Fever',NULL,'2026-02-08','2026-02-08','2026-02-11','2026-02-11','active',NULL,'2026-02-08 05:42:29','2026-02-08 05:42:29'),(11,NULL,20,'Kiruthika',6,'Dr. Robert Wilson','RX-1770563443484','Not specified','tablet','','','',NULL,'Tke Rest',NULL,NULL,'fever',NULL,'2026-02-08','2026-02-08',NULL,'2026-02-10','active',NULL,'2026-02-08 15:10:43','2026-02-08 15:10:43'),(12,NULL,7,'shacika Sree',2,'Dr. John Smith','RX-1770572385686','Not specified','tablet','','','',NULL,'hhh',NULL,NULL,'ggg',NULL,'2026-02-08','2026-02-08',NULL,'2026-02-10','active',NULL,'2026-02-08 17:39:45','2026-02-08 17:39:45'),(13,NULL,7,'shacika Sree',3,'Dr. Sarah Johnson','RX-1770656027686','Not specified','tablet','','','',NULL,'ggg',NULL,NULL,'gg',NULL,'2026-02-09','2026-02-09',NULL,'2026-02-12','active',NULL,'2026-02-09 16:53:47','2026-02-09 16:53:47'),(14,NULL,7,'shacika Sree',2,'Dr. John Smith','RX-1770824314340','Not specified','tablet','','','',NULL,'Take Rest',NULL,NULL,'Pain',NULL,'2026-02-12','2026-02-12',NULL,NULL,'active',NULL,'2026-02-11 15:38:34','2026-02-11 15:38:34'),(15,NULL,7,'shacika Sree',2,'Dr. John Smith','RX-1775138344983','xxxx','tablet','xxxx','Once daily','3 days',NULL,'xxxxx',NULL,NULL,'fever',NULL,'2026-04-02','2026-04-02','2026-04-05','2026-04-04','active',NULL,'2026-04-02 13:59:05','2026-04-02 13:59:05');
/*!40000 ALTER TABLE `prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fullname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` enum('male','female','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `blood_group` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emergency_contact` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emergency_contact_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `specialty` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qualification` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `experience` int DEFAULT '0',
  `license_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('admin','doctor','patient','staff') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('active','inactive','blocked') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'active',
  `profile_photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role_status` (`role`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin01@gmail.com','$2a$12$sveRwsO/WZAil9XXtTNErulz4UonlW7YjE20PJEhICJnsGnnSs5ma','System Administrator','+1234567890',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,'admin','active',NULL,NULL,'2026-01-13 13:05:49','2026-08-19 15:04:35','2026-08-19 15:04:35'),(2,'john02@gmail.com','$2a$12$JMG2Rh5W1s5BFbd03gwRbuoimr3nYPHUWtAz7aYQcSyM83eMTNbNK','Dr. John Smith','1234567801','1978-03-15',46,'male','123 Medical Plaza, New York, NY 10001',NULL,NULL,NULL,'Cardiology','MD, FACC',12,'MED-12345','Cardiology','doctor','active',NULL,NULL,'2026-01-13 13:09:04','2026-04-02 15:00:00','2026-04-02 15:00:00'),(3,'sarah03@gmail.com','$2a$12$8O8fC7ypEIYnVurE61X78utRd09L8KwUNh3HDc7xms0ZqaSPfu6Cq','Dr. Sarah Johnson','+1234567802','1982-07-22',42,'female','456 Health Center, New York, NY 10002',NULL,NULL,NULL,'Neurology','MD, PhD',12,'MED-12346','Neurology','doctor','active',NULL,NULL,'2026-01-13 13:09:04','2026-08-19 15:05:31','2026-08-19 15:05:31'),(4,'michael@hospital.com','$2a$12$AXAJy.H9XwZDYK2dFTocMONRtJG1yUjxljhoyAcka6LNpL1yXJavq','Dr. Michael Chen','+1234567803','1975-11-08',48,'male','789 Medical Ave, New York, NY 10003',NULL,NULL,NULL,'Orthopedics','MD, MS',18,'MED-12347','Orthopedics','doctor','active',NULL,NULL,'2026-01-13 13:09:04','2026-02-01 14:15:25','2026-02-01 14:15:25'),(5,'emily@hospital.com','$2a$12$MPEswrCG8ROZ60FmXvaYg.R0zPoiti87NljflvCisi6wie8ii.jgW','Dr. Emily Davis','+1234567804','1985-04-19',39,'female','321 Care Street, New York, NY 10004',NULL,NULL,NULL,'Pediatrics','MD, FAAP',10,'MED-12348','Pediatrics','doctor','active',NULL,NULL,'2026-01-13 13:09:04','2026-02-01 14:13:52','2026-02-01 14:13:52'),(6,'robert@hospital.com','$2a$12$HiXbPSRhwalIL8AdmTjFXuw.xon4bEZlma/kkaTygseugk2kE/8L6','Dr. Robert Wilson','1234567805','1980-09-30',44,'male','654 Hospital Rd, New York, NY 10005',NULL,NULL,NULL,'General Medicine','MBBS, MD',12,'MED-12349','General Medicine','doctor','active',NULL,NULL,'2026-01-13 13:09:04','2026-02-16 16:24:01','2026-02-08 15:56:32'),(7,'shacikasree760@gmail.com','$2a$12$DGmUYPllfRXEY6cp.eInX.kRSl7w4f8yGf51rsxeWCRXAXCZBa/jC','shacika Sree','7904993255','2006-05-21',19,'female','7th cross Vasan Nagar Rettaivaikkal','B+','9384929347','rama',NULL,NULL,NULL,NULL,NULL,'patient','active',NULL,NULL,'2026-01-13 14:11:21','2026-08-19 15:05:54','2026-08-19 15:05:54'),(10,'rama28@gmail.com','$2a$12$lni1il7l36GP6O4.k5DeGu9Jz9WYhA.7NkQhh0nTeW.czl/KmZ8..','Rama','9384929347','1982-11-28',43,'female','rettaivaikkal','B+','7904993255','shacika',NULL,NULL,NULL,NULL,NULL,'patient','active',NULL,NULL,'2026-01-19 04:25:32','2026-01-19 04:27:01','2026-01-19 04:27:01'),(11,'sathya12@gmail.com','$2a$12$162AiP4rue.sMpmig6dPmOI8FKrmxOiwj2GzlCRNiNMlJuLUcRYgC','sathya','9791831810','2005-06-12',21,'male','vasan nagar','O+','7904993255','shacika',NULL,NULL,NULL,NULL,NULL,'patient','active',NULL,NULL,'2026-01-20 14:27:12','2026-02-04 11:45:01','2026-02-04 11:45:01'),(19,'logeshwaran2005@gmail.com','$2a$12$JLp6P7hRTMiMGsZ1JVPwle0zQqt3hlaqfDKpvhuis/4VWb4IYVxaa','Logeshwran','7339435652','2005-11-15',20,'male','Ramalinga Nagar','B+',NULL,NULL,NULL,NULL,0,NULL,NULL,'patient','active',NULL,NULL,'2026-02-04 13:09:29','2026-02-08 15:50:59','2026-02-08 15:50:25'),(20,'kiruthika@gmail.com','$2a$12$3vQm3jtY1Kvw23Snk8nPyeCWGWRo9DIqyanmTaOwTy9LCXuE9LhW.','Kiruthika','9342464951','2007-01-02',19,'female','Woraiyur Trichy-02','A+','7904993255','shacika',NULL,NULL,NULL,NULL,NULL,'patient','active',NULL,NULL,'2026-02-08 05:33:08','2026-02-08 15:49:16','2026-02-08 15:47:59'),(21,'shacikasree@gmail.com','$2a$12$jEXmj7MX.vaxUv3VcCmUE.1AirMGWYf1peBg0CqFjWAbfVTQTMJ3i','Shacika','7904993255','2006-05-21',19,'female','Rettaivaikkal','B+','9384929347','Rama',NULL,NULL,NULL,NULL,NULL,'patient','active',NULL,NULL,'2026-02-14 13:18:31','2026-02-16 16:25:19','2026-02-16 12:18:50');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-19 21:27:09
