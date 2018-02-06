use crypto;

drop table if exists WhitelistCoin;

create table if not exists WhitelistCoin (
	`Id` int auto_increment not null,
    `Name` varchar(100) not null,
    `CategoryName` varchar(100),
    `Meta` varchar(100),
    `Status` varchar(100),
    `Url` varchar(200),
    `Created` datetime,
    primary key (Id)
);