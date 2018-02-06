use crypto;

drop table if exists PresaleCoin;

create table if not exists PresaleCoin (
	`Id` int auto_increment not null,
    `Name` varchar(100) not null,
    `PresaleInterest` varchar(100),
    `PresaleDate` varchar(100),
    `Bonus` varchar(200),
    `MinRate` varchar(200),
    `Url` varchar(200),
    `Created` datetime,
    primary key (Id)
)