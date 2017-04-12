SET ECHO ON;
SET DEFINE OFF;

CREATE TABLE BBB_SWITCH_A.TBS_HOLIDAY_MESSAGING (
 holiday_message_id  varchar2(254) NOT NULL,
 start_time   TIMESTAMP NULL,
 end_time   TIMESTAMP NULL,
 item_availability  varchar2(254) NOT NULL,
 standard_label   varchar2(254) NULL REFERENCES BBB_SWITCH_A.BBB_LBL_TXT(LBL_TXT_ID),
 expedited_label  varchar2(254) NULL REFERENCES BBB_SWITCH_A.BBB_LBL_TXT(LBL_TXT_ID),
 name    varchar2(254) NULL,
 PRIMARY KEY(holiday_message_id)
);