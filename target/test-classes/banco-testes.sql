SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema minhas_financas_testes
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `minhas_financas_testes` ;
CREATE SCHEMA IF NOT EXISTS `minhas_financas_testes` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `minhas_financas_testes` ;

-- -----------------------------------------------------
-- Table `minhas_financas_testes`.`contas`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `minhas_financas_testes`.`contas` ;

CREATE TABLE IF NOT EXISTS `minhas_financas_testes`.`contas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descricao` VARCHAR(100) NOT NULL,
  `saldo` DOUBLE NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `descricao_UNIQUE` (`descricao` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `minhas_financas_testes`.`categorias`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `minhas_financas_testes`.`categorias` ;

CREATE TABLE IF NOT EXISTS `minhas_financas_testes`.`categorias` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descricao` VARCHAR(100) NOT NULL,
  `tipo` ENUM('RECEITA', 'DESPESA') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `descricao_UNIQUE` (`descricao` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `minhas_financas_testes`.`periodos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `minhas_financas_testes`.`periodos` ;

CREATE TABLE IF NOT EXISTS `minhas_financas_testes`.`periodos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descricao` VARCHAR(100) NOT NULL,
  `unidadeTemporal` ENUM('DIAS', 'MESES', 'ANOS') NOT NULL,
  `quantidade` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `descricao_UNIQUE` (`descricao` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `minhas_financas_testes`.`agendamentos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `minhas_financas_testes`.`agendamentos` ;

CREATE TABLE IF NOT EXISTS `minhas_financas_testes`.`agendamentos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descricao` VARCHAR(100) NOT NULL,
  `dataAgendamento` DATE NOT NULL,
  `valor` DOUBLE NOT NULL,
  `periodosId` INT NOT NULL,
  `categoriasId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_agendamentos_periodos1_idx` (`periodosId` ASC),
  INDEX `fk_agendamentos_categorias1_idx` (`categoriasId` ASC),
  CONSTRAINT `fk_agendamentos_periodos1`
    FOREIGN KEY (`periodosId`)
    REFERENCES `minhas_financas_testes`.`periodos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_agendamentos_categorias1`
    FOREIGN KEY (`categoriasId`)
    REFERENCES `minhas_financas_testes`.`categorias` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `minhas_financas_testes`.`movimentacoes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `minhas_financas_testes`.`movimentacoes` ;

CREATE TABLE IF NOT EXISTS `minhas_financas_testes`.`movimentacoes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `dataMovimentacao` DATE NOT NULL,
  `valor` DOUBLE NOT NULL,
  `descricao` VARCHAR(100) NOT NULL,
  `contasId` INT NOT NULL,
  `categoriasId` INT NOT NULL,
  `agendamentosId` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_movimentacoes_contas_idx` (`contasId` ASC),
  INDEX `fk_movimentacoes_categorias1_idx` (`categoriasId` ASC),
  INDEX `fk_movimentacoes_agendamentos1_idx` (`agendamentosId` ASC),
  CONSTRAINT `fk_movimentacoes_contas`
    FOREIGN KEY (`contasId`)
    REFERENCES `minhas_financas_testes`.`contas` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movimentacoes_categorias1`
    FOREIGN KEY (`categoriasId`)
    REFERENCES `minhas_financas_testes`.`categorias` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movimentacoes_agendamentos1`
    FOREIGN KEY (`agendamentosId`)
    REFERENCES `minhas_financas_testes`.`agendamentos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
