-- drop table BBB_SITE_COMMON_GREETINGS;


ALTER TABLE
   BBB_SITE_ATTRIBUTES
ADD (
	giftWrapSku_flag 	number(1)	NULL,
	giftWrapPrice 		number(28, 20)	NULL,
	CHECK (pack_hold IN (0, 1)),
	CHECK (giftWrapSku_flag IN (0, 1))
);


CREATE TABLE BBB_SITE_COMMON_GREETINGS (
	asset_version INTEGER NOT NULL,
	id 			varchar2(40)	NOT NULL,
	placeholder 		varchar2(40)	NOT NULL,
	common_greeting 	varchar2(254)	NULL,
	PRIMARY KEY(id, placeholder,asset_version)
);

