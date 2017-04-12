CREATE TABLE bbb_subscribe_users (
	sub_user_id 		varchar2(254)	NOT NULL,
	email 			varchar2(254)	NULL,
	salutation 		varchar2(254)	NULL,
	first_name 		varchar2(254)	NULL,
	last_name 		varchar2(254)	NULL,
	phone 			varchar2(254)	NULL,
	address1 		varchar2(254)	NULL,
	address2 		varchar2(254)	NULL,
	city 			varchar2(254)	NULL,
	state 			varchar2(254)	NULL,
	zip 			varchar2(254)	NULL,
	opt_email 		number(1)	NULL,
	opt_direct_mail 	number(1)	NULL,
	opt_mobile 		number(1)	NULL,
	sub_datetime 		DATE	NULL,
	CHECK (opt_email IN (0, 1)),
	CHECK (opt_direct_mail IN (0, 1)),
	CHECK (opt_mobile IN (0, 1)),
	PRIMARY KEY(sub_user_id)
);

CREATE TABLE bbb_unsubscribe_direct_mail (
	mail_unsub_user_id 	varchar2(254)	NOT NULL,
	email 			varchar2(254)	NULL,
	first_name 		varchar2(254)	NULL,
	last_name 		varchar2(254)	NULL,
	phone 			varchar2(254)	NULL,
	address1 		varchar2(254)	NULL,
	address2 		varchar2(254)	NULL,
	city 			varchar2(254)	NULL,
	state 			varchar2(254)	NULL,
	zip 			varchar2(254)	NULL,
	mail_unsub_date 	DATE	NULL,
	PRIMARY KEY(mail_unsub_user_id)
);

CREATE TABLE bbb_unsubscribe_email (
	email_unsub_user_id 	varchar2(254)	NOT NULL,
	email 			varchar2(254)	NULL,
	frequency 		INTEGER	NULL,
	 email_unsub_date 	DATE	NULL,
	PRIMARY KEY(email_unsub_user_id)
);