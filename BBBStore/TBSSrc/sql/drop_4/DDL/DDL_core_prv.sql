SET ECHO ON;
SET DEFINE OFF;

CREATE TABLE BBB_CORE_PRV.TBS_GS_ORDER_INFO (
    GSORDER_ID    VARCHAR2(254)   NOT NULL,
    USER_TOKEN    VARCHAR2(254)   NULL,
    STORE_ID      INTEGER         NULL,
    TERMINAL_ID   INTEGER         NULL,
    RE_LAST_NM    VARCHAR2(254)   NULL,
    RE_FIRST_NM   VARCHAR2(254)   NULL,
    RE_EMAIL      VARCHAR2(254)   NULL,
    RE_PHONE      VARCHAR2(254)   NULL,
    SKUS          VARCHAR2(254)   NULL,
    QUANTITIES    VARCHAR2(254)   NULL,
    REGISTRY_ID   VARCHAR2(254)   NULL,
    PRIMARY KEY(GSORDER_ID)
);

alter table BBB_CORE_PRV.bbb_order add tbs_store_no varchar2(20) null;

CREATE TABLE BBB_CORE_PRV.TBS_STORE_IP_RANGE (
	STORE_ID	varchar2(254)	NOT NULL,
	IP_RANGE	varchar2(254) NULL,
	CREATE_DATE		DATE NULL,
	LAST_UPDATED_DATE		DATE NULL,
	constraint STORE_ID_f1 foreign key (STORE_ID) references BBB_CORE_PRV.BBB_STORE(STORE_ID)
);

ALTER TABLE BBB_CORE_PRV.TBS_ITEM_INFO_DETAILS MODIFY TBS_COST NUMBER(12,2) NULL;
ALTER TABLE BBB_CORE_PRV.TBS_ITEM_INFO_DETAILS MODIFY TBS_CONFIG_ID VARCHAR2(15) NULL;
ALTER TABLE BBB_CORE_PRV.TBS_ITEM_INFO_DETAILS MODIFY TBS_PRODUCT_DESC VARCHAR2(2000) NULL;


ALTER TABLE BBB_CORE_PRV.bbb_order DROP COLUMN tbs_approver_id;
ALTER TABLE BBB_CORE_PRV.bbb_order DROP COLUMN tbs_associate_id;

alter table BBB_CORE_PRV.bbb_order add (
	tbs_associate_id varchar2(100) null,
	tbs_approver_id varchar2(100) null
);