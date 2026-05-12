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
-- Table structure for table `versao`
--

DROP TABLE IF EXISTS `versao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `versao` (
  `id_dataset` bigint unsigned NOT NULL,
  `num_versao` int NOT NULL,
  `arquivo_csv` varchar(100) NOT NULL,
  `detalhes_feature` varchar(100) DEFAULT NULL,
  `data_registro` date NOT NULL,
  `hora_registro` time NOT NULL,
  `descricao_modificacoes` text,
  `username_autor` varchar(20) NOT NULL,
  `id_dataset_base` bigint unsigned DEFAULT NULL,
  `num_versao_base` int DEFAULT NULL,
  PRIMARY KEY (`id_dataset`,`num_versao`),
  KEY `username_autor` (`username_autor`),
  KEY `id_dataset_base` (`id_dataset_base`,`num_versao_base`),
  CONSTRAINT `versao_ibfk_1` FOREIGN KEY (`id_dataset`) REFERENCES `dataset` (`id_dataset`) ON DELETE CASCADE,
  CONSTRAINT `versao_ibfk_2` FOREIGN KEY (`username_autor`) REFERENCES `usuario` (`username`),
  CONSTRAINT `versao_ibfk_3` FOREIGN KEY (`id_dataset_base`, `num_versao_base`) REFERENCES `versao` (`id_dataset`, `num_versao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `versao`
--

LOCK TABLES `versao` WRITE;
/*!40000 ALTER TABLE `versao` DISABLE KEYS */;
INSERT INTO `versao` VALUES (1,1,'vendas_raw.csv','Colunas: id, valor, data','2026-05-01','10:00:00','Carga inicial do sistema','Fabricio_id',NULL,NULL),(1,2,'vendas_limpo.csv','Colunas: id, valor, data, status','2026-05-05','14:20:00','Remoção de valores nulos','David_id',1,1),(2,1,'iot_v1.csv','Temp, Humidade','2026-05-02','08:30:00','Dados brutos do sensor A1','David_id',NULL,NULL);
/*!40000 ALTER TABLE `versao` ENABLE KEYS */;
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
