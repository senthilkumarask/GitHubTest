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
	id 			varchar2(40)	NOT NULL REFERENCES site_configuration(id),
	placeholder 		varchar2(40)	NOT NULL,
	common_greeting 	varchar2(254)	NULL,
	PRIMARY KEY(id, placeholder)
);

