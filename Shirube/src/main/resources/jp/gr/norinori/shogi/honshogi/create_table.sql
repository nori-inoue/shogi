CREATE DATABASE `shogi`

CREATE TABLE `game` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`winner` TINYINT(4) NULL DEFAULT NULL,
	`starttime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`endtime` TIMESTAMP NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;
CREATE TABLE `game_scene` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`game_id` BIGINT(20) NOT NULL,
	`scene_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `game_id` (`game_id`),
	INDEX `scene_id` (`scene_id`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `on_game_scene` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`game_id` BIGINT(20) NOT NULL,
	`hash` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	PRIMARY KEY (`id`),
	INDEX `game_id` (`game_id`),
	INDEX `scene_id` (`hash`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `scene` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`hash` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	`first_win_count` BIGINT(20) NOT NULL DEFAULT '0',
	`second_win_count` BIGINT(20) NOT NULL DEFAULT '0',
	`draw` INT(11) NOT NULL DEFAULT '0',
	`total` BIGINT(20) NOT NULL DEFAULT '0',
	`tumi` TINYINT(4) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `hash` (`hash`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `on_scene` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`hash` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	`first_win_count` BIGINT(20) NOT NULL DEFAULT '0',
	`second_win_count` BIGINT(20) NOT NULL DEFAULT '0',
	`draw` INT(11) NOT NULL DEFAULT '0',
	`total` BIGINT(20) NOT NULL DEFAULT '0',
	`tumi` TINYINT(4) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `hash` (`hash`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `ex_scene` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`hash` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	`first_win_rate` INT(11) NOT NULL DEFAULT '0',
	`second_win_rate` INT(11) NOT NULL DEFAULT '0',
	`total` BIGINT(20) NOT NULL DEFAULT '0',
	`tumi` TINYINT(4) NULL DEFAULT NULL,
	`reliability` FLOAT NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `hash` (`hash`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `scene_link` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`scene_id_from` BIGINT(20) NOT NULL,
	`action_hash` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	`scene_id_to` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `on_scene_link` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`scene_hash_from` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	`action_hash` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	`scene_hash_to` VARCHAR(46) NOT NULL DEFAULT '0' COLLATE 'utf8_unicode_ci',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `scene_hash` (`scene_hash_from`, `scene_hash_to`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

--- sample sql
delete from game where exists (select * from on_game_scene where game.id = on_game_scene.game_id);
truncate table on_game_scene;
truncate table on_scene;

truncate table game;
truncate table scene;
truncate table game_scene;

select toryo.game_id, (toryo.lst - toryo.bgn) % 2, scene.hash from scene
inner join game_scene on game_scene.scene_id = scene.id
inner join (select game_id,min(id) bgn, max(id) lst from game_scene group by game_id) as toryo on toryo.lst = game_scene.id

select scene.* from scene
inner join game_scene on game_scene.scene_id = scene.id
where game_scene.game_id = xx
order by game_scene.id

select on_scene.* from on_scene
inner join on_game_scene on on_game_scene.hash = on_scene.hash
where on_game_scene.game_id = xx
order by on_game_scene.id

select hash
, truncate((truncate((first_win_count/2) / total, 4) - 0.5) * 20000 ,0)
, truncate((truncate((second_win_count/2) / total, 4) - 0.5) * 20000 ,0)
, total, tumi
from scene where total > 10;


grant all privileges on shogi.* to root@"%" identified by '' with grant option ;