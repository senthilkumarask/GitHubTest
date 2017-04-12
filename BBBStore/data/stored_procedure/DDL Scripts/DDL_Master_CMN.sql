SET DEFINE OFF;
CREATE TABLE BBB_CMN.BBB_UTL (
	ENC_ID  VARCHAR2(100)  NOT NULL,
	VERSION_NO  INT NOT NULL,
	IS_HEAD  VARCHAR2(40),
	CONSTRAINT PK_BBB_UTL PRIMARY KEY (ENC_ID,VERSION_NO) using index tablespace indx01);
COMMIT;