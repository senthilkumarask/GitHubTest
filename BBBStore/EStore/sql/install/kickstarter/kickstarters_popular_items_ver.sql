SET DEFINE OFF;
 	 
CREATE TABLE BBB_POPULAR_ITEMS (
	id varchar2(40) NOT NULL ,
	registry_type varchar2(40) NULL ,
	search_query varchar2(500) NULL,
	asset_version           INTEGER NOT NULL,
	workspace_id            varchar2(254) NOT NULL,
	branch_id               varchar2(254) NOT NULL,
	is_head                 number(1)     NOT NULL,
	version_deleted         number(1) NOT NULL,
	version_editable        number(1) NOT NULL,
	pred_version            INTEGER NULL,
	checkin_date            TIMESTAMP NULL
 ); 
 
ALTER TABLE BBB_POPULAR_ITEMS  ADD CONSTRAINT PK_BBB_POPULAR_ITEMS PRIMARY KEY(id,asset_version) USING INDEX TABLESPACE INDX01;

CREATE TABLE BBB_POPULAR_ITEMS_REG_TYS (
    id varchar2(40) NOT NULL , 
    registry_type varchar2(40) NULL   ,
   asset_version           INTEGER NOT NULL
   )
    
CREATE INDEX BBB_POPULAR_ITEMS_REG_TYS_IDX ON BBB_POPULAR_ITEMS_REG_TYS(registry_type , id) ;

ALTER TABLE BBB_POPULAR_ITEMS_REG_TYS  ADD CONSTRAINT PK_BBB_POPULAR_ITEMS_REG_TYS PRIMARY KEY(id, registry_type , asset_version) USING INDEX TABLESPACE INDX01;

COMMIT;