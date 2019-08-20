# social-master
整和微信 qq 三方登录
1，创建表
create table UserConnection (userId varchar(255) not null,
providerId varchar(255) not null,
providerUserId varchar(255),
rank int not null,
displayName varchar(255),
profileUrl varchar(512),
imageUrl varchar(512),
accessToken varchar(512) not null,
secret varchar(512),
refreshToken varchar(512),
expireTime bigint,
primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);

2，添加jar


2，配置说明
