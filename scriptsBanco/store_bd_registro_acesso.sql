CREATE DATABASE  IF NOT EXISTS `store_bd` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `store_bd`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: store_bd
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `registro_acesso`
--

DROP TABLE IF EXISTS `registro_acesso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registro_acesso` (
  `id_acesso` bigint unsigned NOT NULL AUTO_INCREMENT,
  `data_acesso` date DEFAULT NULL,
  `hora_acesso` time DEFAULT NULL,
  `tipo_acao` varchar(50) DEFAULT NULL,
  `username_leitor` varchar(20) DEFAULT NULL,
  `id_dataset_acessada` bigint unsigned DEFAULT NULL,
  `num_versao_acessada` int DEFAULT NULL,
  PRIMARY KEY (`id_acesso`),
  UNIQUE KEY `id_acesso` (`id_acesso`),
  KEY `username_leitor` (`username_leitor`),
  KEY `id_dataset_acessada` (`id_dataset_acessada`,`num_versao_acessada`),
  CONSTRAINT `registro_acesso_ibfk_1` FOREIGN KEY (`username_leitor`) REFERENCES `usuario` (`username`),
  CONSTRAINT `registro_acesso_ibfk_2` FOREIGN KEY (`id_dataset_acessada`, `num_versao_acessada`) REFERENCES `versao` (`id_dataset`, `num_versao`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registro_acesso`
--

LOCK TABLES `registro_acesso` WRITE;
/*!40000 ALTER TABLE `registro_acesso` DISABLE KEYS */;
INSERT INTO `registro_acesso` VALUES (1,'2026-05-10','09:00:00','LEITURA','David_id',1,1),(2,'2026-05-11','11:30:00','DOWNLOAD','David_id',1,2),(3,'2026-05-11','15:45:00','LEITURA','Fabricio_id',2,1);
/*!40000 ALTER TABLE `registro_acesso` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-11 19:34:05
