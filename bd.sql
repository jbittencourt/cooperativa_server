# phpMyAdmin SQL Dump
# version 2.5.2-rc1
# http://www.phpmyadmin.net
#
# Host: localhost
# Generation Time: Dec 04, 2003 at 10:45 PM
# Server version: 4.1.0
# PHP Version: 4.3.0
# 
# Database : `cooperativa`
# 
CREATE DATABASE cooperativa;
USE cooperativa;
# --------------------------------------------------------

#
# Table structure for table `avatar`
#
# Creation: Aug 26, 2003 at 11:48 AM
# Last update: Aug 26, 2003 at 11:48 AM
#

CREATE TABLE `avatar` (
  `codAvatar` int(11) NOT NULL auto_increment,
  `nomeAvatar` varchar(60) NOT NULL default '',
  `flashFile` varchar(60) NOT NULL default '',
  PRIMARY KEY  (`codAvatar`)
) TYPE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

# --------------------------------------------------------

# --------------------------------------------------------

#
# Table structure for table `coopchat`
#
# Esta tabela guarda as falas do usuario
# 
# Creation: Dec 04, 2003 at 01:35 PM
# Last update: Dec 04, 2003 at 03:13 PM
#

CREATE TABLE `CoopChat` (
  `codUser` int(11) NOT NULL default '0',
  `codCenario` int(11) NOT NULL default '0',
  `tempo` bigint(13) NOT NULL default '0',
  `strMensagem` text NOT NULL,
  PRIMARY KEY  (`codUser`,`codCenario`,`tempo`)
) TYPE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

# --------------------------------------------------------

#
# Table structure for table `coopestadosobjeto`
#
# Esta tabela guarda os estados dos objetos
#
# Creation: Dec 04, 2003 at 02:29 PM
# Last update: Dec 04, 2003 at 10:32 PM
#

CREATE TABLE `CoopEstadosObjeto` (
  `codObjeto` int(11) NOT NULL default '0',
  `codCenario` int(11) NOT NULL default '0',
  `tempo` bigint(13) NOT NULL default '0',
  `status` int(11) NOT NULL default '0',
  `codUser` int(11) NOT NULL default '0',
  `tag` int(11) NOT NULL default '0',
  PRIMARY KEY  (`codObjeto`,`codCenario`,`tempo`)
) TYPE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

# --------------------------------------------------------

#
# Table structure for table `coopmovimentos`
#
# Esta tabela guarda os movimentos dos usuario 
#
# Creation: Dec 04, 2003 at 01:36 PM
# Last update: Dec 04, 2003 at 03:14 PM
#

CREATE TABLE `CoopMovimentos` (
  `codUser` int(11) NOT NULL default '0',
  `codCenario` int(11) NOT NULL default '0',
  `tempo` bigint(13) NOT NULL default '0',
  `posX` int(11) NOT NULL default '0',
  `posY` int(11) NOT NULL default '0',
  PRIMARY KEY  (`codUser`,`codCenario`,`tempo`)
) TYPE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

# --------------------------------------------------------

#
# Table structure for table `coopobjeto`
#
# Esta tabela guarda todos os objetos da cooperativa, bem como seu estado mais atual
#
# Creation: Dec 04, 2003 at 02:13 PM
# Last update: Dec 04, 2003 at 10:32 PM
#

CREATE TABLE `CoopObjeto` (
  `codObjeto` int(11) NOT NULL auto_increment,
  `nome` varchar(40) NOT NULL default '',
  `status` int(11) NOT NULL default '0',
  `tag` int(11) NOT NULL default '0',
  PRIMARY KEY  (`codObjeto`)
) TYPE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


# --------------------------------------------------------

#
# Estrutura da tabela `cenario`
#


CREATE TABLE `cenario` (
  `codCenario` int(11) NOT NULL auto_increment,
  `nomeCenario` varchar(60) NOT NULL default '',
  `maxUsers` int(11) default NULL,
  `xmlFile` varchar(60) NOT NULL default '',
  `xmlData` text,
  PRIMARY KEY  (`codCenario`)
) TYPE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;



# --------------------------------------------------------

#
# Estrutura da tabela `user`
#

CREATE TABLE `user` (
  `codUser` int(11) NOT NULL auto_increment,
  `nomUser` varchar(40) NOT NULL default '',
  `desSenha` varchar(60) NOT NULL default '',
  `desSenhaPlain` varchar(50) NOT NULL default '',
  `flaSuper` char(1) NOT NULL default '',
  `flaAprovado` char(1) NOT NULL default '',
  `flaAtivo` char(1) NOT NULL default '1',
  `nomPessoa` varchar(50) NOT NULL default '',
  `tempo` bigint(20) NOT NULL default '0',
  `strEMail` varchar(50) NOT NULL default '',
  `desEndereco` varchar(150) NOT NULL default '',
  `codCidade` tinyint(4) NOT NULL default '0',
  `desCEP` varchar(9) NOT NULL default '',
  `desTelefone` varchar(15) NOT NULL default '',
  `desFax` varchar(15) NOT NULL default '0',
  `desCargo` varchar(20) default NULL,
  `desHistorico` text,
  `desUrl` varchar(50) default NULL,
  `codEscola` tinyint(4) NOT NULL default '0',
  `datNascimento` bigint(20) NOT NULL default '0',
  `codHomeCenario` int(11) NOT NULL default '0',
  `codAvatar` int(11) default NULL,
  PRIMARY KEY  (`codUser`)
) TYPE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

#POPULATE INITIAL DATA

# Insert default scenario
INSERT INTO cenario SET nomeCenario="Praça", maxUsers="100", xmlFile="praca.xml" ;

INSERT INTO user SET nomUser="testuser", nomPessoa="Teste", desSenhaPlain="teste", desSenha="698dc19d489c4e4db73e28a713eab07b", codAvatar=1, codHomeCenario=1;