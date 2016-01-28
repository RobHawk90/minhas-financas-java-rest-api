-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema minhas_financas_testes
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `minhas_financas_testes` ;

-- -----------------------------------------------------
-- Schema minhas_financas_testes
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `minhas_financas_testes` DEFAULT CHARACTER SET utf8 ;
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
  `periodoId` INT NOT NULL,
  `categoriaId` INT NOT NULL,
  `contaId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_agendamentos_periodos1_idx` (`periodoId` ASC),
  INDEX `fk_agendamentos_categorias1_idx` (`categoriaId` ASC),
  INDEX `fk_agendamentos_contas1_idx` (`contaId` ASC),
  CONSTRAINT `fk_agendamentos_periodos1`
    FOREIGN KEY (`periodoId`)
    REFERENCES `minhas_financas_testes`.`periodos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_agendamentos_categorias1`
    FOREIGN KEY (`categoriaId`)
    REFERENCES `minhas_financas_testes`.`categorias` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_agendamentos_contas1`
    FOREIGN KEY (`contaId`)
    REFERENCES `minhas_financas_testes`.`contas` (`id`)
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
  `emParcelas` TINYINT(1) NULL DEFAULT 0,
  `contaId` INT NOT NULL,
  `categoriaId` INT NOT NULL,
  `agendamentoId` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_movimentacoes_contas_idx` (`contaId` ASC),
  INDEX `fk_movimentacoes_categorias1_idx` (`categoriaId` ASC),
  INDEX `fk_movimentacoes_agendamentos1_idx` (`agendamentoId` ASC),
  CONSTRAINT `fk_movimentacoes_contas`
    FOREIGN KEY (`contaId`)
    REFERENCES `minhas_financas_testes`.`contas` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movimentacoes_categorias1`
    FOREIGN KEY (`categoriaId`)
    REFERENCES `minhas_financas_testes`.`categorias` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movimentacoes_agendamentos1`
    FOREIGN KEY (`agendamentoId`)
    REFERENCES `minhas_financas_testes`.`agendamentos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `minhas_financas_testes`.`parcelas`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `minhas_financas_testes`.`parcelas` ;

CREATE TABLE IF NOT EXISTS `minhas_financas_testes`.`parcelas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `dataVencimento` DATE NOT NULL,
  `dataPagamento` DATE NULL,
  `valor` DOUBLE NOT NULL,
  `tipo` ENUM('MENSAL', 'ANUAL') NOT NULL DEFAULT 'MENSAL',
  `foiPaga` TINYINT(1) NULL DEFAULT 0,
  `movimentacaoId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_parcelas_movimentacoes1_idx` (`movimentacaoId` ASC),
  CONSTRAINT `fk_parcelas_movimentacoes1`
    FOREIGN KEY (`movimentacaoId`)
    REFERENCES `minhas_financas_testes`.`movimentacoes` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `minhas_financas_testes`;

DELIMITER $$

USE `minhas_financas_testes`$$
DROP TRIGGER IF EXISTS `minhas_financas_testes`.`insere_movimentacao_atualiza_saldo` $$
USE `minhas_financas_testes`$$
CREATE TRIGGER `insere_movimentacao_atualiza_saldo` AFTER INSERT ON `movimentacoes` FOR EACH ROW
BEGIN
	SET @emParcelas = NEW.emParcelas;

	IF NOT @emParcelas THEN
		SET @valor = NEW.valor;
		SET @idCategoria = NEW.categoriaId;
		SET @idConta = NEW.contaId;
		SET @tipo = (SELECT tipo FROM categorias WHERE id = @idCategoria);

		IF @tipo = 'RECEITA' THEN
			UPDATE contas SET saldo = (saldo + @valor) WHERE id = @idConta;
		ELSE
			UPDATE contas SET saldo = (saldo - @valor) WHERE id = @idConta;
		END IF;
	END IF;
END$$


USE `minhas_financas_testes`$$
DROP TRIGGER IF EXISTS `minhas_financas_testes`.`deleta_movimentacao_atualiza_saldo` $$
USE `minhas_financas_testes`$$
CREATE TRIGGER `deleta_movimentacao_atualiza_saldo` AFTER DELETE ON `movimentacoes` FOR EACH ROW
BEGIN
	SET @emParcelas = OLD.emParcelas;

	IF NOT @emParcelas THEN
		SET @valor = OLD.valor;
		SET @idCategoria = OLD.categoriaId;
		SET @idConta = OLD.contaId;
		SET @tipo = (SELECT tipo FROM categorias WHERE id = @idCategoria);

		IF @tipo = 'RECEITA' THEN
			UPDATE contas SET saldo = (saldo - @valor) WHERE id = @idConta;
		ELSE
			UPDATE contas SET saldo = (saldo + @valor) WHERE id = @idConta;
		END IF;
	END IF;
