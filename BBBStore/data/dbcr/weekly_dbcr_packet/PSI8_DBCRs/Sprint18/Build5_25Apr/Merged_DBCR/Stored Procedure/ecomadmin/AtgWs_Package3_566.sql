SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;
SET DEFINE OFF;
CREATE OR REPLACE PACKAGE ECOMADMIN.ATGWS
AS
TYPE T_CURSOR IS REF CURSOR;
---------------------------------------------
-- GEMINI 3.566
-- REGISTRY
---------------------------------------------
PROCEDURE GET_REG_LIST_BY_NAME( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_LIST_BY_NAME2( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR, p_vFilterOptions IN VARCHAR2, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_LIST_BY_EMAIL( p_sEmailAddr IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR,  p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_LIST_BY_EMAIL2( p_sEmailAddr IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR, p_vFilterOptions IN VARCHAR2, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_LIST_BY_REG_NUM( p_vREG_NUM IN NUMBER, p_vMODE_GIFT_GIVERS IN VARCHAR, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE REGSEARCH_BY_REG_USING_NAME( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE REGSEARCH_BY_REG_USING_NAME2( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_vFilterOptions IN VARCHAR2, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE REGSEARCH_BY_REG_USING_EMAIL( p_sEmailAddr IN VARCHAR, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE REGSEARCH_BY_REG_USING_EMAIL2( p_sEmailAddr IN VARCHAR, p_vFilterOptions IN VARCHAR2, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE REGSEARCH_BY_REG_USING_REGNUM( p_vREG_NUM IN NUMBER, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_NAMES(  p_vRegNamesArray IN VARCHAR, p_vSubTypesArray IN VARCHAR, p_Cnt IN NUMBER, p_vOtherNamesArray OUT VARCHAR, p_vOtherStatesArray OUT VARCHAR );
PROCEDURE ADD_REG_ITEM( p_registry_num IN NUMBER, p_sku IN NUMBER, p_add_quantity IN NUMBER, p_last_maint_dt_tm IN NUMBER, p_createProg IN VARCHAR,
                    p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2);
PROCEDURE ADD_REG_ITEM2( p_registry_num IN NUMBER, p_sku IN NUMBER, p_add_quantity IN NUMBER, p_reference_id IN varchar2, p_itemType VARCHAR2, p_assembly_selected IN CHAR,p_assembly_price IN NUMBER, p_ltl_delivery_service IN VARCHAR2,
                    p_ltl_delivery_price IN NUMBER, p_personlization_code IN VARCHAR2, p_personalization_price IN NUMBER, p_customization_price IN NUMBER, p_personalization_descrip IN VARCHAR2,
                    p_image_url IN VARCHAR2, p_image_url_thumb IN VARCHAR2, p_mob_image_url IN VARCHAR2, p_mob_image_url_thumb IN VARCHAR2,
                    p_last_maint_dt_tm IN NUMBER, p_createProg IN VARCHAR, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_siteFlag IN VARCHAR2);
PROCEDURE PUT_REG_ITEM( p_RegNum IN NUMBER, p_rowID IN VARCHAR, p_ReqQty IN NUMBER, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2);
PROCEDURE PUT_REG_ITEM2( p_RegNum IN NUMBER, p_rowID IN VARCHAR, p_ReqQty IN NUMBER, p_itemType VARCHAR2, p_assembly_selected IN CHAR,p_assembly_price IN NUMBER, p_ltl_delivery_service IN VARCHAR2, p_ltl_delivery_price IN NUMBER, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_siteFlag IN VARCHAR2);
PROCEDURE GET_REG_ITEM_LIST( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_ITEM_LIST2( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_ITEM_LIST_BY_PRICE( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR );
PROCEDURE GET_REG_ITEM_LIST_BY_PRICE2( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR );
PROCEDURE INSERT_REG_HEADER( p_EventType IN VARCHAR, p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER, p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2,
            p_RegNum OUT NUMBER, p_JDADateNow OUT NUMBER );
PROCEDURE INSERT_REG_HEADER2( p_EventType IN VARCHAR, p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER, p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER, p_IsPublic IN VARCHAR, p_SiteFlag IN VARCHAR,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2,
            p_RegNum OUT NUMBER, p_JDADateNow OUT NUMBER );
PROCEDURE INSERT_REG_NAMES( p_RegNum IN NUMBER, p_AddrSubType IN VARCHAR, p_LastName IN VARCHAR, p_FirstName IN VARCHAR,
            p_Company IN VARCHAR, p_Addr1 IN VARCHAR, p_Addr2 IN VARCHAR, p_City IN VARCHAR, p_StateCD IN VARCHAR, p_Zip IN VARCHAR,
            p_DayPhone IN VARCHAR, p_EvePhone IN VARCHAR, p_Email IN VARCHAR, p_AsOfDate IN NUMBER,
            p_JDADateNow NUMBER, p_creationProg IN VARCHAR,
            p_DayPhoneExt IN VARCHAR, p_EvePhoneExt IN VARCHAR,
            p_PrefContMeth IN VARCHAR, p_PrefContTime IN VARCHAR,
            p_EmailFlag IN VARCHAR,
            p_Maiden IN VARCHAR,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2,
            p_ProfileID IN VARCHAR2, p_AffiliateOptIn IN VARCHAR2, p_Country IN VARCHAR2, p_RowXngUser IN VARCHAR2);
PROCEDURE INSERT_REG_BABY( p_RegNum IN NUMBER, p_FirstName IN VARCHAR2, p_Gender IN VARCHAR2, p_Decor IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2);
PROCEDURE INSERT_REG_PREF_STORE( p_RegNum IN NUMBER, p_StoreNum IN NUMBER, p_ContactFlag IN VARCHAR2);
PROCEDURE UPDATE_REG_PREF_STORE( p_RegNum IN NUMBER, p_StoreNum IN NUMBER, p_ContactFlag IN VARCHAR2);
PROCEDURE UPDATE_REG_PREF_STORE2( p_RegNum IN NUMBER, p_StoreNum IN NUMBER, p_ContactFlag IN VARCHAR2);
PROCEDURE UPDATE_REG_HEADER( p_RegNum IN NUMBER,
            p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER,
            p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2,
            p_JDADateNow OUT NUMBER );
PROCEDURE UPDATE_REG_HEADER2( p_RegNum IN NUMBER,
            p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER,
            p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER, p_IsPublic IN VARCHAR, p_SiteFlag IN VARCHAR,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2,
            p_JDADateNow OUT NUMBER );
PROCEDURE UPDATE_REG_NAMES( p_RegNum IN NUMBER, p_AddrSubType IN VARCHAR,
                        p_LastName IN VARCHAR, p_FirstName IN VARCHAR,
                        p_Company IN VARCHAR,
                        p_Addr1 IN VARCHAR, p_Addr2 IN VARCHAR, p_City IN VARCHAR, p_StateCD IN VARCHAR, p_Zip IN VARCHAR,
                        p_DayPhone IN VARCHAR, p_EvePhone IN VARCHAR, p_Email IN VARCHAR,
                        p_AsOfDate IN NUMBER, p_JDADateNow NUMBER, p_creationProg IN VARCHAR,
                        p_DayPhoneExt IN VARCHAR, p_EvePhoneExt IN VARCHAR,
                        p_PrefContMeth IN VARCHAR, p_PrefContTime IN VARCHAR,
                        p_EmailFlag IN VARCHAR,
                        p_Maiden IN VARCHAR,
                        p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2,
                        p_coRegProfileId IN VARCHAR, p_AffiliateOptIn IN VARCHAR2, p_Country IN VARCHAR2, p_RowXngUser IN VARCHAR2);
PROCEDURE UPDATE_REG_BABY( p_RegNum IN NUMBER, p_FirstName IN VARCHAR2, p_Gender IN VARCHAR2, p_Decor IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2);
PROCEDURE GET_REG_INFO( p_RegNum IN NUMBER, p_nGiftsRegistered OUT NUMBER, p_nGiftsPurchased OUT NUMBER, cur_Info OUT T_CURSOR, cur_Header OUT T_CURSOR, cur_Baby OUT T_CURSOR, cur_PrefStore OUT T_CURSOR);
--###### THIS IS ESSENTIALY A DUPLICATE OF GET_REG_INFO, DUPLICATE CREATED TO MAKE DEPLOYMENT OF FULL SOLUTION EASIER.
PROCEDURE GET_REG_INFO2( p_RegNum IN NUMBER, p_siteFlag IN VARCHAR, p_nGiftsRegistered OUT NUMBER, p_nGiftsPurchased OUT NUMBER, cur_Info OUT T_CURSOR, cur_Header OUT T_CURSOR, cur_Baby OUT T_CURSOR, cur_PrefStore OUT T_CURSOR);
PROCEDURE GET_REG_INFO_BY_PROFILEID( p_siteFlag IN VARCHAR, p_ProfileId IN VARCHAR2, cur_Info OUT T_CURSOR);
PROCEDURE GET_REG_STATUSES_BY_PROFILEID( p_siteFlag IN VARCHAR, p_ProfileId IN VARCHAR2, cur_Info OUT T_CURSOR);
PROCEDURE IMPORT_REGISTRY( p_RegNum IN NUMBER, p_vPass IN VARCHAR2, p_email IN VARCHAR2, p_firstNm IN VARCHAR2, p_lastNm IN VARCHAR2, p_phone1 IN VARCHAR2, p_phone2 IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_sProfileID IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_sRecordUpdated OUT VARCHAR2, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 );
PROCEDURE LINK_REGISTRY_TO_ATG_PROFILE( p_RegNum IN NUMBER, p_registrantType IN VARCHAR2,p_profileID IN VARCHAR2,p_Email IN VARCHAR2, p_RowXngUser in VARCHAR2, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 );
PROCEDURE UPD_COREG_PROFILE_BY_REGNUM( p_RegNum IN NUMBER, p_sCoRegProfileID IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 );
PROCEDURE UPD_COREG_PROFILE_BY_EMAIL( p_Email IN VARCHAR2, p_sCoRegProfileID IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2, cur_Info OUT T_CURSOR, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 );
PROCEDURE SET_ANNC_CARD_COUNT( p_RegNum IN NUMBER, p_nCount IN NUMBER, p_RowXngUser IN VARCHAR2 );
PROCEDURE GET_REGISTRY_PWD( p_RegNum IN NUMBER, p_password OUT VARCHAR2, p_email OUT VARCHAR2 );
PROCEDURE UPD_REG_NAMES_BY_PROFILE_ID(
                        p_ProfileId IN VARCHAR,
                        p_LastName IN VARCHAR, p_FirstName IN VARCHAR,
                        p_Phone1 IN VARCHAR, p_Phone2 IN VARCHAR,
                        p_Phone1Ext IN VARCHAR, p_Phone2Ext IN VARCHAR,
                        p_Email IN VARCHAR,
                        p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2);
PROCEDURE VALIDATE_REGISTRY_PASS_EMAIL( p_Email IN VARCHAR2, p_Password IN VARCHAR2, p_isValidAccount OUT VARCHAR2 );
PROCEDURE DISABLE_FUTURE_SHIPPING(p_RegistryNum IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2);
PROCEDURE COPY_REGISTRY( p_siteFlag in VARCHAR, p_src_reg_num IN NUMBER, p_target_reg_num IN NUMBER, p_createProg IN VARCHAR, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_NoOfItemsAdded OUT NUMBER);
---------------------------------------------
-- COUPONS
---------------------------------------------
PROCEDURE GET_REGULAR_COUPONS( p_SiteFlag IN VARCHAR, p_EmailAddr IN VARCHAR, cur_Info OUT T_CURSOR );
PROCEDURE IS_VALID_ENTRY_CD( p_SiteFlag IN VARCHAR, p_EntryCd IN VARCHAR,  p_sIsValid OUT VARCHAR2 );
PROCEDURE VERIFY_EMAIL_COUPON( p_SiteFlag IN VARCHAR2, p_sEmailAddr IN VARCHAR2, p_sCampaign IN VARCHAR2, p_sCouponCode IN VARCHAR2, p_nRetCode OUT NUMBER, p_sRetCodeDesc OUT VARCHAR2 );
FUNCTION CHK_ONL_CPN_RECIPIENTS( p_SiteFlag IN VARCHAR2, sEmailAddr IN VARCHAR, sCampaign IN VARCHAR, sCouponCode IN VARCHAR ) RETURN NUMBER;
PROCEDURE ACTIVATE_COUPON( p_SiteFlag IN VARCHAR2, p_sEmailAddr IN VARCHAR2, p_sCouponCode IN VARCHAR2, p_nRetCode OUT NUMBER, p_sRetCodeDesc OUT VARCHAR2 );
PROCEDURE COPY_ACTIVATE_COUPON( p_SiteFlag IN VARCHAR2, p_sEmailAddr IN VARCHAR2, p_sCouponCode IN VARCHAR2, p_CampaignEndDate IN VARCHAR2, p_nRetCode OUT NUMBER, p_sRetCodeDesc OUT VARCHAR2 );
PROCEDURE APPLY_COUPON_TO_USAGE_WEB( p_OrderNum IN VARCHAR, p_OriginCd IN VARCHAR, p_SiteFlag IN VARCHAR, p_couponCd IN VARCHAR, p_emailAddr IN VARCHAR, p_UniqueCouponCode IN VARCHAR );
---------------------------------------------
-- UTILITY FUNCTIONS
---------------------------------------------
PROCEDURE CREATE_SUPPORT_REC( p_process_cd IN VARCHAR, p_sub_code IN varchar, p_message IN varchar, p_rec_id OUT NUMBER );
PROCEDURE SEND_ERROR_EMAIL;
---------------------------------------------
---FUNCTIONS
---------------------------------------------
FUNCTION GET_EVENTTYPE_DESC( p_sEventType IN VARCHAR2 ) RETURN VARCHAR2;
FUNCTION GET_JDA_DEPT_SORT_SEQ( p_JDADeptID IN NUMBER, p_siteFlag IN VARCHAR ) RETURN NUMBER;
FUNCTION GET_NEXT_REGNUM RETURN NUMBER;
FUNCTION GET_JDADATE_NOW( p_datetime IN DATE ) RETURN NUMBER;
FUNCTION GET_GMT_DATE RETURN DATE;
FUNCTION GET_GMT_DATE2 RETURN DATE;
FUNCTION FILTER_OPTIONS_SPLIT (p_filterOptions IN VARCHAR2, p_filterPos NUMBER ) RETURN VARCHAR2;
FUNCTION REG_SEARCH_STATE_FILTER( p_stateToCheck VARCHAR2, p_statesIncluded VARCHAR2, p_SiteFlag CHAR ) RETURN NUMBER;
FUNCTION REG_SEARCH_EVENT_FILTER( p_eventTypeToCheck VARCHAR2, p_eventsIncluded VARCHAR2, p_SiteFlag CHAR ) RETURN NUMBER;
FUNCTION GET_PRODUCT_DESC( p_sku IN NUMBER, p_cntryCd CHAR) RETURN VARCHAR2;
FUNCTION GET_PRODUCT_PRICE( p_sku IN NUMBER, p_cntryCd CHAR ) RETURN NUMBER;
FUNCTION GET_GIFTS_REGISTERED( p_RegNum IN NUMBER) RETURN NUMBER;
FUNCTION GET_GIFTS_PURCHASED( p_RegNum IN NUMBER) RETURN NUMBER;
---------------------------------------------
-- MEMBER ACCOUNTS
---------------------------------------------
PROCEDURE GET_MEMBER_INFO( p_username IN varchar, p_origin_cd IN varchar, cur_MemberInfo OUT T_CURSOR );
PROCEDURE GET_ACCT_PWD( p_userName IN VARCHAR, p_originCd IN VARCHAR, p_password OUT VARCHAR );
PROCEDURE GET_ALL_ACCT_PWD( p_Email IN VARCHAR, cur_Info OUT T_CURSOR );

END ATGWS;
/
SHOW ERROR;