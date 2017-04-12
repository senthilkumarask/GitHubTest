 SET DEFINE OFF;
 CREATE TABLE BBB_PICKLIST_SKU_ITEM (
	id varchar2(40) NOT NULL,
	sku_id varchar2(40) NOT NULL,
	recommended_qty INTEGER NOT NULL,
	sku_comment  varchar2(2000) NULL, 
	asset_version           INTEGER NOT NULL,
	workspace_id            varchar2(254) NOT NULL,
	branch_id               varchar2(254) NOT NULL,
	is_head                 number(1)     NOT NULL,
	version_deleted         number(1) NOT NULL,
	version_editable        number(1) NOT NULL,
	pred_version            INTEGER NULL,
	checkin_date            TIMESTAMP NULL
 );	

ALTER TABLE BBB_PICKLIST_SKU_ITEM  ADD CONSTRAINT PK_BBB_PICKLIST_SKU_ITEM  PRIMARY KEY (id,asset_version) USING INDEX TABLESPACE INDX01;

CREATE TABLE BBB_KICK_STARTER (
 	id varchar2(40) NOT NULL,
 	heading1 varchar2(254) NOT NULL,
 	heading2 varchar2(254) NULL, 
 	description varchar2(2000) NULL, 
 	image_url varchar2(254) NULL,
 	hero_image_url varchar2(254) NULL,
 	priority INTEGER NULL, 
	is_active INTEGER NULL, 
 	kick_starter_type INTEGER NULL, 
 	asset_version           INTEGER NOT NULL,
	workspace_id            varchar2(254) NOT NULL,
	branch_id               varchar2(254) NOT NULL,
	is_head                 number(1)     NOT NULL,
	version_deleted         number(1) NOT NULL,
	version_editable        number(1) NOT NULL,
	pred_version            INTEGER NULL,
	checkin_date            TIMESTAMP NULL
  ); 
 
 ALTER TABLE BBB_KICK_STARTER ADD CONSTRAINT PK_BBB_KICK_STARTER PRIMARY KEY (id,asset_version) USING INDEX TABLESPACE INDX01;

CREATE TABLE BBB_KICK_START_REG_TYPE_BADGE ( 
 	id varchar2(40) NOT NULL, 
 	registry_type varchar2(254) NOT NULL , 
 	registry_image_url varchar2(254) NULL,
 	asset_version           INTEGER NOT NULL,
	workspace_id            varchar2(254) NOT NULL,
	branch_id               varchar2(254) NOT NULL,
	is_head                 number(1)     NOT NULL,
	version_deleted         number(1) NOT NULL,
	version_editable        number(1) NOT NULL,
	pred_version            INTEGER NULL,
	checkin_date            TIMESTAMP NULL);	

ALTER TABLE BBB_KICK_START_REG_TYPE_BADGE  ADD CONSTRAINT PK_BBB_KICK_START_REG_TYPE_BE   PRIMARY KEY (id) USING INDEX TABLESPACE INDX01;

 CREATE TABLE BBB_KICK_STARTER_PICKLIST ( 
	id varchar2(40) NOT NULL, 
	picklist_title varchar2(254) NULL,
	picklist_summary_description varchar2(1000) NULL,
	customer_type INTEGER NULL, 
	site_id varchar2(40) NULL , 
	asset_version           INTEGER NOT NULL,
	workspace_id            varchar2(254) NOT NULL,
	branch_id               varchar2(254) NOT NULL,
	is_head                 number(1)     NOT NULL,
	version_deleted         number(1) NOT NULL,
	version_editable        number(1) NOT NULL,
	pred_version            INTEGER NULL,
	checkin_date            TIMESTAMP NULL);

ALTER TABLE BBB_KICK_STARTER_PICKLIST  ADD CONSTRAINT PK_BBB_KICK_STARTER_PICKLIST   PRIMARY KEY (id,asset_version) USING INDEX TABLESPACE INDX01;

CREATE TABLE BBB_KICK_STARTER_PICKLISTS (
 	id varchar2(40) NOT NULL ,
 	kick_starter_picklist_id varchar2(40) NULL,
 	asset_version           INTEGER NOT NULL);


ALTER TABLE BBB_KICK_STARTER_PICKLISTS  ADD CONSTRAINT PK_BBB_KICK_STARTER_PICKLISTS   PRIMARY KEY (id, kick_starter_picklist_id,asset_version) USING INDEX TABLESPACE INDX01;


CREATE TABLE BBB_PICKLIST_REG_TYPES (
	id varchar2(40) NOT NULL ,
	registry_type_id varchar2(40) NULL , 
	asset_version           INTEGER NOT NULL ); 

ALTER TABLE BBB_PICKLIST_REG_TYPES  ADD CONSTRAINT  PK_BBB_PICKLIST_REG_TYPES   PRIMARY KEY (id, registry_type_id,asset_version) USING INDEX TABLESPACE INDX01;
	
CREATE TABLE BBB_PICKLIST_CHANNELS (
    id varchar2(40) NOT NULL , 
    channels_id varchar2(40) NULL ,
    asset_version           INTEGER NOT NULL );

ALTER TABLE BBB_PICKLIST_CHANNELS  ADD CONSTRAINT PK_BBB_PICKLIST_CHANNELS  PRIMARY KEY (id, channels_id,asset_version) USING INDEX TABLESPACE INDX01;	

CREATE TABLE BBB_PICKLIST_SKUS ( 
	id varchar2(40) NOT NULL ,
	sequence_num INTEGER NOT NULL, 
	sku_id varchar2(40) NOT NULL , 
	asset_version           INTEGER NOT NULL ); 


ALTER TABLE BBB_PICKLIST_SKUS  ADD CONSTRAINT PK_BBB_PICKLIST_SKUS  PRIMARY KEY (id, sequence_num,asset_version) USING INDEX TABLESPACE INDX01;	
COMMIT;