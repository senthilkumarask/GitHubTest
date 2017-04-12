CREATE TABLE BBB_DPS_CREDIT_CARD (
	id 			varchar2(254)	NOT NULL REFERENCES dps_credit_card(id),
	credit_card_number 	varchar2(254)	NULL,
	name_on_card 		varchar2(254)	NULL,
	PRIMARY KEY(id)
);