END$$


USE `minhas_financas_testes`$$
DROP TRIGGER IF EXISTS `minhas_financas_testes`.`movimentacoes_BEFORE_DELETE` $$
USE `minhas_financas_testes`$$
CREATE DEFINER = CURRENT_USER TRIGGER `minhas_financas_testes`.`movimentacoes_BEFORE_DELETE` BEFORE DELETE ON `movimentacoes` FOR EACH ROW
BEGIN
	SET @id = OLD.id;
    
    DELETE FROM parcelas WHERE movimentacaoId = @id;
END$$


USE `minhas_financas_testes`$$
DROP TRIGGER IF EXISTS `minhas_financas_testes`.`insere_parcela_atualiza_saldo` $$
USE `minhas_financas_testes`$$
CREATE TRIGGER `insere_parcela_atualiza_saldo` AFTER INSERT ON `parcelas` FOR EACH ROW
BEGIN
	SET @foiPaga = NEW.foiPaga;

	IF @foiPaga THEN
		SET @valor = NEW.valor;
		SET @idMovimentacao = NEW.movimentacaoId;
		SET @tipo = (SELECT tipo FROM categorias 
					 INNER JOIN movimentacoes ON categorias.id = movimentacoes.categoriaId
					 WHERE movimentacoes.id = @idMovimentacao);
		SET @idConta = (SELECT contas.id FROM contas 
						INNER JOIN movimentacoes ON movimentacoes.contaId = contas.id
						WHERE movimentacoes.id = @idMovimentacao);

		IF @tipo = 'RECEITA' THEN
			UPDATE contas SET saldo = (saldo + @valor) WHERE id = @idConta;
		ELSE
			UPDATE contas SET saldo = (saldo - @valor) WHERE id = @idConta;
		END IF;
	END IF;
END$$


USE `minhas_financas_testes`$$
DROP TRIGGER IF EXISTS `minhas_financas_testes`.`atualiza_parcela_atualiza_saldo` $$
USE `minhas_financas_testes`$$
CREATE TRIGGER `minhas_financas_testes`.`atualiza_parcela_atualiza_saldo` AFTER UPDATE ON `parcelas` FOR EACH ROW
BEGIN
	SET @foiPaga = NEW.foiPaga;

	IF @foiPaga THEN
		SET @valor = NEW.valor;
		SET @idMovimentacao = NEW.movimentacaoId;
		SET @tipo = (SELECT tipo FROM categorias 
					 INNER JOIN movimentacoes ON categorias.id = movimentacoes.categoriaId
					 WHERE movimentacoes.id = @idMovimentacao);
		SET @idConta = (SELECT contas.id FROM contas 
						INNER JOIN movimentacoes ON movimentacoes.contaId = contas.id
						WHERE movimentacoes.id = @idMovimentacao);

		IF @tipo = 'RECEITA' THEN
			UPDATE contas SET saldo = (saldo + @valor) WHERE id = @idConta;
		ELSE
			UPDATE contas SET saldo = (saldo - @valor) WHERE id = @idConta;
		END IF;
	END IF;
END$$


USE `minhas_financas_testes`$$
DROP TRIGGER IF EXISTS `minhas_financas_testes`.`deleta_parcela_atualiza_saldo` $$
USE `minhas_financas_testes`$$
CREATE DEFINER = CURRENT_USER TRIGGER `minhas_financas_testes`.`deleta_parcela_atualiza_saldo` BEFORE DELETE ON `parcelas` FOR EACH ROW
BEGIN
	SET @foiPaga = OLD.foiPaga;

	IF @foiPaga THEN
		SET @valor = OLD.valor;
		SET @idMovimentacao = OLD.movimentacaoId;
		SET @tipo = (SELECT tipo FROM categorias 
					 INNER JOIN movimentacoes ON categorias.id = movimentacoes.categoriaId
					 WHERE movimentacoes.id = @idMovimentacao);
		SET @idConta = (SELECT contas.id FROM contas 
						INNER JOIN movimentacoes ON movimentacoes.contaId = contas.id
						WHERE movimentacoes.id = @idMovimentacao);

		IF @tipo = 'RECEITA' THEN
			UPDATE contas SET saldo = (saldo - @valor) WHERE id = @idConta;
		ELSE
			UPDATE contas SET saldo = (saldo + @valor) WHERE id = @idConta;
		END IF;
	END IF;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
