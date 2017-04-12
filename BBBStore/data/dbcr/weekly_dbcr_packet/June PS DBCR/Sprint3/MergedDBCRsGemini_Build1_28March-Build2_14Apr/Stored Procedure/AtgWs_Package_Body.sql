SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;
SET DEFINE OFF;
CREATE OR REPLACE PACKAGE BODY ECOMADMIN.ATGWS
AS

---------------------------------------------
-- GEMINI 3.531 
-- REGISTRY 
-- Modification History:
    -- Programmer   Date        Modification
    -- SB           03/02/2016  1. Added GET_GMT_DATE2 function to fix Daylight saving time issue caused in TIBCO Reg Sync.
    --                          2. Replaced all references of GET_GMT_DATE with GET_GMT_DATE2 in all Insert/Update statements.
---------------------------------------------

PROCEDURE GET_REG_LIST_BY_NAME( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR,  p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor     T_CURSOR;
    v_firstNm    VARCHAR2(31) := '';
    v_lastNm     VARCHAR2(31) := '';

BEGIN

    --Gift Givers Search. 
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to TRUE.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.
    --Use Dummy State_Cd if Registrant Stsate Code is null for Sorting purpose 
    --and push the 'ZZ' rows to the bottom the displayed list in the front end and hide the state code 'ZZ'. 
        
    v_firstNm := UPPER(p_vFIRST_NM) || '%';
    v_lastNm  := UPPER(p_vLAST_NM) || '%';
    
    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        IF p_siteFlag <> '2' THEN
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) FIRST_NM, NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                --AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )                    
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
        AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1;                
            --ORDER BY O.last_nm, O.first_nm, H.event_dt;
        ELSE                
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) FIRST_NM, NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                NULL SITE_PUBLISHED_CD, NULL ROW_STATUS, NULL PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)                
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                --AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )                    
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
        AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1; 
            --ORDER BY O.last_nm, O.first_nm, H.event_dt;
        END IF;
    ELSE
        IF p_siteFlag <> '2' THEN
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) FIRST_NM, NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                --AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )                    
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
        AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
                AND REG.ATG_PROFILE_ID IS NULL;
            --ORDER BY O.last_nm, O.first_nm, H.event_dt;
        ELSE                
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) FIRST_NM, NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                NULL SITE_PUBLISHED_CD, NULL ROW_STATUS, NULL PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)                
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                --AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )                    
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
        AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
                AND REG.ATG_PROFILE_ID IS NULL;
            --ORDER BY O.last_nm, O.first_nm, H.event_dt;
        END IF;
    END IF;   

    cur_Info := v_Cursor;

END GET_REG_LIST_BY_NAME;

PROCEDURE GET_REG_LIST_BY_NAME2( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR, p_vFilterOptions IN VARCHAR2, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor     T_CURSOR;
    v_firstNm    VARCHAR2(31) := '';
    v_lastNm     VARCHAR2(31) := '';

BEGIN

    --Gift Givers Search. 
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to TRUE.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.
    --Use Dummy State_Cd if Registrant Stsate Code is null for Sorting purpose 
    --and push the 'ZZ' rows to the bottom the displayed list in the front end and hide the state code 'ZZ'. 
        
    v_firstNm := UPPER(p_vFIRST_NM) || '%';
    v_lastNm  := UPPER(p_vLAST_NM) || '%';
    
    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        IF p_siteFlag <> '2' THEN
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) FIRST_NM, 
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1;
        ELSE                
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) FIRST_NM, 
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                NULL SITE_PUBLISHED_CD, NULL ROW_STATUS, NULL PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)                
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1  
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                  
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1;
        END IF;
    ELSE
        IF p_siteFlag <> '2' THEN
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) FIRST_NM, 
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                                    
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
                AND REG.ATG_PROFILE_ID IS NULL;            
        ELSE                
            OPEN v_Cursor FOR
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) FIRST_NM, 
                NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                NULL SITE_PUBLISHED_CD, NULL ROW_STATUS, NULL PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)                
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'                
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1 
                AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                   
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
                AND REG.ATG_PROFILE_ID IS NULL;            
        END IF;
    END IF;   

    cur_Info := v_Cursor;

END GET_REG_LIST_BY_NAME2;


PROCEDURE GET_REG_LIST_BY_EMAIL( p_sEmailAddr IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR,  p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor        T_CURSOR;
    v_sEmailAddr    reg_names.email_addr%TYPE := '';

BEGIN
    
    --Gift Givers Search. 
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to TRUE.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.

    --v_sEmailAddr := UPPER(TRIM(p_sEmailAddr)) || '%';
    v_sEmailAddr := UPPER(TRIM(p_sEmailAddr));
    
    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR

        SELECT REGISTRY_NUM, STATE_CD, EVENT_DT, EVENT_TYPE, EVENT_DESC, NM_ADDR_SUB_TYPE,
            REG_NAME, LAST_NM, FIRST_NM, MAIDEN, SITE_PUBLISHED_CD, ROW_STATUS, PWS_URL
            FROM
        (
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) FIRST_NM, NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                UPPER(TRIM(REG.EMAIL_ADDR)) EMAIL_ADDR,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                --AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' ) 
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                   
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
        )
        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr));
        --ORDER BY last_nm, first_nm, event_dt;
    ELSE
        OPEN v_Cursor FOR
        SELECT REGISTRY_NUM, STATE_CD, EVENT_DT, EVENT_TYPE, EVENT_DESC, NM_ADDR_SUB_TYPE,
            REG_NAME, LAST_NM, FIRST_NM, MAIDEN, SITE_PUBLISHED_CD, ROW_STATUS, PWS_URL
            FROM
        (
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) FIRST_NM, NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                NULL SITE_PUBLISHED_CD, NULL ROW_STATUS, NULL PWS_URL,
                UPPER(TRIM(REG.EMAIL_ADDR)) EMAIL_ADDR,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)                
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                --AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                    
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
                AND REG.ATG_PROFILE_ID IS NULL
        )
        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr));
        --ORDER BY last_nm, first_nm, event_dt;
    END IF;    

    cur_Info := v_Cursor;

END GET_REG_LIST_BY_EMAIL;

PROCEDURE GET_REG_LIST_BY_EMAIL2( p_sEmailAddr IN VARCHAR, p_vMODE_GIFT_GIVERS IN VARCHAR, p_vFilterOptions IN VARCHAR2,  p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor        T_CURSOR;
    v_sEmailAddr    reg_names.email_addr%TYPE := '';

BEGIN
    
    --Gift Givers Search. 
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to TRUE.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.

    --v_sEmailAddr := UPPER(TRIM(p_sEmailAddr)) || '%';
    v_sEmailAddr := UPPER(TRIM(p_sEmailAddr));
    
    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR

        SELECT REGISTRY_NUM, STATE_CD, EVENT_DT, EVENT_TYPE, EVENT_DESC, NM_ADDR_SUB_TYPE,
            REG_NAME, LAST_NM, FIRST_NM, MAIDEN, COREG_NAME, COREG_LAST_NM, COREG_FIRST_NM, 
            COREG_MAIDEN, SITE_PUBLISHED_CD, ROW_STATUS, PWS_URL, EMAIL_ADDR, SEARCHED_REG_SUB_TYPE
            FROM
        (
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) FIRST_NM, 
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) MAIDEN,
                COREG.STATE_CD COREG_STATE_CD,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM, 
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                UPPER(TRIM(REG.EMAIL_ADDR)) EMAIL_ADDR,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'                
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1  
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                  
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
        )
        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr));
        
    ELSE    
        
        OPEN v_Cursor FOR
        SELECT REGISTRY_NUM, STATE_CD, EVENT_DT, EVENT_TYPE, EVENT_DESC, NM_ADDR_SUB_TYPE,
            REG_NAME, LAST_NM, FIRST_NM, MAIDEN, COREG_NAME, COREG_LAST_NM, COREG_FIRST_NM, 
            COREG_MAIDEN, SITE_PUBLISHED_CD, ROW_STATUS, PWS_URL, EMAIL_ADDR, SEARCHED_REG_SUB_TYPE
            FROM
        (
            SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) FIRST_NM, NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) MAIDEN,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM, 
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                NULL SITE_PUBLISHED_CD, NULL ROW_STATUS, NULL PWS_URL,
                UPPER(TRIM(REG.EMAIL_ADDR)) EMAIL_ADDR,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)                
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'                
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1 
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                   
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
                AND REG.ATG_PROFILE_ID IS NULL
        )
        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr));
                
    END IF;    

    cur_Info := v_Cursor;

END GET_REG_LIST_BY_EMAIL2;

PROCEDURE GET_REG_LIST_BY_REG_NUM( p_vREG_NUM IN NUMBER, p_vMODE_GIFT_GIVERS IN VARCHAR, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor    T_CURSOR;
    v_regNum    reg_names.REGISTRY_NUM%TYPE := '';

BEGIN

    --Gift Givers Search. 
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to TRUE.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.
    
    v_regNum := p_vREG_NUM;
    
    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) FIRST_NM, 
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) MAIDEN,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM, 
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE                
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE REGISTRY_NUM = v_regNum
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' ) 
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                   
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1;
        --ORDER BY O.last_nm, O.first_nm, H.event_dt;
    ELSE
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, NVL(REG.STATE_CD, 'ZZ') STATE_CD, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC, REG.NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) LAST_NM, 
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) FIRST_NM, 
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) MAIDEN,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM, 
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                PWS.SITE_PUBLISHED_CD, PWS.ROW_STATUS, PWS.PWS_URL,
                RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE                
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H, BT_PWS_PERSONAL_SITE PWS,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE REGISTRY_NUM = v_regNum
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.REGISTRY_NUM = PWS.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND REG.ACTION_CD <> 'D'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' ) 
        AND NVL(H.IS_PUBLIC, 'Y') = 'Y'                   
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1
                AND REG.ATG_PROFILE_ID IS NULL;
        --ORDER BY O.last_nm, O.first_nm, H.event_dt;
    END IF;   

    cur_Info := v_Cursor;

END GET_REG_LIST_BY_REG_NUM;

PROCEDURE REGSEARCH_BY_REG_USING_NAME( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor    T_CURSOR;
    v_firstNm    reg_names.first_nm%TYPE := '';
    v_lastNm    reg_names.last_nm%TYPE := '';

BEGIN

    --Registrant Search.
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to False.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.

    v_firstNm := UPPER(p_vFIRST_NM) || '%';
    v_lastNm := UPPER(p_vLAST_NM) || '%';

    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) REG_FIRST_NM,
                NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
        FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
        WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
            AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
            AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
            AND REG.NM_ADDR_SUB_TYPE = 'RE'
            AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
            AND H.ACTION_CD <> 'D'
            AND H.STATUS_CD NOT IN  ('I','C','H')
            AND H.ON_LINE_REG_FLAG = 'Y'
            AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
            AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
        AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag) = 1;
        --ORDER BY REG.last_nm, REG.first_nm, H.event_dt;
    ELSE
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) REG_FIRST_NM,
                NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
        FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
        WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
            AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
            AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
            AND REG.NM_ADDR_SUB_TYPE = 'RE'
            AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
            AND H.ACTION_CD <> 'D'
            AND H.STATUS_CD NOT IN  ('I','C','H')
            AND H.ON_LINE_REG_FLAG = 'Y'
            AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
            AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
            AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1
            AND ( REG.ATG_PROFILE_ID IS NULL OR (COREG.LAST_NM IS NOT NULL AND COREG.ATG_PROFILE_ID IS NULL))
            AND ( REG.ATG_PROFILE_ID IS NULL OR (REG.ATG_PROFILE_ID IS NOT NULL AND REG.ATG_PROFILE_ID <> p_vProfileId))
            AND ( COREG.ATG_PROFILE_ID IS NULL OR (COREG.ATG_PROFILE_ID IS NOT NULL AND COREG.ATG_PROFILE_ID <> p_vProfileId));
        --ORDER BY REG.last_nm, REG.first_nm, H.event_dt;
    END IF;

    cur_Info := v_Cursor;

END REGSEARCH_BY_REG_USING_NAME;

PROCEDURE REGSEARCH_BY_REG_USING_NAME2( p_vFIRST_NM IN VARCHAR, p_vLAST_NM IN VARCHAR, p_vFilterOptions IN VARCHAR2, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor    T_CURSOR;
    v_firstNm    reg_names.first_nm%TYPE := '';
    v_lastNm    reg_names.last_nm%TYPE := '';

BEGIN

    --Registrant Search.
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to False.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.

    v_firstNm := UPPER(p_vFIRST_NM) || '%';
    v_lastNm := UPPER(p_vLAST_NM) || '%';

    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_FULL_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_LAST_NM,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) REG_FIRST_NM,
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_FULL_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
        FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
        WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
            AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
            AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
            AND REG.NM_ADDR_SUB_TYPE = 'RE'
            AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
            AND H.ACTION_CD <> 'D'
            AND H.STATUS_CD NOT IN  ('I','C','H')
            AND H.ON_LINE_REG_FLAG = 'Y'
            AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
            AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1
            AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
            AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1;        
    ELSE
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_FULL_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_LAST_NM,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) REG_FIRST_NM,
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_FULL_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
        FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                ( SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE
                    FROM REG_NAMES
                    WHERE FIRST_NM LIKE v_firstNm AND ( LAST_NM LIKE v_lastNm OR MAIDEN LIKE v_lastNm)
                    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                    AND ACTION_CD <> 'D' ) RNX
        WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
            AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
            AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
            AND REG.NM_ADDR_SUB_TYPE = 'RE'
            AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
            AND H.ACTION_CD <> 'D'
            AND H.STATUS_CD NOT IN  ('I','C','H')
            AND H.ON_LINE_REG_FLAG = 'Y'
            AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
            AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1
            AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
            AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1
            AND ( REG.ATG_PROFILE_ID IS NULL OR (COREG.LAST_NM IS NOT NULL AND COREG.ATG_PROFILE_ID IS NULL))
            AND ( REG.ATG_PROFILE_ID IS NULL OR (REG.ATG_PROFILE_ID IS NOT NULL AND REG.ATG_PROFILE_ID <> p_vProfileId))
            AND ( COREG.ATG_PROFILE_ID IS NULL OR (COREG.ATG_PROFILE_ID IS NOT NULL AND COREG.ATG_PROFILE_ID <> p_vProfileId));
    END IF;

    cur_Info := v_Cursor;

END REGSEARCH_BY_REG_USING_NAME2;


PROCEDURE REGSEARCH_BY_REG_USING_EMAIL( p_sEmailAddr IN VARCHAR, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor    T_CURSOR;
    v_sEmailAddr    reg_names.email_addr%TYPE := '';
BEGIN

    --Registrant Search.
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to False.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.


    --v_sEmailAddr := UPPER(TRIM(p_sEmailAddr)) || '%';
    v_sEmailAddr := UPPER(TRIM(p_sEmailAddr));

    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) REG_FIRST_NM,
                NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                    (
                        SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE, EMAIL_ADDR FROM
                        (
                            SELECT DISTINCT(REGISTRY_NUM) REGISTRY_NUM, NM_ADDR_SUB_TYPE, UPPER(TRIM(EMAIL_ADDR)) EMAIL_ADDR
                            FROM REG_NAMES
                            WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                            AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                            AND ACTION_CD <> 'D'
                        )
                        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr))
                    ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
                AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1;
            --ORDER BY REG.last_nm, REG.first_nm, H.event_dt;
    ELSE
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) REG_FIRST_NM,
                NVL(REG.MAIDEN_COPY,initcap(REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) COREG_FIRST_NM,
                NVL(COREG.MAIDEN_COPY,initcap(COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                    (
                        SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE, EMAIL_ADDR FROM
                        (
                            SELECT DISTINCT(REGISTRY_NUM) REGISTRY_NUM, NM_ADDR_SUB_TYPE, UPPER(TRIM(EMAIL_ADDR)) EMAIL_ADDR
                            FROM REG_NAMES
                            WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                            AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                            AND ACTION_CD <> 'D'
                        )
                        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr))
                    ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
                AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1
                AND ( REG.ATG_PROFILE_ID IS NULL OR (COREG.LAST_NM IS NOT NULL AND COREG.ATG_PROFILE_ID IS NULL))
                AND ( REG.ATG_PROFILE_ID IS NULL OR (REG.ATG_PROFILE_ID IS NOT NULL AND REG.ATG_PROFILE_ID <> p_vProfileId))
                AND ( COREG.ATG_PROFILE_ID IS NULL OR (COREG.ATG_PROFILE_ID IS NOT NULL AND COREG.ATG_PROFILE_ID <> p_vProfileId));
            --ORDER BY REG.last_nm, REG.first_nm, H.event_dt;
    END IF;

    cur_Info := v_Cursor;

END REGSEARCH_BY_REG_USING_EMAIL;

PROCEDURE REGSEARCH_BY_REG_USING_EMAIL2( p_sEmailAddr IN VARCHAR, p_vFilterOptions IN VARCHAR2, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor    T_CURSOR;
    v_sEmailAddr    reg_names.email_addr%TYPE := '';
BEGIN

    --Registrant Search.
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to False.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.


    --v_sEmailAddr := UPPER(TRIM(p_sEmailAddr)) || '%';
    v_sEmailAddr := UPPER(TRIM(p_sEmailAddr));

    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_FULL_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_LAST_NM,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) REG_FIRST_NM,
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_FULL_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                    (
                        SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE, EMAIL_ADDR FROM
                        (
                            SELECT DISTINCT(REGISTRY_NUM) REGISTRY_NUM, NM_ADDR_SUB_TYPE, UPPER(TRIM(EMAIL_ADDR)) EMAIL_ADDR
                            FROM REG_NAMES
                            WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                            AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                            AND ACTION_CD <> 'D'
                        )
                        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr))
                    ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1
                AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1;            
    ELSE
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_FULL_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_LAST_NM,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) REG_FIRST_NM,
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_FULL_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                    (
                        SELECT REGISTRY_NUM, NM_ADDR_SUB_TYPE, EMAIL_ADDR FROM
                        (
                            SELECT DISTINCT(REGISTRY_NUM) REGISTRY_NUM, NM_ADDR_SUB_TYPE, UPPER(TRIM(EMAIL_ADDR)) EMAIL_ADDR
                            FROM REG_NAMES
                            WHERE UPPER(EMAIL_ADDR) = v_sEmailAddr
                            AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO' )
                            AND ACTION_CD <> 'D'
                        )
                        WHERE EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr))
                    ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND REG_SEARCH_STATE_FILTER( REG.STATE_CD,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 1), p_SiteFlag) = 1
                AND REG_SEARCH_EVENT_FILTER( H.EVENT_TYPE,  FILTER_OPTIONS_SPLIT (p_vFilterOptions, 2), p_SiteFlag) = 1
                AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1
                AND ( REG.ATG_PROFILE_ID IS NULL OR (COREG.LAST_NM IS NOT NULL AND COREG.ATG_PROFILE_ID IS NULL))
                AND ( REG.ATG_PROFILE_ID IS NULL OR (REG.ATG_PROFILE_ID IS NOT NULL AND REG.ATG_PROFILE_ID <> p_vProfileId))
                AND ( COREG.ATG_PROFILE_ID IS NULL OR (COREG.ATG_PROFILE_ID IS NOT NULL AND COREG.ATG_PROFILE_ID <> p_vProfileId));
    END IF;

    cur_Info := v_Cursor;

END REGSEARCH_BY_REG_USING_EMAIL2;


PROCEDURE REGSEARCH_BY_REG_USING_REGNUM( p_vREG_NUM IN NUMBER, p_siteFlag in VARCHAR, p_vOnlyIncludeLegacyReg IN VARCHAR, p_vProfileId IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor    T_CURSOR;

BEGIN

    --Registrant Search.
    --This Proc is called from .Net only when GIFT_GIVERS flag is set to False.
    --Sorting is done in .Net based on the input sort column. So removed Order by from proc.

    IF p_vOnlyIncludeLegacyReg = 'N' THEN
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_FULL_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_LAST_NM,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) REG_FIRST_NM,
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_FULL_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                (
                    SELECT DISTINCT(REGISTRY_NUM) REGISTRY_NUM, NM_ADDR_SUB_TYPE, TRIM(EMAIL_ADDR) EMAIL_ADDR
                    FROM REG_NAMES
                    WHERE REGISTRY_NUM = p_vREG_NUM
                    AND NM_ADDR_SUB_TYPE = 'RE'
                    AND ACTION_CD <> 'D'
                ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
                AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1;
            --ORDER BY REG.last_nm, REG.first_nm, H.event_dt;
    ELSE
        OPEN v_Cursor FOR
        SELECT H.REGISTRY_NUM, H.EVENT_DT, H.EVENT_TYPE, GET_EVENTTYPE_DESC(H.EVENT_TYPE) EVENT_DESC,
                REG.STATE_CD REG_STATE_CD, REG.NM_ADDR_SUB_TYPE REG_NM_ADDR_SUB_TYPE,
                --NVL(REG.FIRST_NM_COPY,initcap(REG.FIRST_NM)) || ' ' || NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_FULL_NAME,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_FULL_NAME,
                --NVL(REG.LAST_NM_COPY,initcap(REG.LAST_NM)) REG_LAST_NM, 
                TRIM(INITCAP(NVL(REG.LAST_NM_COPY, REG.LAST_NM))|| ' ' ||INITCAP(REG.LAST_NM2)) REG_LAST_NM,
                INITCAP(NVL(REG.FIRST_NM_COPY, REG.FIRST_NM)) REG_FIRST_NM,
                INITCAP(NVL(REG.MAIDEN_COPY, REG.MAIDEN)) REG_MAIDEN,
                REG.ATG_PROFILE_ID REG_ATG_PROFILE_ID,
                REG.EMAIL_ADDR REG_EMAIL_ADDR,
                COREG.STATE_CD COREG_STATE_CD, COREG.NM_ADDR_SUB_TYPE COREG_NM_ADDR_SUB_TYPE,
                --NVL(COREG.FIRST_NM_COPY,initcap(COREG.FIRST_NM)) || ' ' || NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_FULL_NAME,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) || ' ' || TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_FULL_NAME,
                --NVL(COREG.LAST_NM_COPY,initcap(COREG.LAST_NM)) COREG_LAST_NM, 
                TRIM(INITCAP(NVL(COREG.LAST_NM_COPY, COREG.LAST_NM))|| ' ' ||INITCAP(COREG.LAST_NM2)) COREG_LAST_NM,
                INITCAP(NVL(COREG.FIRST_NM_COPY, COREG.FIRST_NM)) COREG_FIRST_NM,
                INITCAP(NVL(COREG.MAIDEN_COPY, COREG.MAIDEN)) COREG_MAIDEN,
                COREG.ATG_PROFILE_ID COREG_ATG_PROFILE_ID,
                COREG.EMAIL_ADDR COREG_EMAIL_ADDR, RNX.NM_ADDR_SUB_TYPE as SEARCHED_REG_SUB_TYPE
            FROM REG_NAMES REG, REG_NAMES COREG, REG_HEADERS H,
                (
                    SELECT DISTINCT(REGISTRY_NUM) REGISTRY_NUM, NM_ADDR_SUB_TYPE, TRIM(EMAIL_ADDR) EMAIL_ADDR
                    FROM REG_NAMES
                    WHERE REGISTRY_NUM = p_vREG_NUM
                    AND NM_ADDR_SUB_TYPE = 'RE'
                    AND ACTION_CD <> 'D'
                ) RNX
            WHERE H.REGISTRY_NUM = REG.REGISTRY_NUM
                AND H.REGISTRY_NUM = RNX.REGISTRY_NUM
                AND REG.REGISTRY_NUM = COREG.REGISTRY_NUM (+)
                AND REG.NM_ADDR_SUB_TYPE = 'RE'
                AND COREG.NM_ADDR_SUB_TYPE (+) = 'CO'
                AND H.ACTION_CD <> 'D'
                AND H.STATUS_CD NOT IN  ('I','C','H')
                AND H.ON_LINE_REG_FLAG = 'Y'
                AND H.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
                AND NVL(H.IS_PUBLIC, 'Y') = 'Y'
                AND GET_IN_COUNTRY( H.REGISTRY_NUM, p_siteFlag ) = 1
                AND ( REG.ATG_PROFILE_ID IS NULL OR (COREG.LAST_NM IS NOT NULL AND COREG.ATG_PROFILE_ID IS NULL))
                AND ( REG.ATG_PROFILE_ID IS NULL OR (REG.ATG_PROFILE_ID IS NOT NULL AND REG.ATG_PROFILE_ID <> p_vProfileId))
                AND ( COREG.ATG_PROFILE_ID IS NULL OR (COREG.ATG_PROFILE_ID IS NOT NULL AND COREG.ATG_PROFILE_ID <> p_vProfileId));
            --ORDER BY REG.last_nm, REG.first_nm, H.event_dt;
    END IF;

    cur_Info := v_Cursor;

END REGSEARCH_BY_REG_USING_REGNUM;

PROCEDURE GET_REG_NAMES( p_vRegNamesArray IN VARCHAR, p_vSubTypesArray IN VARCHAR, p_Cnt IN NUMBER, p_vOtherNamesArray OUT VARCHAR, p_vOtherStatesArray OUT VARCHAR )
IS

    /*Counters*/
    v_RegCommaPosEnd   NUMBER := 0;
    v_RegCommaPosStart NUMBER := 1;
    v_SubTypeCommaPosEnd   NUMBER := 0;
    v_SubTypeCommaPosStart NUMBER := 1;

    /*Temps*/
    v_RegNum           VARCHAR(11) := '';
    v_SubType          VARCHAR(2) := '';

    /*Select Temps*/
    v_RegNameItem      VARCHAR(80) := '';
    v_StateItem        VARCHAR(3) := '';

    /* Return */
    v_OtherNamesArray VARCHAR(4000) := '';
    v_OtherStatesArray VARCHAR(4000) := '';

    v_RegCount           NUMBER := 0;

BEGIN

    FOR i IN 1..p_Cnt LOOP
        v_RegCommaPosEnd := INSTR( p_vRegNamesArray, '|', 1, i );
        v_RegNum := SUBSTR( p_vRegNamesArray, v_RegCommaPosStart, v_RegCommaPosEnd - v_RegCommaPosStart );

        v_SubTypeCommaPosEnd := INSTR( p_vSubTypesArray, '|', 1, i );
        v_SubType := SUBSTR( p_vSubTypesArray, v_SubTypeCommaPosStart, v_SubTypeCommaPosEnd - v_SubTypeCommaPosStart );

        /*We need the other registrant so we switch subtypes*/
        IF v_SubType = 'RE' THEN
            v_SubType := 'CO';
        ELSE
            v_SubType := 'RE';
        END IF;

        SELECT COUNT(*) INTO v_RegCount
        FROM REG_NAMES
        WHERE REGISTRY_NUM = v_RegNum AND NM_ADDR_SUB_TYPE = v_SubType
        AND ACTION_CD <> 'D';

        IF v_RegCount > 0 THEN

            /*Get Other name */
            SELECT ( LTRIM(RTRIM(initcap(NVL(NVL(FIRST_NM_COPY,FIRST_NM),'')))) || ' ' || LTRIM(RTRIM(initcap(NVL(NVL(LAST_NM_COPY,LAST_NM),''))|| ' ' ||INITCAP(LAST_NM2)))) REG_NAME INTO v_RegNameItem
            FROM REG_NAMES
            WHERE REGISTRY_NUM = v_RegNum AND NM_ADDR_SUB_TYPE = v_SubType
            AND ACTION_CD <> 'D';

            /*Get state*/
            SELECT NVL(STATE_CD,'') INTO v_StateItem
            FROM REG_NAMES
            WHERE REGISTRY_NUM = v_RegNum AND NM_ADDR_SUB_TYPE = v_SubType
            AND ACTION_CD <> 'D';

        ELSE
            /*This happens for college registries where there is no co-registrant*/
            v_RegNameItem := '';
            v_StateItem := '';

        END IF;

        /*Append for returns*/
        v_OtherNamesArray := v_OtherNamesArray || '|' || v_RegNameItem;
        v_OtherStatesArray := v_OtherStatesArray || '|' ||  v_StateItem;

        v_RegCommaPosStart := v_RegCommaPosEnd + 1;
        v_SubTypeCommaPosStart := v_SubTypeCommaPosEnd + 1;

    END LOOP;

    /* Remove comma at the beginning*/
    v_OtherNamesArray := SUBSTR(v_OtherNamesArray, 2, LENGTH(v_OtherNamesArray));
    v_OtherStatesArray := SUBSTR(v_OtherStatesArray, 2, LENGTH(v_OtherStatesArray));

    /*
    TRANSLATING NULL TO A SINGLE SPACE IS BEING DONE BECAUSE WHEN THIS CODE IS EXECUTED IN ANNAPOLIS A NULL VALUE IS BEING SENT AS NULL THE WORD
    BACK TO THE .NET WEB SERVICE. TRIM IS BEING USED IN .NET TO REMOVE THE SPACE.
    */
    p_vOtherStatesArray := nvl(v_OtherStatesArray, ' ');
    p_vOtherNamesArray := nvl(v_OtherNamesArray, ' ');

END GET_REG_NAMES;

PROCEDURE ADD_REG_ITEM( p_registry_num IN NUMBER, p_sku IN NUMBER, p_add_quantity IN NUMBER, p_last_maint_dt_tm IN NUMBER, p_createProg IN VARCHAR,
                    p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2 )
IS

    v_registry_num        Reg_Details.registry_num%TYPE := p_registry_num;
    v_sku                Reg_Details.sku%TYPE := p_sku;
    v_last_maint_dt_tm    Reg_Details.last_maint_dt_tm%TYPE := p_last_maint_dt_tm;
    v_add_quantity        Reg_Details.qty_fulfilled%TYPE := p_add_quantity;

    rec_reg_details        Reg_Details%ROWTYPE;

    is_col_sku            EXCEPTION;
    sku_not_found        EXCEPTION;
    registry_not_found    EXCEPTION;

    v_count                NUMBER    := 0;
    v_is_col            VARCHAR2(1)    := 'N';
    v_sku_count            NUMBER    :=    0;
    v_action_cd            VARCHAR2(1)    := 'C';
BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_registry_num;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;
    /*
    SELECT COUNT(*) INTO v_sku_count FROM ITEMS WHERE SKU = p_sku;
    IF v_sku_count = 0 THEN
        RAISE sku_not_found;
    END IF;

    SELECT COLLECTION_SKU_FLAG INTO v_is_col FROM ITEMS WHERE SKU = p_sku;
    IF UPPER(v_is_col) = 'Y' THEN
        RAISE is_col_sku;
    END IF;
    */
    BEGIN
        -- MAX QTY IS 9999
        IF v_add_quantity > 9999 THEN
            v_add_quantity := 9999;
        END IF;

        INSERT INTO Reg_Details( registry_num, sku, registry_dept, registry_sub_dept, qty_requested,
                qty_fulfilled, qty_purch_resrv, qty_ret_resrv, gen_run, last_maint_user,
                action_cd, process_flag, create_prog, create_user, create_dt_tm,
                last_maint_dt_tm, error_code, row_xng_dt, row_xng_usr, row_status,
                LAST_TRANS_STORE, LAST_MAINT_PROG)
        VALUES(v_registry_num, v_sku, null, null, v_add_quantity,
                    0, 0, 0, NULL, p_RowXngUser,
                    'A', 'W', p_createProg, p_RowXngUser, GET_JDADATE_NOW(SYSDATE),
                    GET_JDADATE_NOW(SYSDATE), NULL, GET_GMT_DATE2(), p_RowXngUser,'A',
                    p_LastTransStore, p_LastMaintProg);

        -- ###### 1. JUST IN CASE BAD SKU IS PASSED FROM FRONT-END.
        -- ###### 2. EXCEPTION FROM DUP RECORD WILL PREVENT THIS CONDITION FROM BEING EVALUATED.
        IF SQL%ROWCOUNT = 0 THEN
            RAISE sku_not_found;
        END IF;

        EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            BEGIN
                SELECT *
                INTO rec_reg_details
                FROM Reg_Details
                WHERE registry_num = v_registry_num
                AND sku = v_sku;

                -- MAX QTY IS 9999
                IF rec_reg_details.qty_requested + v_add_quantity > 9999 THEN
                    v_add_quantity := 9999 - rec_reg_details.qty_requested;
                END IF;

                SELECT action_cd INTO v_action_cd FROM Reg_Details WHERE registry_num = v_registry_num AND sku = v_sku;

                IF v_action_cd = 'D' THEN
                    UPDATE Reg_Details SET
                        qty_requested = v_add_quantity,
                        last_maint_user = p_RowXngUser,
                        action_cd = 'C',
                        process_flag = 'W',
                        last_trans_store = p_LastTransStore,
                        last_maint_prog = p_LastMaintProg,
                        last_maint_dt_tm = GET_JDADATE_NOW(SYSDATE),
                        row_xng_dt = GET_GMT_DATE2(),
                        row_xng_usr = p_RowXngUser
                    WHERE registry_num = v_registry_num
                    AND sku = v_sku;
                ELSE
                    UPDATE Reg_Details SET
                        qty_requested = rec_reg_details.qty_requested + v_add_quantity,
                        last_maint_user = p_RowXngUser,
                        action_cd = 'C',
                        process_flag = 'W',
                        last_trans_store = p_LastTransStore,
                        last_maint_prog = p_LastMaintProg,
                        last_maint_dt_tm = GET_JDADATE_NOW(SYSDATE),
                        row_xng_dt = GET_GMT_DATE2(),
                        row_xng_usr = p_RowXngUser
                    WHERE registry_num = v_registry_num
                    AND sku = v_sku;
                END IF;

                EXCEPTION
                    WHEN OTHERS THEN    -- exception when updating "inserted" record
                        RAISE;
            END;
        WHEN sku_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Could not find SKU::' || v_sku );
        WHEN OTHERS THEN    -- exception for non DUP_VAL_ON_INDEX exception
            RAISE;
    END;

EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_registry_num );
    WHEN is_col_sku THEN
            RAISE_APPLICATION_ERROR( -20501, 'Cannot add collection sku::' || v_sku );
    WHEN sku_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Could not find SKU::' || v_sku );
    WHEN OTHERS THEN
        RAISE;

END ADD_REG_ITEM;

PROCEDURE ADD_REG_ITEM2( p_registry_num IN NUMBER, p_sku IN NUMBER, p_add_quantity IN NUMBER, p_reference_id IN varchar2,  p_itemType IN VARCHAR2, p_assembly_selected IN CHAR,p_assembly_price IN NUMBER, p_ltl_delivery_service IN VARCHAR2, 
                    p_ltl_delivery_price IN NUMBER, p_personlization_code IN VARCHAR2, p_personalization_price IN NUMBER, p_customization_price IN NUMBER, p_personalization_descrip IN VARCHAR2,
                    p_image_url IN VARCHAR2, p_image_url_thumb IN VARCHAR2, p_mob_image_url IN VARCHAR2, p_mob_image_url_thumb IN VARCHAR2, p_last_maint_dt_tm IN NUMBER, p_createProg IN VARCHAR,
                    p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_siteFlag IN VARCHAR2 )
IS

    v_registry_num              Reg_Details.registry_num%TYPE := p_registry_num;
    v_sku                       Reg_Details.sku%TYPE := p_sku;
    v_last_maint_dt_tm          Reg_Details.last_maint_dt_tm%TYPE := p_last_maint_dt_tm;
    v_add_quantity              Reg_Details.qty_fulfilled%TYPE := p_add_quantity;
    v_currency_country          Reg_Details2.currency_country%TYPE;

    v_ltl_delivery_service      Reg_Details2.ltl_delivery_service%TYPE;
    v_assembly_selected          Reg_Details2.assembly_selected%TYPE;
    v_reference_id              Reg_Details2.reference_id%TYPE;
        
    INVALID_DS_AS_VALUES      EXCEPTION;

    rec_reg_details             Reg_Details%ROWTYPE;
    rec_reg_details2            Reg_Details2%ROWTYPE;
    

    is_col_sku                  EXCEPTION;
    sku_not_found               EXCEPTION;
    registry_not_found          EXCEPTION;

    v_count                     NUMBER    := 0;
    v_is_col                    VARCHAR2(1)    := 'N';
    v_sku_count                 NUMBER    :=    0;
    v_action_cd                 VARCHAR2(1)    := 'C';
    p_message                   VARCHAR2(4000);
BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_registry_num;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;
    
    IF p_siteFlag = '1' OR p_siteFlag = '2'  THEN    
        v_currency_country := 'USD';
    ELSIF p_siteFlag = '3' THEN
        v_currency_country := 'CAD';
    ELSIF p_siteFlag = '5' THEN
        v_currency_country := 'MXN';
    END IF;
    
        
    IF (p_itemType = 'PER' OR p_itemType = 'LTL') THEN
        
        BEGIN
            -- MAX QTY IS 9999
            IF v_add_quantity > 9999 THEN
                v_add_quantity := 9999;
            END IF; 

            IF p_itemType = 'LTL' THEN   

        v_ltl_delivery_service := TRIM(p_ltl_delivery_service);
        v_assembly_selected := TRIM(p_assembly_selected);

        IF v_ltl_delivery_service IS NULL AND v_assembly_selected IS NOT NULL THEN
                RAISE INVALID_DS_AS_VALUES;
            END IF;
 
            v_reference_id := NVL(v_ltl_delivery_service || v_assembly_selected, '-1');
            ELSE
        v_ltl_delivery_service := NULL;
        v_assembly_selected := NULL;
                v_reference_id := p_reference_id;
            END IF;
            
            INSERT INTO Reg_Details2( registry_num, sku, registry_dept, registry_sub_dept, qty_requested,
                    qty_fulfilled, qty_purch_resrv, qty_ret_resrv, gen_run, last_maint_user,
                    action_cd, process_flag, create_prog, create_user, create_dt_tm,
                    last_maint_dt_tm, error_code, row_xng_dt, row_xng_usr, row_status,
                    LAST_TRANS_STORE, LAST_MAINT_PROG, reference_id, item_type ,assembly_selected, assembly_price,ltl_delivery_service,
                    ltl_delivery_price, personalization_code, personalization_price, customization_price, personalization_descrip,
                    image_url, image_url_thumb, mob_image_url, mob_image_url_thumb, currency_country)
            VALUES(v_registry_num, v_sku, null, null, v_add_quantity,
                        0, 0, 0, NULL, p_RowXngUser,
                        'A', 'W', p_createProg, p_RowXngUser, GET_JDADATE_NOW(SYSDATE),
                        GET_JDADATE_NOW(SYSDATE), NULL, GET_GMT_DATE2(), p_RowXngUser,'A',
                        p_LastTransStore, p_LastMaintProg, v_reference_id, p_itemType, v_assembly_selected, p_assembly_price,v_ltl_delivery_service,
                        p_ltl_delivery_price, p_personlization_code, p_personalization_price, p_customization_price, p_personalization_descrip,
                        p_image_url, p_image_url_thumb, p_mob_image_url, p_mob_image_url_thumb, v_currency_country);

            -- ###### 1. JUST IN CASE BAD SKU IS PASSED FROM FRONT-END.
            -- ###### 2. EXCEPTION FROM DUP RECORD WILL PREVENT THIS CONDITION FROM BEING EVALUATED.
            IF SQL%ROWCOUNT = 0 THEN
                RAISE sku_not_found;
            END IF;

        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
                BEGIN
                    SELECT *
                    INTO rec_reg_details2
                    FROM ecomadmin.Reg_Details2
                    WHERE registry_num = v_registry_num                    
                    AND sku = v_sku
                    AND reference_id = v_reference_id;

                    -- MAX QTY IS 9999
                    IF rec_reg_details2.qty_requested + v_add_quantity > 9999 THEN
                        v_add_quantity := 9999 - rec_reg_details2.qty_requested;
                    END IF;

                    SELECT action_cd INTO v_action_cd FROM Reg_Details2 WHERE registry_num = v_registry_num AND reference_id = v_reference_id AND sku = v_sku;

                    IF v_action_cd = 'D' THEN
                        UPDATE Reg_Details2 SET
                            qty_requested           = v_add_quantity,
                            last_maint_user         = p_RowXngUser,
                            action_cd               = 'C',
                            process_flag            = 'W',
                            last_trans_store        = p_LastTransStore,
                            last_maint_prog         = p_LastMaintProg,
                            last_maint_dt_tm        = GET_JDADATE_NOW(SYSDATE),
                            row_xng_dt              = GET_GMT_DATE2(),
                            row_xng_usr             = p_RowXngUser,
                            item_type               = p_itemType,
                          assembly_selected       = v_assembly_selected,
                       -- assembly_price          = p_assembly_price,
                          ltl_delivery_service    = v_ltl_delivery_service,
                         -- ltl_delivery_price      = p_ltl_delivery_price,
                            personalization_code    = p_personlization_code,
                            personalization_price   = p_personalization_price,
                            customization_price     = p_customization_price,
                            personalization_descrip = p_personalization_descrip,
                            image_url               = p_image_url,
                            image_url_thumb         = p_image_url_thumb,
                            mob_image_url           = p_mob_image_url,
                            mob_image_url_thumb     = p_mob_image_url_thumb,
                            currency_country        = v_currency_country                            
                        WHERE registry_num          = v_registry_num
                        AND sku = v_sku
                        AND reference_id = v_reference_id;
                    ELSE

            IF NVL(v_ltl_delivery_service, ' ') != NVL(rec_reg_details2.ltl_delivery_service, ' ') OR NVL(v_assembly_selected, ' ') != NVL(rec_reg_details2.assembly_selected, ' ') THEN
                RAISE;
            END IF;

                        UPDATE Reg_Details2 SET
                            qty_requested           = rec_reg_details2.qty_requested + v_add_quantity,
                            last_maint_user         = p_RowXngUser,
                            action_cd               = 'C',
                            process_flag            = 'W',
                            last_trans_store        = p_LastTransStore,
                            last_maint_prog         = p_LastMaintProg,
                            last_maint_dt_tm        = GET_JDADATE_NOW(SYSDATE),
                            row_xng_dt              = GET_GMT_DATE2(),
                            row_xng_usr             = p_RowXngUser,
                            item_type               = p_itemType,
                       -- assembly_selected       = v_assembly_selected,
                       -- assembly_price          = p_assembly_price,
                       -- ltl_delivery_service    = v_ltl_delivery_service,
                       -- ltl_delivery_price      = p_ltl_delivery_price,
                            personalization_code    = p_personlization_code,
                            personalization_price   = p_personalization_price,
                            customization_price     = p_customization_price,
                            personalization_descrip = p_personalization_descrip,
                            image_url               = p_image_url,
                            image_url_thumb         = p_image_url_thumb,
                            mob_image_url           = p_mob_image_url,
                            mob_image_url_thumb     = p_mob_image_url_thumb,
                            currency_country        = v_currency_country
                        WHERE registry_num          = v_registry_num
                        AND sku = v_sku
                        AND reference_id = v_reference_id;
                    END IF;

                    EXCEPTION
                        WHEN OTHERS THEN    -- exception when updating "inserted" record
                            RAISE;
                END;
          WHEN sku_not_found THEN
              RAISE_APPLICATION_ERROR( -20501, 'Could not find SKU::' || v_sku );
          WHEN INVALID_DS_AS_VALUES THEN
          RAISE_APPLICATION_ERROR( -20501, 'Invalid delivery service / assembly selected::' || p_registry_num );
          WHEN OTHERS THEN    -- exception for non DUP_VAL_ON_INDEX exception
              RAISE;  
        END;          
        
    ELSIF p_itemType = 'REG' THEN
    
        BEGIN         
            -- MAX QTY IS 9999
            IF v_add_quantity > 9999 THEN
                v_add_quantity := 9999;
            END IF;

            INSERT INTO Reg_Details( registry_num, sku, registry_dept, registry_sub_dept, qty_requested,
                    qty_fulfilled, qty_purch_resrv, qty_ret_resrv, gen_run, last_maint_user,
                    action_cd, process_flag, create_prog, create_user, create_dt_tm,
                    last_maint_dt_tm, error_code, row_xng_dt, row_xng_usr, row_status,
                    LAST_TRANS_STORE, LAST_MAINT_PROG)
            VALUES(v_registry_num, v_sku, null, null, v_add_quantity,
                        0, 0, 0, NULL, p_RowXngUser,
                        'A', 'W', p_createProg, p_RowXngUser, GET_JDADATE_NOW(SYSDATE),
                        GET_JDADATE_NOW(SYSDATE), NULL, GET_GMT_DATE2(), p_RowXngUser,'A',
                        p_LastTransStore, p_LastMaintProg);

            -- ###### 1. JUST IN CASE BAD SKU IS PASSED FROM FRONT-END.
            -- ###### 2. EXCEPTION FROM DUP RECORD WILL PREVENT THIS CONDITION FROM BEING EVALUATED.
            IF SQL%ROWCOUNT = 0 THEN
                RAISE sku_not_found;
            END IF;

        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
                BEGIN
                    SELECT *
                    INTO rec_reg_details
                    FROM Reg_Details
                    WHERE registry_num = v_registry_num
                    AND sku = v_sku;

                    -- MAX QTY IS 9999
                    IF rec_reg_details.qty_requested + v_add_quantity > 9999 THEN
                        v_add_quantity := 9999 - rec_reg_details.qty_requested;
                    END IF;

                    SELECT action_cd INTO v_action_cd FROM Reg_Details WHERE registry_num = v_registry_num AND sku = v_sku;

                    IF v_action_cd = 'D' THEN
                        UPDATE Reg_Details SET
                            qty_requested = v_add_quantity,
                            last_maint_user = p_RowXngUser,
                            action_cd = 'C',
                            process_flag = 'W',
                            last_trans_store = p_LastTransStore,
                            last_maint_prog = p_LastMaintProg,
                            last_maint_dt_tm = GET_JDADATE_NOW(SYSDATE),
                            row_xng_dt = GET_GMT_DATE2(),
                            row_xng_usr = p_RowXngUser
                        WHERE registry_num = v_registry_num
                        AND sku = v_sku;
                    ELSE
                        UPDATE Reg_Details SET
                            qty_requested = rec_reg_details.qty_requested + v_add_quantity,
                            last_maint_user = p_RowXngUser,
                            action_cd = 'C',
                            process_flag = 'W',
                            last_trans_store = p_LastTransStore,
                            last_maint_prog = p_LastMaintProg,
                            last_maint_dt_tm = GET_JDADATE_NOW(SYSDATE),
                            row_xng_dt = GET_GMT_DATE2(),
                            row_xng_usr = p_RowXngUser
                        WHERE registry_num = v_registry_num
                        AND sku = v_sku;
                    END IF;

                    EXCEPTION
                        WHEN OTHERS THEN    -- exception when updating "inserted" record
                            RAISE;
                END;
                
          WHEN sku_not_found THEN
          RAISE_APPLICATION_ERROR( -20501, 'Could not find SKU::' || v_sku );
          WHEN OTHERS THEN    -- exception for non DUP_VAL_ON_INDEX exception
          RAISE;
                
        END;
    
    END IF;    
    
   -- END;

EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_registry_num );
    WHEN is_col_sku THEN
            RAISE_APPLICATION_ERROR( -20501, 'Cannot add collection sku::' || v_sku );
    WHEN sku_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Could not find SKU::' || v_sku );
    WHEN OTHERS THEN
        RAISE;

END ADD_REG_ITEM2;

PROCEDURE PUT_REG_ITEM( p_RegNum IN NUMBER, p_rowID IN VARCHAR, p_ReqQty IN NUMBER, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2 )
IS

    v_reqQty REG_DETAILS.QTY_REQUESTED%TYPE;
    v_rowID VARCHAR2(38);
    v_count                NUMBER    := 0;
    registry_not_found    EXCEPTION;

BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    v_reqQty := p_ReqQty;
    v_rowID := replace(p_rowID,' ', '+'); --Replace is done to fix a bug which is caused during the GET request of webservice in which + is converted into black space

    IF v_reqQty > 0 THEN
        BEGIN
            UPDATE REG_DETAILS
                SET QTY_REQUESTED = v_reqQty,
                    LAST_MAINT_USER = p_RowXngUser,
                    LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                    ACTION_CD = 'C',
                    PROCESS_FLAG = 'W',
                    ROW_XNG_USR = p_RowXngUser,
                    ROW_XNG_DT = GET_GMT_DATE2(),
                    LAST_TRANS_STORE = p_LastTransStore,
                    LAST_MAINT_PROG    = p_LastMaintProg
            WHERE ROWID = p_rowID;
        END;
    ELSIF v_reqQty = -1 OR v_reqQty = 0 THEN
        BEGIN
            --Sleep one second when Item is deleted from a Registry.
            dbms_lock.sleep(1);
            UPDATE REG_DETAILS
                SET QTY_REQUESTED = 0,
                ROW_XNG_DT = GET_GMT_DATE2(),
                ACTION_CD = 'D',
                PROCESS_FLAG = 'W',
                LAST_MAINT_USER = p_RowXngUser,
                LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                ROW_XNG_USR = p_RowXngUser,
                LAST_TRANS_STORE = p_LastTransStore,
                LAST_MAINT_PROG    = p_LastMaintProg
            WHERE ROWID = p_rowID;
        END;
    END IF;

    EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END PUT_REG_ITEM;

PROCEDURE PUT_REG_ITEM2( p_RegNum IN NUMBER, p_rowID IN VARCHAR, p_ReqQty IN NUMBER, p_itemType VARCHAR2, p_assembly_selected IN CHAR,p_assembly_price IN NUMBER, p_ltl_delivery_service IN VARCHAR2, p_ltl_delivery_price IN NUMBER, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_siteFlag IN VARCHAR2 )
IS

    v_reqQty                    REG_DETAILS.QTY_REQUESTED%TYPE;
    v_rowID                     VARCHAR2(38);
    v_count                     NUMBER    := 0;
    registry_not_found          EXCEPTION;
    INVALID_DS_AS_VALUES      EXCEPTION;
    INVALID_ITEM_TYPE_ROWID    EXCEPTION;
    v_currency_country          Reg_Details2.currency_country%TYPE;

    v_ltl_delivery_service      Reg_Details2.ltl_delivery_service%TYPE;
    v_assembly_selected          Reg_Details2.assembly_selected%TYPE;
    v_reference_id              Reg_Details2.reference_id%TYPE;

    rec_reg_details2            Reg_Details2%ROWTYPE;

BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    IF p_siteFlag = '1' OR p_siteFlag = '2'  THEN    
        v_currency_country := 'USD';
    ELSIF p_siteFlag = '3' THEN
        v_currency_country := 'CAD';
    ELSIF p_siteFlag = '5' THEN
        v_currency_country := 'MXN';
    END IF;


    v_reqQty := p_ReqQty;
    v_rowID := replace(p_rowID,' ', '+'); --Replace is done to fix a bug which is caused during the GET request of webservice in which + is converted into black space

    v_count := 0;

    IF p_itemType = 'REG' THEN

    SELECT COUNT(*) INTO v_count
    FROM Reg_Details
    WHERE ROWID = p_rowID
    AND REGISTRY_NUM = p_RegNum;

    ELSIF (p_itemType = 'PER' OR p_itemType = 'LTL') THEN

    SELECT COUNT(*) INTO v_count
    FROM Reg_Details2
    WHERE ROWID = p_rowID
    AND REGISTRY_NUM = p_RegNum
         AND ITEM_TYPE = p_itemType;

    END IF;

    IF v_count = 0 THEN
        RAISE INVALID_ITEM_TYPE_ROWID;
    END IF;

    IF v_reqQty > 0 THEN
    
        BEGIN

            IF (p_itemType = 'LTL') THEN
 
        v_ltl_delivery_service := TRIM(p_ltl_delivery_service);
        v_assembly_selected := TRIM(p_assembly_selected);

        IF v_ltl_delivery_service IS NULL AND v_assembly_selected IS NOT NULL THEN
                RAISE INVALID_DS_AS_VALUES;
            END IF;

                  SELECT *
                INTO rec_reg_details2
                FROM Reg_Details2
        WHERE ROWID = p_rowID;

         -- IF rec_reg_details2.ltl_delivery_service IS NOT NULL AND v_ltl_delivery_service IS NULL THEN
             --       RAISE INVALID_DS_AS_VALUES;
             -- END IF;

        v_reference_id := NVL(v_ltl_delivery_service || v_assembly_selected, '-1');

        IF v_reference_id != rec_reg_details2.reference_id OR NVL(v_ltl_delivery_service, ' ') != NVL(rec_reg_details2.ltl_delivery_service, ' ') OR NVL(v_assembly_selected, ' ') != NVL(rec_reg_details2.assembly_selected, ' ') THEN

            IF rec_reg_details2.qty_fulfilled > 0 THEN
                    RAISE INVALID_DS_AS_VALUES;
            END IF;

            SELECT COUNT(*) INTO v_count
            FROM Reg_Details2
                    WHERE registry_num = rec_reg_details2.registry_num                  
                    AND sku = rec_reg_details2.sku
                AND ROWID != p_rowID
            AND action_cd != 'D'
                    AND reference_id = v_reference_id;
    
            IF v_count > 0 THEN
                    RAISE INVALID_DS_AS_VALUES;
                END IF;

            DELETE FROM Reg_Details2
                    WHERE registry_num = rec_reg_details2.registry_num                  
                    AND sku = rec_reg_details2.sku
                AND ROWID != p_rowID
            AND action_cd = 'D'
            AND reference_id = v_reference_id;

                    UPDATE REG_DETAILS2
                    SET QTY_REQUESTED           = v_reqQty,
                            ITEM_TYPE               = p_itemType,
                            ASSEMBLY_SELECTED       = p_assembly_selected,
                      -- ASSEMBLY_PRICE          = p_assembly_price,
                            LTL_DELIVERY_SERVICE    = p_ltl_delivery_service,
                      -- LTL_DELIVERY_PRICE      = p_ltl_delivery_price,
                            LAST_MAINT_USER         = p_RowXngUser,
                            LAST_MAINT_DT_TM        = GET_JDADATE_NOW(SYSDATE),
                            ACTION_CD               = 'C',
                            PROCESS_FLAG            = 'W',
                            ROW_XNG_USR             = p_RowXngUser,
                            ROW_XNG_DT              = GET_GMT_DATE2(),
                               LAST_TRANS_STORE        = p_LastTransStore,
                               LAST_MAINT_PROG         = p_LastMaintProg,
                               CURRENCY_COUNTRY        = v_currency_country,
                    REFERENCE_ID        = v_reference_id
                       WHERE ROWID = p_rowID;

        ELSE
                    UPDATE REG_DETAILS2
                    SET QTY_REQUESTED           = v_reqQty,
                            ITEM_TYPE               = p_itemType,
                       -- ASSEMBLY_SELECTED       = p_assembly_selected,
                      -- ASSEMBLY_PRICE          = p_assembly_price,
                      -- LTL_DELIVERY_SERVICE    = p_ltl_delivery_service,
                      -- LTL_DELIVERY_PRICE      = p_ltl_delivery_price,
                            LAST_MAINT_USER         = p_RowXngUser,
                            LAST_MAINT_DT_TM        = GET_JDADATE_NOW(SYSDATE),
                            ACTION_CD               = 'C',
                            PROCESS_FLAG            = 'W',
                            ROW_XNG_USR             = p_RowXngUser,
                            ROW_XNG_DT              = GET_GMT_DATE2(),
                               LAST_TRANS_STORE        = p_LastTransStore,
                               LAST_MAINT_PROG         = p_LastMaintProg,
                               CURRENCY_COUNTRY        = v_currency_country
                       WHERE ROWID = p_rowID; 

        END IF;

        ELSIF p_itemType = 'PER' THEN 

                UPDATE REG_DETAILS2
                SET QTY_REQUESTED           = v_reqQty,
                    ITEM_TYPE               = p_itemType,
                 -- ASSEMBLY_SELECTED       = p_assembly_selected,
                 -- ASSEMBLY_PRICE          = p_assembly_price,
                 -- LTL_DELIVERY_SERVICE    = p_ltl_delivery_service,
                 -- LTL_DELIVERY_PRICE      = p_ltl_delivery_price,
                    LAST_MAINT_USER         = p_RowXngUser,
                    LAST_MAINT_DT_TM        = GET_JDADATE_NOW(SYSDATE),
                    ACTION_CD               = 'C',
                    PROCESS_FLAG            = 'W',
                    ROW_XNG_USR             = p_RowXngUser,
                    ROW_XNG_DT              = GET_GMT_DATE2(),
                       LAST_TRANS_STORE        = p_LastTransStore,
                       LAST_MAINT_PROG         = p_LastMaintProg,
                       CURRENCY_COUNTRY        = v_currency_country
                   WHERE ROWID = p_rowID; 
            
            ELSIF p_itemType = 'REG' THEN
            
                UPDATE REG_DETAILS
                SET QTY_REQUESTED = v_reqQty,
                    LAST_MAINT_USER = p_RowXngUser,
                    LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                    ACTION_CD = 'C',
                    PROCESS_FLAG = 'W',
                    ROW_XNG_USR = p_RowXngUser,
                    ROW_XNG_DT = GET_GMT_DATE2(),
                    LAST_TRANS_STORE = p_LastTransStore,
                    LAST_MAINT_PROG    = p_LastMaintProg
                WHERE ROWID = p_rowID;
            END IF;
        END;
        
    ELSIF v_reqQty = -1 OR v_reqQty = 0 THEN
    
        BEGIN
            --Sleep one second when Item is deleted from a Registry.
            dbms_lock.sleep(1);
           IF (p_itemType = 'PER' OR p_itemType = 'LTL') THEN
           
                UPDATE REG_DETAILS2
                    SET QTY_REQUESTED       = 0,
                    ITEM_TYPE               = p_itemType,
                 -- ASSEMBLY_SELECTED       = p_assembly_selected,
                 -- ASSEMBLY_PRICE          = p_assembly_price,
                 -- LTL_DELIVERY_SERVICE    = p_ltl_delivery_service,
                 -- LTL_DELIVERY_PRICE      = p_ltl_delivery_price,
                    ROW_XNG_DT              = GET_GMT_DATE2(),
                    ACTION_CD               = 'D',
                    PROCESS_FLAG            = 'W',
                    LAST_MAINT_USER         = p_RowXngUser,
                    LAST_MAINT_DT_TM        = GET_JDADATE_NOW(SYSDATE),
                    ROW_XNG_USR             = p_RowXngUser,
                    LAST_TRANS_STORE        = p_LastTransStore,
                    LAST_MAINT_PROG         = p_LastMaintProg,
                    CURRENCY_COUNTRY        = v_currency_country
                WHERE ROWID = p_rowID;          
            
           ELSIF p_itemType = 'REG'  THEN
            
                UPDATE REG_DETAILS
                    SET QTY_REQUESTED = 0,
                    ROW_XNG_DT = GET_GMT_DATE2(),
                    ACTION_CD = 'D',
                    PROCESS_FLAG = 'W',
                    LAST_MAINT_USER = p_RowXngUser,
                    LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                    ROW_XNG_USR = p_RowXngUser,
                    LAST_TRANS_STORE = p_LastTransStore,
                    LAST_MAINT_PROG    = p_LastMaintProg
                WHERE ROWID = p_rowID;
           END IF;
        END;
    END IF;

    EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN INVALID_DS_AS_VALUES THEN
        RAISE_APPLICATION_ERROR( -20501, 'Invalid delivery service / assembly selected::' || p_ltl_delivery_service || ' / ' || p_assembly_selected );
    WHEN INVALID_ITEM_TYPE_ROWID THEN
        RAISE_APPLICATION_ERROR( -20501, 'Invalid item type / ROWID' );
 -- WHEN DUP_VAL_ON_INDEX THEN
    WHEN OTHERS THEN
        RAISE;

END PUT_REG_ITEM2;

PROCEDURE GET_REG_ITEM_LIST( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR )
IS

    v_Cursor T_CURSOR;
    v_count                NUMBER    := 0;
    registry_not_found    EXCEPTION;
BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;

/*
    p_Mode = 0 for registrant; 1 for gift giver
    p_BelowLine = 1 DO NOT GET ITEMS BELOW THE LINE; p_BelowLine = 0 GET ITEMS BELOW THE LINE
*/
    IF p_SiteFlag = '1' OR p_SiteFlag = '2' OR p_SiteFlag = '3' THEN
    
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            NULL JDA_RETAIL_PRICE, NULL FILE_LOC,
            NULL STYLE_DESCRIP, NULL PRODUCT_DESCRIP, NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    ELSIF (p_SiteFlag = '5') THEN
        
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            GET_PRODUCT_PRICE(R.SKU, 'MX') JDA_RETAIL_PRICE,  
            NULL FILE_LOC,
            NULL STYLE_DESCRIP, 
            GET_PRODUCT_DESC(R.SKU, 'MX') PRODUCT_DESCRIP, 
            NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    END IF;
    
    cur_Info := v_Cursor;

    EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END GET_REG_ITEM_LIST;

PROCEDURE GET_REG_ITEM_LIST2( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR )
IS

    v_Cursor T_CURSOR;
    v_count                NUMBER    := 0;
    registry_not_found    EXCEPTION;
BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;

/*
    p_Mode = 0 for registrant; 1 for gift giver
    p_BelowLine = 1 DO NOT GET ITEMS BELOW THE LINE; p_BelowLine = 0 GET ITEMS BELOW THE LINE
*/
    IF p_SiteFlag = '1' OR p_SiteFlag = '2' OR p_SiteFlag = '3' THEN
    
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            NULL JDA_RETAIL_PRICE, NULL FILE_LOC,
            NULL STYLE_DESCRIP, NULL PRODUCT_DESCRIP, NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG,
            NULL REFERENCE_ID,
            NULL ITEM_TYPE,
            NULL ASSEMBLY_SELECTED,
            NULL ASSEMBLY_PRICE,
            NULL LTL_DELIVERY_SERVICE,
            NULL LTL_DELIVERY_PRICE,
            NULL PERSONALIZATION_CODE,
            NULL PERSONALIZATION_PRICE,
            NULL CUSTOMIZATION_PRICE,
            NULL PERSONALIZATION_DESCRIP,
            NULL IMAGE_URL,
            NULL IMAGE_URL_THUMB,
            NULL MOB_IMAGE_URL,
            NULL MOB_IMAGE_URL_THUMB            
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum
        --Union the personalized/LTL items.
        UNION
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            NULL JDA_RETAIL_PRICE, NULL FILE_LOC,
            NULL STYLE_DESCRIP, NULL PRODUCT_DESCRIP, NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG,
            REFERENCE_ID,
            ITEM_TYPE,
            ASSEMBLY_SELECTED,
            ASSEMBLY_PRICE,
            LTL_DELIVERY_SERVICE,
            LTL_DELIVERY_PRICE,
            PERSONALIZATION_CODE,
            PERSONALIZATION_PRICE,
            CUSTOMIZATION_PRICE,
            PERSONALIZATION_DESCRIP,
            IMAGE_URL,
            IMAGE_URL_THUMB,
            MOB_IMAGE_URL,
            MOB_IMAGE_URL_THUMB
        FROM REG_DETAILS2 R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    ELSIF (p_SiteFlag = '5') THEN
        
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            GET_PRODUCT_PRICE(R.SKU, 'MX') JDA_RETAIL_PRICE,  
            NULL FILE_LOC,
            NULL STYLE_DESCRIP, 
            GET_PRODUCT_DESC(R.SKU, 'MX') PRODUCT_DESCRIP, 
            NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG,
            NULL REFERENCE_ID,
            NULL ITEM_TYPE,
            NULL ASSEMBLY_SELECTED,
            NULL ASSEMBLY_PRICE,
            NULL LTL_DELIVERY_SERVICE,
            NULL LTL_DELIVERY_PRICE,
            NULL PERSONALIZATION_CODE,
            NULL PERSONALIZATION_PRICE,
            NULL CUSTOMIZATION_PRICE,
            NULL PERSONALIZATION_DESCRIP,
            NULL IMAGE_URL,
            NULL IMAGE_URL_THUMB,
            NULL MOB_IMAGE_URL,
            NULL MOB_IMAGE_URL_THUMB            
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    END IF;
    
    cur_Info := v_Cursor;

    EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END GET_REG_ITEM_LIST2;

PROCEDURE GET_REG_ITEM_LIST_BY_PRICE( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR )
IS

    v_Cursor T_CURSOR;
    v_count                NUMBER    := 0;
    registry_not_found    EXCEPTION;
BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;

/*
    p_Mode = 0 for registrant; 1 for gift giver
    p_BelowLine = 1 DO NOT GET ITEMS BELOW THE LINE; p_BelowLine = 0 GET ITEMS BELOW THE LINE
*/
    IF p_SiteFlag = '1' OR p_SiteFlag = '2' OR p_SiteFlag = '3' THEN
    
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            NULL JDA_RETAIL_PRICE, NULL FILE_LOC,
            NULL STYLE_DESCRIP, NULL PRODUCT_DESCRIP, NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    ELSIF (p_SiteFlag = '5') THEN
        
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            GET_PRODUCT_PRICE(R.SKU, 'MX') JDA_RETAIL_PRICE,  
            NULL FILE_LOC,
            NULL STYLE_DESCRIP, 
            GET_PRODUCT_DESC(R.SKU, 'MX') PRODUCT_DESCRIP, 
            NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    END IF;

    cur_Info := v_Cursor;

    EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END GET_REG_ITEM_LIST_BY_PRICE;

PROCEDURE GET_REG_ITEM_LIST_BY_PRICE2( p_siteFlag IN VARCHAR, p_RegNum IN NUMBER, p_Mode IN NUMBER, p_BelowLine IN NUMBER, p_LowPrice IN NUMBER, p_HighPrice IN NUMBER, cur_Info OUT T_CURSOR )
IS

    v_Cursor T_CURSOR;
    v_count                NUMBER    := 0;
    registry_not_found    EXCEPTION;
BEGIN

    SELECT COUNT(*) INTO v_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF v_count = 0 THEN
        RAISE registry_not_found;
    END IF;

/*
    p_Mode = 0 for registrant; 1 for gift giver
    p_BelowLine = 1 DO NOT GET ITEMS BELOW THE LINE; p_BelowLine = 0 GET ITEMS BELOW THE LINE
*/
    IF p_SiteFlag = '1' OR p_SiteFlag = '2' OR p_SiteFlag = '3' THEN
    
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            NULL JDA_RETAIL_PRICE, NULL FILE_LOC,
            NULL STYLE_DESCRIP, NULL PRODUCT_DESCRIP, NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG,
            NULL REFERENCE_ID,
            NULL ITEM_TYPE,
            NULL ASSEMBLY_SELECTED,
            NULL ASSEMBLY_PRICE,
            NULL LTL_DELIVERY_SERVICE,
            NULL LTL_DELIVERY_PRICE,
            NULL PERSONALIZATION_CODE,
            NULL PERSONALIZATION_PRICE,
            NULL CUSTOMIZATION_PRICE,
            NULL PERSONALIZATION_DESCRIP,
            NULL IMAGE_URL,
            NULL IMAGE_URL_THUMB,
            NULL MOB_IMAGE_URL,
            NULL MOB_IMAGE_URL_THUMB            
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum
        --Union the personalized/LTL items.
        UNION
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            NULL JDA_RETAIL_PRICE, NULL FILE_LOC,
            NULL STYLE_DESCRIP, NULL PRODUCT_DESCRIP, NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG,
            REFERENCE_ID,
            ITEM_TYPE,
            ASSEMBLY_SELECTED,
            ASSEMBLY_PRICE,
            LTL_DELIVERY_SERVICE,
            LTL_DELIVERY_PRICE,
            PERSONALIZATION_CODE,
            PERSONALIZATION_PRICE,
            CUSTOMIZATION_PRICE,
            PERSONALIZATION_DESCRIP,
            IMAGE_URL,
            IMAGE_URL_THUMB,
            MOB_IMAGE_URL,
            MOB_IMAGE_URL_THUMB
        FROM REG_DETAILS2 R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    ELSIF (p_SiteFlag = '5') THEN
        
        OPEN v_Cursor FOR
        SELECT R.SKU,
            R.ROWID,
            R.QTY_REQUESTED,
            CASE WHEN R.QTY_FULFILLED < 0 THEN 0 ELSE R.QTY_FULFILLED END QTY_FULFILLED,
            R.QTY_PURCH_RESRV,
            GET_PRODUCT_PRICE(R.SKU, 'MX') JDA_RETAIL_PRICE,  
            NULL FILE_LOC,
            NULL STYLE_DESCRIP, 
            GET_PRODUCT_DESC(R.SKU, 'MX') PRODUCT_DESCRIP, 
            NULL JDANM, NULL WEB_OFFERED_FLAG, NULL DISABLE_FLAG,
            NULL JDA_DEPT_ID, NULL AVAIL_FOR_SALE_QTY, NULL DESCRIP, NULL OVR_WGHT_FLAG, NULL OVR_SIZE_FLAG, NULL AVAIL_FOR_SALE_IGR_QTY,
            NULL COLOR_CD, NULL COLOR_DESCRIP, NULL UPC,
            NULL SORT_DEPT, NULL ATTRIB_ID_VALUE_PAIR,
            NULL AVAIL_FOR_SALE_IGR_FLAG,
            NULL REFERENCE_ID,
            NULL ITEM_TYPE,
            NULL ASSEMBLY_SELECTD,
            NULL ASSEMBLY_PRICE,
            NULL LTL_DELIVERY_SERVICE,
            NULL LTL_DELIVERY_PRICE,
            NULL PERSONALIZATION_CODE,
            NULL PERSONALIZATION_PRICE,
            NULL CUSTOMIZATION_PRICE,
            NULL PERSONALIZATION_DESCRIP,
            NULL IMAGE_URL,
            NULL IMAGE_URL_THUMB,
            NULL MOB_IMAGE_URL,
            NULL MOB_IMAGE_URL_THUMB
        FROM REG_DETAILS R
        WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_RegNum;
    
    END IF;

    cur_Info := v_Cursor;

    EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END GET_REG_ITEM_LIST_BY_PRICE2;

PROCEDURE INSERT_REG_HEADER( p_EventType IN VARCHAR, p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER, p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2,
            p_RegNum OUT NUMBER, p_JDADateNow OUT NUMBER )
IS

    v_regNum            NUMBER        := 0;
    v_JDADate           NUMBER        := 0; -- convert to string to truncate time portion and convert back to number.
    v_JDADateNow        NUMBER        := 0; -- call function to get this.
    v_annc_cards_cnt    NUMBER        := 0;

BEGIN

    v_regNum    := GET_NEXT_REGNUM;-- GET NEXT AVAILABLE REGISTRY NUMBER.

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;
    v_JDADate := TO_NUMBER( SUBSTR( TO_CHAR( v_JDADateNow ), 1, 8 ) ); -- ONLY NEED DATE PORTION OF DATETIME.

    -- SET ANNC_CARDS_REQ TO DEFAULT COUNT 25 FOR "BRD" AND "COM "EVENT TYPES.

    IF p_EventType = 'BRD' OR p_EventType = 'COM' THEN
        v_annc_cards_cnt := 25;
    END IF;

    BEGIN
        INSERT INTO reg_headers
        ( REGISTRY_NUM, EVENT_TYPE, EVENT_DT, EMAIL_FLAG,
            REFERRAL_CD, RECENT_PURCH_ACTIVITY,
            ACTION_CD, STATUS_CD, SERIES_CD, ANNC_CARDS_REQ, ERROR_CODE, ORIGIN_FLAG,
            LAST_MAINT_USER, STORE_NUM, REGISTRY_DT, REGISTRY_CONSULTANT1, PROCESS_FLAG, ON_LINE_REG_FLAG, CREATION_PROG, CREATION_USER,
            ROW_XNG_DT, CREATE_DT_TM, LAST_MAINT_DT_TM, STORE_NUM_GR, ROW_XNG_USR, ROW_STATUS,
            SHOWER_DATE, OTHER_DATE, NETWORK_AFFILIATE_FLAG, NUMBER_OF_GUEST, LAST_TRANS_STORE, LAST_MAINT_PROG
        )
        VALUES
        ( v_regNum, p_EventType, p_EventDate, p_PromoEmailFlag,
            NULL, NULL,
            'A', 'A', 'N', v_annc_cards_cnt, NULL, '2',
            p_RowXngUser, p_StoreNum, v_JDADate, USER, 'W', 'Y', p_creationProg, p_RowXngUser,
            GET_GMT_DATE2(), v_JDADateNow, v_JDADateNow, p_StoreNumGr, p_RowXngUser, 'A',
            TO_DATE(TO_CHAR(p_showerDate),'YYYYMMDD'), TO_DATE(TO_CHAR(p_otherDate),'YYYYMMDD'), p_NetworkAffFlag, p_numGuests, p_LastTransStore, p_LastMaintProg
        );

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

    p_RegNum        := v_regNum;
    p_JDADateNow    := v_JDADateNow;

END INSERT_REG_HEADER;

PROCEDURE INSERT_REG_HEADER2( p_EventType IN VARCHAR, p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER, p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER, p_IsPublic IN VARCHAR, p_SiteFlag IN VARCHAR,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2,
            p_RegNum OUT NUMBER, p_JDADateNow OUT NUMBER )
IS

    v_regNum                NUMBER        := 0;
    v_JDADate               NUMBER        := 0; -- convert to string to truncate time portion and convert back to number.
    v_JDADateNow            NUMBER        := 0; -- call function to get this.
    v_annc_cards_cnt        NUMBER        := 0;
    v_SiteFlag            NUMBER          := 1;
    v_StoreNum            NUMBER        := p_StoreNum;
    v_StoreNumGr        NUMBER        := p_StoreNumGr;
    v_RegistryConsultant    VARCHAR2(30)  := USER;
    v_creationProg        VARCHAR2(10)  := p_creationProg;    
    v_lastMaintProg        VARCHAR2(10)  := p_LastMaintProg;
    v_LastTransStore        NUMBER          := p_LastTransStore;

BEGIN

    IF SUBSTR(p_Password, 1, 4) = 'TBS:' THEN

        SELECT DECODE(p_StoreNum, 651, 1, 3651, 2, 2291, 3, 1) INTO v_SiteFlag FROM DUAL;
        v_RegistryConsultant := NVL(UPPER(TRIM(REGEXP_SUBSTR(p_Password, '[^\:]+', 1, 2))), USER);
        v_StoreNum := NVL(TRIM(REGEXP_SUBSTR(p_Password, '[^\:]+', 1, 3)), p_StoreNum);
        v_StoreNumGr := NVL(TRIM(REGEXP_SUBSTR(p_Password, '[^\:]+', 1, 3)), p_StoreNumGr);
        v_LastTransStore := NVL(TRIM(REGEXP_SUBSTR(p_Password, '[^\:]+', 1, 3)), p_LastTransStore);
        SELECT DECODE(UPPER(TRIM(p_creationProg)), 'BBBY.COM', 'BBBYTBS', 'BAB.COM', 'BABTBS.COM', 'BBBY.CA', 'BBBYTBS.CA', p_creationProg) INTO v_creationProg FROM DUAL;
        SELECT DECODE(UPPER(TRIM(p_LastMaintProg)), 'BBBY.COM', 'BBBYTBS', 'BAB.COM', 'BABTBS.COM', 'BBBY.CA', 'BBBYTBS.CA', p_LastMaintProg) INTO v_LastMaintProg FROM DUAL;
    END IF;

    v_regNum    := GET_NEXT_REGNUM;-- GET NEXT AVAILABLE REGISTRY NUMBER.

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;
    v_JDADate := TO_NUMBER( SUBSTR( TO_CHAR( v_JDADateNow ), 1, 8 ) ); -- ONLY NEED DATE PORTION OF DATETIME.

    -- SET ANNC_CARDS_REQ TO DEFAULT COUNT 25 FOR "BRD" AND "COM "EVENT TYPES.

    IF p_EventType = 'BRD' OR p_EventType = 'COM' THEN
        v_annc_cards_cnt := 25;
    END IF;

    BEGIN
        INSERT INTO reg_headers
        ( REGISTRY_NUM, EVENT_TYPE, EVENT_DT, EMAIL_FLAG,
            REFERRAL_CD, RECENT_PURCH_ACTIVITY,
            ACTION_CD, STATUS_CD, SERIES_CD, ANNC_CARDS_REQ, ERROR_CODE, ORIGIN_FLAG,
            LAST_MAINT_USER, STORE_NUM, REGISTRY_DT, REGISTRY_CONSULTANT1, PROCESS_FLAG, ON_LINE_REG_FLAG, CREATION_PROG, CREATION_USER,
            ROW_XNG_DT, CREATE_DT_TM, LAST_MAINT_DT_TM, STORE_NUM_GR, ROW_XNG_USR, ROW_STATUS,
            SHOWER_DATE, OTHER_DATE, NETWORK_AFFILIATE_FLAG, NUMBER_OF_GUEST, LAST_TRANS_STORE, LAST_MAINT_PROG, 
        IS_PUBLIC, CREATE_COUNTRY
        )
        VALUES
        ( v_regNum, p_EventType, p_EventDate, p_PromoEmailFlag,
            NULL, NULL,
            'A', 'A', 'N', v_annc_cards_cnt, NULL, '2',
            p_RowXngUser, v_StoreNum, v_JDADate, v_RegistryConsultant, 'W', 'Y', v_creationProg, p_RowXngUser,
            GET_GMT_DATE2(), v_JDADateNow, v_JDADateNow, v_StoreNumGr, p_RowXngUser, 'A',
            TO_DATE(TO_CHAR(p_showerDate),'YYYYMMDD'), TO_DATE(TO_CHAR(p_otherDate),'YYYYMMDD'), p_NetworkAffFlag, p_numGuests, v_LastTransStore, v_LastMaintProg,
            p_IsPublic, DECODE(p_SiteFlag, '1', 'USA', '2', 'USA', '3', 'CAN', '5', 'MEX')
        );

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

    p_RegNum        := v_regNum;
    p_JDADateNow    := v_JDADateNow;

END INSERT_REG_HEADER2;

PROCEDURE INSERT_REG_NAMES( p_RegNum IN NUMBER, p_AddrSubType IN VARCHAR, p_LastName IN VARCHAR, p_FirstName IN VARCHAR,
            p_Company IN VARCHAR, p_Addr1 IN VARCHAR, p_Addr2 IN VARCHAR, p_City IN VARCHAR, p_StateCD IN VARCHAR, p_Zip IN VARCHAR,
            p_DayPhone IN VARCHAR, p_EvePhone IN VARCHAR, p_Email IN VARCHAR, p_AsOfDate IN NUMBER,
            p_JDADateNow NUMBER, p_creationProg IN VARCHAR,
            p_DayPhoneExt IN VARCHAR, p_EvePhoneExt IN VARCHAR,
            p_PrefContMeth IN VARCHAR, p_PrefContTime IN VARCHAR,
            p_EmailFlag IN VARCHAR,
            p_Maiden IN VARCHAR,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2,
            p_ProfileID IN VARCHAR2, p_AffiliateOptIn IN VARCHAR2, p_Country IN VARCHAR2, p_RowXngUser in VARCHAR2)
IS
    v_addrNum           NUMBER        := 0;
    v_addrType          VARCHAR2(2)   := NULL;
    v_JDADateNow        NUMBER        := 0;
BEGIN

    IF p_AddrSubType = 'RE' THEN
        v_addrNum := 1;
        v_addrType := 'NA';
    ELSIF p_AddrSubType = 'CO' THEN
        v_addrNum := 2;
        v_addrType := 'NA';
    ELSIF p_AddrSubType = 'SH' THEN
        v_addrNum := 3;
        v_addrType := 'AD';
    ELSIF p_AddrSubType = 'FU' THEN
        v_addrNum := 4;
        v_addrType := 'AD';
    END IF;

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;

    INSERT INTO REG_NAMES
            ( REGISTRY_NUM, NM_ADDR_NUM, NM_ADDR_TYPE, NM_ADDR_SUB_TYPE, LAST_NM, FIRST_NM, COMPANY,
                ADDR1, ADDR2, CITY, STATE_CD, ZIP_CD, DAY_PHONE, EVE_PHONE,
                EMAIL_ADDR, ACTION_CD, STORE_NUM, AS_OF_DT, LAST_MAINT_USR, PROCESS_FLAG, CREATE_PROG, CREATE_USER,
                ROW_XNG_DT, CREATE_DT_TM, LAST_MAINT_DT_TM, ROW_XNG_USR, ROW_STATUS,
                DAY_PHONE_EXT, EVE_PHONE_EXT,
                LAST_NM_COPY, FIRST_NM_COPY,
                MAIDEN, MAIDEN_COPY,
                LAST_TRANS_STORE, LAST_MAINT_PROG,
                ATG_PROFILE_ID, RECEIVE_WEDDINGCHANNEL_EMAIL, CNTRY
            )
            VALUES
            ( p_RegNum, v_addrNum, v_addrType, UPPER(p_AddrSubType), UPPER(p_LastName), UPPER(p_FirstName), p_Company,
                UPPER(p_Addr1), UPPER(p_Addr2), UPPER(p_City), p_StateCD, p_Zip, p_DayPhone, p_EvePhone,
                TRIM(p_Email), 'A', NULL, p_AsOfDate, p_RowXngUser, 'W', p_creationProg, p_RowXngUser,
                GET_GMT_DATE2(), v_JDADateNow, v_JDADateNow, p_RowXngUser, 'A',
                p_DayPhoneExt, p_EvePhoneExt,
                p_LastName, p_FirstName,
                UPPER(p_Maiden), p_Maiden,
                p_LastTransStore, p_LastMaintProg,
                p_ProfileID, p_AffiliateOptIn, p_Country
            );

    EXCEPTION
        WHEN OTHERS THEN
            RAISE;

END INSERT_REG_NAMES;

PROCEDURE INSERT_REG_BABY( p_RegNum IN NUMBER, p_FirstName IN VARCHAR2, p_Gender IN VARCHAR2, p_Decor IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2)
IS
    v_JDADateNow    NUMBER        := 0;
BEGIN

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;

    BEGIN
        INSERT INTO REG_BABY
        ( REGISTRY_NUM, FIRST_NAME, FIRST_NAME_COPY, GENDER, DECOR, SEQ, ACTION_CD, LAST_TRANS_STORE, LAST_MAINT_PROG,
            CREATE_DT_TM, CREATE_PROG, CREATE_USER, LAST_MAINT_DT_TM, LAST_MAINT_USR, ROW_XNG_DT, ROW_XNG_USR )
        VALUES
        ( p_RegNum, UPPER(p_FirstName), p_FirstName, p_Gender, p_Decor, 1, 'A', p_LastTransStore, p_LastMaintProg,
          v_JDADateNow, p_LastMaintProg, p_RowXngUser, v_JDADateNow, p_RowXngUser, GET_GMT_DATE2(), p_RowXngUser);

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

END INSERT_REG_BABY;

PROCEDURE UPDATE_REG_BABY( p_RegNum IN NUMBER, p_FirstName IN VARCHAR2, p_Gender IN VARCHAR2, p_Decor IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2)
IS
    v_JDADateNow    NUMBER        := 0;
BEGIN

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;

    BEGIN
        UPDATE REG_BABY
            SET
            FIRST_NAME = UPPER(p_FirstName),
            FIRST_NAME_COPY = p_FirstName,
            GENDER = p_Gender,
            DECOR = p_Decor,
            LAST_TRANS_STORE = p_LastTransStore,
            LAST_MAINT_PROG = p_LastMaintProg,
            LAST_MAINT_DT_TM = v_JDADateNow,
            LAST_MAINT_USR = p_RowXngUser,
            ACTION_CD = 'C',
            ROW_XNG_DT = GET_GMT_DATE2(),
            ROW_XNG_USR = p_RowXngUser
            WHERE
            REGISTRY_NUM = p_RegNum AND SEQ = 1;

            IF SQL%ROWCOUNT = 0 THEN

                INSERT INTO REG_BABY
                ( REGISTRY_NUM, FIRST_NAME, FIRST_NAME_COPY, GENDER, DECOR, SEQ, ACTION_CD, LAST_TRANS_STORE, LAST_MAINT_PROG,
                    CREATE_DT_TM, CREATE_PROG, CREATE_USER, LAST_MAINT_DT_TM, LAST_MAINT_USR, ROW_XNG_DT, ROW_XNG_USR )
                VALUES
                ( p_RegNum, UPPER(p_FirstName), p_FirstName, p_Gender, p_Decor, 1, 'A', p_LastTransStore, p_LastMaintProg,
                  v_JDADateNow, p_LastMaintProg, p_RowXngUser, v_JDADateNow, p_RowXngUser, GET_GMT_DATE2(), p_RowXngUser);

            END IF;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

END UPDATE_REG_BABY;

PROCEDURE INSERT_REG_PREF_STORE( p_RegNum IN NUMBER, p_StoreNum IN NUMBER, p_ContactFlag IN VARCHAR2)
IS


BEGIN

    BEGIN
        INSERT INTO REG_PREF_STORE
        ( STORE_NUM, REGISTRY_NUM, CONTACT_FLAG, ROW_XNG_DT, ROW_XNG_USR )
        VALUES
        ( p_StoreNum, p_RegNum, p_ContactFlag, SYSDATE, USER );

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

END INSERT_REG_PREF_STORE;

PROCEDURE UPDATE_REG_PREF_STORE( p_RegNum IN NUMBER, p_StoreNum IN NUMBER, p_ContactFlag IN VARCHAR2)
IS


BEGIN

    IF p_StoreNum = -1 THEN
        UPDATE REG_PREF_STORE
            SET ROW_STATUS = 'I'
            WHERE
            REGISTRY_NUM = p_RegNum;
    ELSE
        BEGIN
            UPDATE REG_PREF_STORE
                    SET
                    STORE_NUM = p_StoreNum,
                    CONTACT_FLAG = p_ContactFlag,
                    ROW_STATUS = 'A',
                    ROW_XNG_DT = SYSDATE,
                    ROW_XNG_USR = USER
                    WHERE
                    REGISTRY_NUM = p_RegNum;

            IF SQL%ROWCOUNT = 0 THEN

                INSERT INTO REG_PREF_STORE
                ( STORE_NUM, REGISTRY_NUM, CONTACT_FLAG, ROW_XNG_DT, ROW_XNG_USR )
                VALUES
                ( p_StoreNum, p_RegNum, p_ContactFlag, SYSDATE, USER );

            END IF;

        END;
    END IF;

END UPDATE_REG_PREF_STORE;

PROCEDURE UPDATE_REG_PREF_STORE2( p_RegNum IN NUMBER, p_StoreNum IN NUMBER, p_ContactFlag IN VARCHAR2)
IS


BEGIN

    BEGIN
        INSERT INTO REG_PREF_STORE
        ( STORE_NUM, REGISTRY_NUM, CONTACT_FLAG, ROW_XNG_DT, ROW_XNG_USR )
        SELECT p_StoreNum, p_RegNum, p_ContactFlag, SYSDATE, USER 
    FROM DUAL 
    WHERE NOT EXISTS (SELECT 1 FROM REG_PREF_STORE WHERE REGISTRY_NUM = p_RegNum);

    IF SQL%ROWCOUNT = 0 THEN

            UPDATE REG_PREF_STORE
                    SET
                    CONTACT_FLAG = p_ContactFlag,
                    ROW_STATUS = 'A',
                    ROW_XNG_DT = SYSDATE,
                    ROW_XNG_USR = USER
                    WHERE
                    REGISTRY_NUM = p_RegNum;
    END IF;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

END UPDATE_REG_PREF_STORE2;

PROCEDURE UPDATE_REG_HEADER( p_RegNum IN NUMBER,
            p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER,
            p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2,
            p_JDADateNow OUT NUMBER )
IS

    v_JDADate        NUMBER        := 0; -- convert to string to truncate time portion and convert back to number.
    v_JDADateNow    NUMBER        := 0; -- call function to get this.

    reg_count            NUMBER    := 0;
    registry_not_found    EXCEPTION;

BEGIN

    SELECT COUNT(*) INTO reg_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;
    v_JDADate := TO_NUMBER( SUBSTR( TO_CHAR( v_JDADateNow ), 1, 8 ) ); -- ONLY NEED DATE PORTION OF DATETIME.

    BEGIN
    UPDATE REG_HEADERS
            SET
            EVENT_DT = p_EventDate,
            EMAIL_FLAG = p_PromoEmailFlag,
            LAST_MAINT_DT_TM = v_JDADateNow,
            ROW_XNG_USR = p_RowXngUser,
            LAST_MAINT_USER = p_RowXngUser,
            ROW_XNG_DT = GET_GMT_DATE2(),
            SHOWER_DATE = TO_DATE(TO_CHAR(p_showerDate),'YYYYMMDD'),
            OTHER_DATE = TO_DATE(TO_CHAR(p_otherDate),'YYYYMMDD'),
            NUMBER_OF_GUEST = p_numGuests,
            LAST_TRANS_STORE = p_LastTransStore,
            LAST_MAINT_PROG = p_LastMaintProg,
            ACTION_CD = 'C',
            PROCESS_FLAG = 'W'
            WHERE
            REGISTRY_NUM = p_RegNum
            AND
            (
                EVENT_DT <> p_EventDate OR
                TRIM(EMAIL_FLAG) <> UPPER(p_PromoEmailFlag) OR
                NVL(SHOWER_DATE,TO_DATE('19000101','YYYYMMDD')) <> TO_DATE(TO_CHAR(NVL(p_showerDate,'19000101')), 'YYYYMMDD') OR
                NVL(OTHER_DATE,TO_DATE('19000101','YYYYMMDD')) <> TO_DATE(TO_CHAR(NVL(p_otherDate,'19000101')), 'YYYYMMDD') OR
                NUMBER_OF_GUEST <> p_numGuests
            );

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

    p_JDADateNow    := v_JDADateNow;

    EXCEPTION
            WHEN registry_not_found THEN
                RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
            WHEN OTHERS THEN
                RAISE;



END UPDATE_REG_HEADER;

PROCEDURE UPDATE_REG_HEADER2( p_RegNum IN NUMBER,
            p_EventDate IN NUMBER, p_PromoEmailFlag IN VARCHAR,
            p_Password IN VARCHAR, p_PasswordHint IN VARCHAR,
            p_StoreNum IN NUMBER, p_StoreNumGr IN NUMBER,
            p_GuestPassword IN VARCHAR,
            p_creationProg IN VARCHAR,
            p_showerDate IN VARCHAR, p_otherDate IN VARCHAR, p_NetworkAffFlag IN VARCHAR, p_numGuests IN NUMBER, p_IsPublic IN VARCHAR, p_SiteFlag IN VARCHAR,
            p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2,
            p_JDADateNow OUT NUMBER )
IS

    v_JDADate        NUMBER        := 0; -- convert to string to truncate time portion and convert back to number.
    v_JDADateNow    NUMBER        := 0; -- call function to get this.

    reg_count            NUMBER    := 0;
    registry_not_found    EXCEPTION;

BEGIN

    SELECT COUNT(*) INTO reg_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;
    v_JDADate := TO_NUMBER( SUBSTR( TO_CHAR( v_JDADateNow ), 1, 8 ) ); -- ONLY NEED DATE PORTION OF DATETIME.

    BEGIN
    UPDATE REG_HEADERS
            SET
            EVENT_DT = p_EventDate,
            EMAIL_FLAG = p_PromoEmailFlag,
            LAST_MAINT_DT_TM = v_JDADateNow,
            ROW_XNG_USR = p_RowXngUser,
            LAST_MAINT_USER = p_RowXngUser,
            ROW_XNG_DT = GET_GMT_DATE2(),
            SHOWER_DATE = TO_DATE(TO_CHAR(p_showerDate),'YYYYMMDD'),
            OTHER_DATE = TO_DATE(TO_CHAR(p_otherDate),'YYYYMMDD'),
            NUMBER_OF_GUEST = p_numGuests,
        NETWORK_AFFILIATE_FLAG = p_NetworkAffFlag,
            IS_PUBLIC = p_IsPublic,
            LAST_TRANS_STORE = p_LastTransStore,
            LAST_MAINT_PROG = p_LastMaintProg,
            ACTION_CD = 'C',
            PROCESS_FLAG = 'W'
            WHERE
            REGISTRY_NUM = p_RegNum
            AND
            (
                NVL(EVENT_DT, 0) <> NVL(p_EventDate, 0) OR
                NVL(TRIM(EMAIL_FLAG), ' ') <> UPPER(NVL(p_PromoEmailFlag, ' ')) OR
                NVL(SHOWER_DATE,TO_DATE('19000101','YYYYMMDD')) <> TO_DATE(TO_CHAR(NVL(p_showerDate,'19000101')), 'YYYYMMDD') OR
                NVL(OTHER_DATE,TO_DATE('19000101','YYYYMMDD')) <> TO_DATE(TO_CHAR(NVL(p_otherDate,'19000101')), 'YYYYMMDD') OR
                NVL(NUMBER_OF_GUEST, 0) <> NVL(p_numGuests, 0) OR
        NVL(NETWORK_AFFILIATE_FLAG, ' ') <> NVL(p_NetworkAffFlag, ' ') OR
        NVL(IS_PUBLIC, ' ') <> NVL(p_IsPublic, ' ')
            );

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END;

    p_JDADateNow    := v_JDADateNow;

    EXCEPTION
            WHEN registry_not_found THEN
                RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
            WHEN OTHERS THEN
                RAISE;



END UPDATE_REG_HEADER2;

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
                        p_coRegProfileId IN VARCHAR, p_AffiliateOptIn IN VARCHAR2, p_Country IN VARCHAR2, p_RowXngUser in VARCHAR2)
IS
    v_addrNum                       NUMBER                  := 0;
    v_addrType                      VARCHAR2(2)             := NULL;
    reg_count                       NUMBER                  := 0;
    co_count                        NUMBER                  := 0;
    registry_not_found              EXCEPTION;
    v_eventType                     VARCHAR2(3)             := NULL;
    v_no_maiden_reg_found           NUMBER                  := 0;
    v_JDADateNow                    NUMBER                  := 0;
BEGIN

    SELECT COUNT(*) INTO reg_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;

    IF reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    IF p_AddrSubType = 'RE' THEN
        v_addrNum := 1;
        v_addrType := 'NA';
    ELSIF p_AddrSubType = 'CO' THEN
        v_addrNum := 2;
        v_addrType := 'NA';
    ELSIF p_AddrSubType = 'SH' THEN
        v_addrNum := 3;
        v_addrType := 'AD';
    ELSIF p_AddrSubType = 'FU' THEN
        v_addrNum := 4;
        v_addrType := 'AD';
    END IF;

    SELECT event_type INTO v_eventType FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    SELECT INSTR('BR1,OT1', UPPER(TRIM(v_eventType))) into v_no_maiden_reg_found from dual;

    BEGIN

        BEGIN
            IF p_AddrSubType = 'RE' OR p_AddrSubType = 'CO' THEN

                IF v_no_maiden_reg_found > 0 THEN
                    INSERT INTO REG_NAMES
                        ( REGISTRY_NUM, NM_ADDR_NUM, NM_ADDR_TYPE, NM_ADDR_SUB_TYPE, LAST_NM, FIRST_NM, COMPANY,
                            ADDR1, ADDR2, CITY, STATE_CD, ZIP_CD, DAY_PHONE, EVE_PHONE,
                            EMAIL_ADDR, ACTION_CD, STORE_NUM, AS_OF_DT, LAST_MAINT_USR, PROCESS_FLAG, CREATE_PROG, CREATE_USER,
                            ROW_XNG_DT, CREATE_DT_TM, LAST_MAINT_DT_TM, ROW_XNG_USR, ROW_STATUS,
                            LAST_NM_COPY, FIRST_NM_COPY,
                            LAST_TRANS_STORE, LAST_MAINT_PROG,
                            CNTRY
                        )
                        VALUES
                        ( p_RegNum, v_addrNum, v_addrType, UPPER(p_AddrSubType), UPPER(p_LastName), UPPER(p_FirstName), p_Company,
                            UPPER(p_Addr1), UPPER(p_Addr2), UPPER(p_City), p_StateCD, p_Zip, p_DayPhone, p_EvePhone,
                            TRIM(p_Email), 'A', NULL, p_AsOfDate, p_RowXngUser, 'W', p_creationProg, p_RowXngUser,
                            GET_GMT_DATE2(), v_JDADateNow, v_JDADateNow, p_RowXngUser, 'A',
                            p_LastName, p_FirstName,
                            p_LastTransStore, p_LastMaintProg,
                            p_Country
                        );
                ELSE
                    INSERT INTO REG_NAMES
                    ( REGISTRY_NUM, NM_ADDR_NUM, NM_ADDR_TYPE, NM_ADDR_SUB_TYPE, LAST_NM, FIRST_NM, COMPANY,
                        ADDR1, ADDR2, CITY, STATE_CD, ZIP_CD, DAY_PHONE, EVE_PHONE,
                        EMAIL_ADDR, ACTION_CD, STORE_NUM, AS_OF_DT, LAST_MAINT_USR, PROCESS_FLAG, CREATE_PROG, CREATE_USER,
                        ROW_XNG_DT, CREATE_DT_TM, LAST_MAINT_DT_TM, ROW_XNG_USR, ROW_STATUS,
                        LAST_NM_COPY, FIRST_NM_COPY,
                        MAIDEN, MAIDEN_COPY,
                        LAST_TRANS_STORE, LAST_MAINT_PROG,
                        CNTRY
                    )
                    VALUES
                    ( p_RegNum, v_addrNum, v_addrType, UPPER(p_AddrSubType), UPPER(p_LastName), UPPER(p_FirstName), p_Company,
                        UPPER(p_Addr1), UPPER(p_Addr2), UPPER(p_City), p_StateCD, p_Zip, p_DayPhone, p_EvePhone,
                        TRIM(p_Email), 'A', NULL, p_AsOfDate, p_RowXngUser, 'W', p_creationProg, p_RowXngUser,
                        GET_GMT_DATE2(), v_JDADateNow, v_JDADateNow, p_RowXngUser, 'A',
                        p_LastName, p_FirstName,
                        UPPER(p_Maiden), p_Maiden,
                        p_LastTransStore, p_LastMaintProg,
                        p_Country
                    );
                END IF;

            ELSE

                INSERT INTO REG_NAMES
                    ( REGISTRY_NUM, NM_ADDR_NUM, NM_ADDR_TYPE, NM_ADDR_SUB_TYPE, LAST_NM, FIRST_NM, COMPANY,
                        ADDR1, ADDR2, CITY, STATE_CD, ZIP_CD,
                        ACTION_CD, STORE_NUM, AS_OF_DT, LAST_MAINT_USR, PROCESS_FLAG, CREATE_PROG, CREATE_USER,
                        ROW_XNG_DT, CREATE_DT_TM, LAST_MAINT_DT_TM, ROW_XNG_USR, ROW_STATUS,
                        LAST_NM_COPY, FIRST_NM_COPY,
                        MAIDEN, MAIDEN_COPY,
                        LAST_TRANS_STORE, LAST_MAINT_PROG,
                        CNTRY
                    )
                    VALUES
                    ( p_RegNum, v_addrNum, v_addrType, UPPER(p_AddrSubType), UPPER(p_LastName), UPPER(p_FirstName), p_Company,
                        UPPER(p_Addr1), UPPER(p_Addr2), UPPER(p_City), p_StateCD, p_Zip,
                        'A', NULL, p_AsOfDate, p_RowXngUser, 'W', p_creationProg, p_RowXngUser,
                        GET_GMT_DATE2(), v_JDADateNow, v_JDADateNow, p_RowXngUser, 'A',
                        p_LastName, p_FirstName,
                        UPPER(p_Maiden), p_Maiden,
                        p_LastTransStore, p_LastMaintProg,
                        p_Country
                    );
            END IF;

            EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN

                IF p_AddrSubType = 'RE' OR p_AddrSubType = 'CO' THEN
                    IF v_no_maiden_reg_found > 0 THEN
                        UPDATE REG_NAMES
                                SET
                                LAST_NM = UPPER(p_LastName),
                                FIRST_NM = UPPER(p_FirstName),
                                LAST_NM_COPY = p_LastName,
                                FIRST_NM_COPY = p_FirstName,
                                COMPANY = p_Company,
                                ADDR1 = UPPER(p_Addr1),
                                ADDR2 = UPPER(p_Addr2),
                                CITY = UPPER(p_City),
                                STATE_CD = p_StateCD,
                                ZIP_CD = p_Zip,
                                DAY_PHONE = p_DayPhone,
                                EVE_PHONE = p_EvePhone,
                                EMAIL_ADDR = TRIM(p_Email),
                                AS_OF_DT = p_AsOfDate,
                                LAST_MAINT_DT_TM = v_JDADateNow,
                                LAST_TRANS_STORE = p_LastTransStore,
                                LAST_MAINT_PROG = p_LastMaintProg,
                                LAST_MAINT_USR = p_RowXngUser,
                                ACTION_CD = 'C',
                                PROCESS_FLAG = 'W',
                                ROW_XNG_DT = GET_GMT_DATE2(),
                                ROW_XNG_USR = p_RowXngUser,
                                CNTRY = p_Country
                                WHERE
                                REGISTRY_NUM = p_RegNum AND
                                NM_ADDR_NUM = v_addrNum AND
                                NM_ADDR_TYPE = v_addrType AND
                                NM_ADDR_SUB_TYPE = UPPER(p_AddrSubType) AND
                                (
                                    TRIM(NVL(LAST_NM,'-')) <> UPPER(NVL(p_LastName,'-')) OR
                                    TRIM(NVL(FIRST_NM,'-')) <> UPPER(NVL(p_FirstName,'-'))OR
                                    TRIM(NVL(COMPANY,'-')) <> NVL(p_Company,'-') OR
                                    TRIM(NVL(ADDR1,'-')) <> UPPER(NVL(p_Addr1,'-')) OR
                                    TRIM(NVL(ADDR2,'-')) <> UPPER(NVL(p_Addr2,'-')) OR
                                    TRIM(NVL(CITY,'-')) <> UPPER(NVL(p_City,'-')) OR
                                    TRIM(NVL(STATE_CD,'-')) <> UPPER(NVL(p_StateCD,'-')) OR
                                    TRIM(NVL(ZIP_CD,'-')) <> NVL(p_Zip,'-') OR
                                    TRIM(NVL(DAY_PHONE,'-')) <> NVL(p_DayPhone,'-') OR
                                    TRIM(NVL(EVE_PHONE,'-')) <> NVL(p_EvePhone,'-') OR
                                    TRIM(NVL(EMAIL_ADDR,'-')) <> UPPER(NVL(TRIM(p_Email),'-')) OR
                                    NVL(AS_OF_DT,0) <> NVL(p_AsOfDate,0)
                                );
                    ELSE
                        UPDATE REG_NAMES
                                SET
                                LAST_NM = UPPER(p_LastName),
                                FIRST_NM = UPPER(p_FirstName),
                                LAST_NM_COPY = p_LastName,
                                FIRST_NM_COPY = p_FirstName,
                                COMPANY = p_Company,
                                ADDR1 = UPPER(p_Addr1),
                                ADDR2 = UPPER(p_Addr2),
                                CITY = UPPER(p_City),
                                STATE_CD = p_StateCD,
                                ZIP_CD = p_Zip,
                                DAY_PHONE = p_DayPhone,
                                EVE_PHONE = p_EvePhone,
                                EMAIL_ADDR = TRIM(p_Email),
                                AS_OF_DT = p_AsOfDate,
                                LAST_MAINT_DT_TM = v_JDADateNow,
                                MAIDEN = UPPER(p_Maiden),
                                MAIDEN_COPY = p_Maiden,
                                LAST_TRANS_STORE = p_LastTransStore,
                                LAST_MAINT_PROG = p_LastMaintProg,
                                LAST_MAINT_USR = p_RowXngUser,
                                ACTION_CD = 'C',
                                PROCESS_FLAG = 'W',
                                ROW_XNG_DT = GET_GMT_DATE2(),
                                ROW_XNG_USR = p_RowXngUser,
                                CNTRY = p_Country
                                WHERE
                                REGISTRY_NUM = p_RegNum AND
                                NM_ADDR_NUM = v_addrNum AND
                                NM_ADDR_TYPE = v_addrType AND
                                NM_ADDR_SUB_TYPE = UPPER(p_AddrSubType) AND
                                (
                                    TRIM(NVL(LAST_NM,'-')) <> UPPER(NVL(p_LastName,'-')) OR
                                    TRIM(NVL(FIRST_NM,'-')) <> UPPER(NVL(p_FirstName,'-'))OR
                                    TRIM(NVL(COMPANY,'-')) <> NVL(p_Company,'-') OR
                                    TRIM(NVL(ADDR1,'-')) <> UPPER(NVL(p_Addr1,'-')) OR
                                    TRIM(NVL(ADDR2,'-')) <> UPPER(NVL(p_Addr2,'-')) OR
                                    TRIM(NVL(CITY,'-')) <> UPPER(NVL(p_City,'-')) OR
                                    TRIM(NVL(STATE_CD,'-')) <> UPPER(NVL(p_StateCD,'-')) OR
                                    TRIM(NVL(ZIP_CD,'-')) <> NVL(p_Zip,'-') OR
                                    TRIM(NVL(DAY_PHONE,'-')) <> NVL(p_DayPhone,'-') OR
                                    TRIM(NVL(EVE_PHONE,'-')) <> NVL(p_EvePhone,'-') OR
                                    TRIM(NVL(EMAIL_ADDR,'-')) <> UPPER(NVL(TRIM(p_Email),'-')) OR
                                    NVL(AS_OF_DT,0) <> NVL(p_AsOfDate,0) OR
                                    TRIM(NVL(MAIDEN,'-')) <> UPPER(NVL(p_Maiden,'-'))
                                );
                    END IF;
                ELSE
                    UPDATE REG_NAMES
                            SET
                            LAST_NM = UPPER(p_LastName),
                            FIRST_NM = UPPER(p_FirstName),
                            LAST_NM_COPY = p_LastName,
                            FIRST_NM_COPY = p_FirstName,
                            COMPANY = p_Company,
                            ADDR1 = UPPER(p_Addr1),
                            ADDR2 = UPPER(p_Addr2),
                            CITY = UPPER(p_City),
                            STATE_CD = p_StateCD,
                            ZIP_CD = p_Zip,
                            DAY_PHONE = p_DayPhone,
                            EVE_PHONE = p_EvePhone,
                            EMAIL_ADDR = TRIM(p_Email),
                            AS_OF_DT = p_AsOfDate,
                            LAST_MAINT_DT_TM = v_JDADateNow,
                            MAIDEN = UPPER(p_Maiden),
                            MAIDEN_COPY = p_Maiden,
                            LAST_TRANS_STORE = p_LastTransStore,
                            LAST_MAINT_PROG = p_LastMaintProg,
                            LAST_MAINT_USR = p_RowXngUser,
                            ACTION_CD = 'C',
                            PROCESS_FLAG = 'W',
                            ROW_XNG_DT = GET_GMT_DATE2(),
                            ROW_XNG_USR = p_RowXngUser,
                            CNTRY = p_Country
                            WHERE
                            REGISTRY_NUM = p_RegNum AND
                            NM_ADDR_NUM = v_addrNum AND
                            NM_ADDR_TYPE = v_addrType AND
                            NM_ADDR_SUB_TYPE = UPPER(p_AddrSubType) AND
                            (
                                TRIM(NVL(LAST_NM,'-')) <> UPPER(NVL(p_LastName,'-')) OR
                                TRIM(NVL(FIRST_NM,'-')) <> UPPER(NVL(p_FirstName,'-'))OR
                                TRIM(NVL(COMPANY,'-')) <> NVL(p_Company,'-') OR
                                TRIM(NVL(ADDR1,'-')) <> UPPER(NVL(p_Addr1,'-')) OR
                                TRIM(NVL(ADDR2,'-')) <> UPPER(NVL(p_Addr2,'-')) OR
                                TRIM(NVL(CITY,'-')) <> UPPER(NVL(p_City,'-')) OR
                                TRIM(NVL(STATE_CD,'-')) <> UPPER(NVL(p_StateCD,'-')) OR
                                TRIM(NVL(ZIP_CD,'-')) <> NVL(p_Zip,'-') OR
                                TRIM(NVL(DAY_PHONE,'-')) <> NVL(p_DayPhone,'-') OR
                                TRIM(NVL(EVE_PHONE,'-')) <> NVL(p_EvePhone,'-') OR
                                TRIM(NVL(EMAIL_ADDR,'-')) <> UPPER(NVL(TRIM(p_Email),'-')) OR
                                NVL(AS_OF_DT,0) <> NVL(p_AsOfDate,0) OR
                                TRIM(NVL(MAIDEN,'-')) <> UPPER(NVL(p_Maiden,'-'))
                            );
                END IF;

            WHEN OTHERS THEN
                RAISE;
        END;

        IF p_AddrSubType = 'CO' THEN
            SELECT COUNT(*) INTO co_count FROM REG_NAMES WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'CO';

            IF co_count > 0 THEN

                UPDATE REG_NAMES
                    SET ATG_PROFILE_ID = p_coRegProfileId,
                    ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_DT = GET_GMT_DATE2(),
                    LAST_TRANS_STORE = p_LastTransStore,
                    LAST_MAINT_PROG = p_LastMaintProg,
                    ROW_XNG_USR = p_RowXngUser,
                    LAST_MAINT_USR = p_RowXngUser,
                    LAST_MAINT_DT_TM = v_JDADateNow
                    WHERE REGISTRY_NUM = p_RegNum
                    AND NM_ADDR_SUB_TYPE = 'CO'
                    AND NVL(ATG_PROFILE_ID,'-') <> p_coRegProfileId;

            END IF;

        END IF;

    END;

    EXCEPTION
        WHEN registry_not_found THEN
                RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
        WHEN OTHERS THEN
            RAISE;

END UPDATE_REG_NAMES;

PROCEDURE GET_REG_INFO( p_RegNum IN NUMBER, p_nGiftsRegistered OUT NUMBER, p_nGiftsPurchased OUT NUMBER,
cur_Info OUT T_CURSOR, cur_Header OUT T_CURSOR, cur_Baby OUT T_CURSOR, cur_PrefStore OUT T_CURSOR)
IS

    v_nGiftsRegistered     NUMBER     := 0;
    v_nGiftsPurchased    NUMBER     := 0;
    v_Cursor T_CURSOR;
    v_Cursor2 T_CURSOR;
    v_Cursor3 T_CURSOR;
    v_Cursor4 T_CURSOR;

    v_site_flag          NUMBER    := 0;
    reg_count            NUMBER    := 0;
    registry_not_found    EXCEPTION;

    v_TimeToExpire NUMBER := 60/1440;

BEGIN

    SELECT COUNT(*) INTO reg_count FROM REG_HEADERS RH
    WHERE RH.REGISTRY_NUM = p_RegNum
    AND RH.ACTION_CD <> 'D'
    AND RH.STATUS_CD NOT IN  ('I','C','H');
    IF reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    BEGIN
        --FAKE OUT SESSION SO THAT BRIDAL TOOLKIT ON ATG WORKS
        INSERT INTO REG_SESSION VALUES ( p_RegNum, SYSDATE + v_TimeToExpire );
        COMMIT;

    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            UPDATE REG_SESSION SET EXPIRATION_DT = SYSDATE + v_TimeToExpire    WHERE REGISTRY_NUM = p_RegNum;
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR( -20501, 'Could create an entry REG_SESSION::' || p_RegNum );
    END;

    --GET GIFTS REGISTERED COUNT
    SELECT NVL(SUM(qty_requested),0) INTO v_nGiftsRegistered
    FROM reg_details
    WHERE registry_num = p_RegNum
    AND action_cd <> 'D';

    p_nGiftsRegistered := v_nGiftsRegistered;

    --GET PURCHASED COUNT
    SELECT NVL(SUM(case when qty_fulfilled < 0 then 0
                        else qty_fulfilled end ),0) + NVL(SUM(QTY_PURCH_RESRV),0 )
    INTO v_nGiftsPurchased
    FROM REG_DETAILS
    WHERE REGISTRY_NUM = p_RegNum
    AND action_cd <> 'D';

    p_nGiftsPurchased := v_nGiftsPurchased;

    -- GET REGNAMES INFO
    OPEN v_Cursor FOR
    SELECT NM_ADDR_SUB_TYPE, NVL(LAST_NM_COPY, INITCAP(LAST_NM)) LAST_NM_COPY, NVL(FIRST_NM_COPY, INITCAP(FIRST_NM)) FIRST_NM_COPY, COMPANY, ADDR1, ADDR2, CITY, STATE_CD, ZIP_CD, DAY_PHONE, EVE_PHONE,
    EMAIL_ADDR, STORE_NUM, AS_OF_DT,
    DAY_PHONE_EXT, EVE_PHONE_EXT, EMAIL_FLAG, MAIDEN_COPY, PREF_CONTACT_METHOD, PREF_CONTACT_TIME, ATG_PROFILE_ID, RECEIVE_WEDDINGCHANNEL_EMAIL
    FROM REG_NAMES
    WHERE REGISTRY_NUM = p_RegNum AND ACTION_CD <> 'D';

    OPEN v_Cursor2 FOR
    SELECT EVENT_TYPE, EVENT_DT, EMAIL_FLAG, PASSWORD, PASSWORD_HINT, REFERRAL_CD, GUEST_PASSWORD, NUMBER_OF_GUEST, NETWORK_AFFILIATE_FLAG,
    TO_CHAR(OTHER_DATE,'YYYYMMDD') OTHER_DATE, TO_CHAR(SHOWER_DATE,'YYYYMMDD') SHOWER_DATE,
    (SELECT REGISTRY_NUM FROM BT_PLANNER WHERE REGISTRY_NUM = RH.REGISTRY_NUM AND ROWNUM = 1) BRIDAL_TOOLKIT
    FROM REG_HEADERS RH
    WHERE REGISTRY_NUM = p_RegNum;

    OPEN v_Cursor3 FOR
    SELECT FIRST_NAME_COPY, GENDER, DECOR
    FROM REG_BABY
    WHERE REGISTRY_NUM = p_RegNum AND SEQ = 1;

    OPEN v_Cursor4 FOR
    SELECT STORE_NUM, CONTACT_FLAG
    FROM REG_PREF_STORE
    WHERE REGISTRY_NUM = p_RegNum AND ROW_STATUS = 'A';

    cur_Info := v_Cursor;
    cur_Header :=  v_Cursor2;
    cur_Baby := v_Cursor3;
    cur_PrefStore := v_Cursor4;

    EXCEPTION
        WHEN registry_not_found THEN
                RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
        WHEN OTHERS THEN
            RAISE;

END GET_REG_INFO;

PROCEDURE GET_REG_INFO2( p_RegNum IN NUMBER, p_siteFlag IN VARCHAR, p_nGiftsRegistered OUT NUMBER, p_nGiftsPurchased OUT NUMBER,
cur_Info OUT T_CURSOR, cur_Header OUT T_CURSOR, cur_Baby OUT T_CURSOR, cur_PrefStore OUT T_CURSOR)
IS

    v_nGiftsRegistered      NUMBER          := 0;
    v_nGiftsPurchased       NUMBER          := 0;
    v_nGiftsRegistered2     NUMBER          := 0;
    v_nGiftsPurchased2      NUMBER          := 0;
    v_Cursor T_CURSOR;
    v_Cursor2 T_CURSOR;
    v_Cursor3 T_CURSOR;
    v_Cursor4 T_CURSOR;

    v_site_flag          NUMBER    := 0;
    reg_count            NUMBER    := 0;
    registry_not_found    EXCEPTION;

    v_TimeToExpire NUMBER := 60/1440;

BEGIN

    --Check if the Registry belongs to the same Country as the p_siteFlag.
    SELECT GET_IN_COUNTRY( p_RegNum, p_siteFlag, 2 ) INTO v_site_flag FROM DUAL;

    IF v_site_flag = 0 THEN
        RAISE registry_not_found;
    END IF;

    SELECT COUNT(*) INTO reg_count FROM REG_HEADERS RH
    WHERE RH.REGISTRY_NUM = p_RegNum
    AND RH.ACTION_CD <> 'D'
    AND RH.STATUS_CD NOT IN  ('I','C','H');
    IF reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

--  BEGIN
--      --FAKE OUT SESSION SO THAT BRIDAL TOOLKIT ON ATG WORKS
--      INSERT INTO REG_SESSION VALUES ( p_RegNum, SYSDATE + v_TimeToExpire );
--      COMMIT;

--  EXCEPTION
--      WHEN DUP_VAL_ON_INDEX THEN
--          UPDATE REG_SESSION SET EXPIRATION_DT = SYSDATE + v_TimeToExpire    WHERE REGISTRY_NUM = p_RegNum;
--      WHEN OTHERS THEN
--          RAISE_APPLICATION_ERROR( -20501, 'Could create an entry REG_SESSION::' || p_RegNum );
--  END;
    
    
    --GET GIFTS REGISTERED COUNT
    SELECT NVL(SUM(case when qty_requested = 0 then 1 else qty_requested end),0) INTO v_nGiftsRegistered
    FROM reg_details
    WHERE registry_num = p_RegNum
    AND action_cd <> 'D';
    
    --GET  PERSONALIZED GIFTS REGISTERED COUNT. (DRACO).
    SELECT NVL(SUM(case when qty_requested = 0 then 1 else qty_requested end),0) INTO v_nGiftsRegistered2
    FROM reg_details2
    WHERE registry_num = p_RegNum
    AND action_cd <> 'D';
    
    p_nGiftsRegistered := v_nGiftsRegistered + v_nGiftsRegistered2;
    
     --GET PURCHASED COUNT
    SELECT NVL(SUM(case when qty_fulfilled < 0 then 0
                        else qty_fulfilled end ),0) + NVL(SUM(QTY_PURCH_RESRV),0 )
    INTO v_nGiftsPurchased
    FROM REG_DETAILS
    WHERE REGISTRY_NUM = p_RegNum
    AND action_cd <> 'D';
    
    --GET  PERSONALIZED GIFTS PURCHASED COUNT. (DRACO).
    SELECT NVL(SUM(case when qty_fulfilled < 0 then 0
                        else qty_fulfilled end ),0) + NVL(SUM(QTY_PURCH_RESRV),0 )
    INTO v_nGiftsPurchased2
    FROM REG_DETAILS2
    WHERE REGISTRY_NUM = p_RegNum
    AND action_cd <> 'D';

    p_nGiftsPurchased := v_nGiftsPurchased + v_nGiftsPurchased2;
    
    
    
    --GET_GIFTS_REGISTERED function wll return the sum of items (regualr and personalized ) requested from reg_details and reg_details2 tables.
    --p_nGiftsRegistered := GET_GIFTS_REGISTERED(p_RegNum);
    
    --GET_GIFTS_PURCHASED function wll return the sum of items (regualr and personalized )  purchased from reg_details and reg_details2 tables.    
    --p_nGiftsPurchased := GET_GIFTS_PURCHASED(p_RegNum);

    -- GET REGNAMES INFO
    OPEN v_Cursor FOR
    --SELECT NM_ADDR_SUB_TYPE, NVL(LAST_NM_COPY, INITCAP(LAST_NM)) LAST_NM_COPY, NVL(FIRST_NM_COPY, INITCAP(FIRST_NM)) FIRST_NM_COPY, COMPANY, ADDR1, ADDR2, CITY, STATE_CD, ZIP_CD, DAY_PHONE, EVE_PHONE,
    SELECT NM_ADDR_SUB_TYPE, TRIM(INITCAP(NVL(LAST_NM_COPY, LAST_NM))|| ' ' ||INITCAP(LAST_NM2)) LAST_NM_COPY, INITCAP(NVL(FIRST_NM_COPY, FIRST_NM)) FIRST_NM_COPY, COMPANY, ADDR1, ADDR2, CITY, STATE_CD, ZIP_CD, DAY_PHONE, EVE_PHONE,    
    EMAIL_ADDR, STORE_NUM, AS_OF_DT,
    DAY_PHONE_EXT, EVE_PHONE_EXT, EMAIL_FLAG, MAIDEN_COPY, PREF_CONTACT_METHOD, PREF_CONTACT_TIME, ATG_PROFILE_ID, RECEIVE_WEDDINGCHANNEL_EMAIL
    FROM REG_NAMES
    WHERE REGISTRY_NUM = p_RegNum AND ACTION_CD <> 'D';

    OPEN v_Cursor2 FOR
    SELECT RH.EVENT_TYPE, RH.EVENT_DT, EMAIL_FLAG, PASSWORD, PASSWORD_HINT, REFERRAL_CD, GUEST_PASSWORD, NUMBER_OF_GUEST, NETWORK_AFFILIATE_FLAG,
    TO_CHAR(OTHER_DATE,'YYYYMMDD') OTHER_DATE, TO_CHAR(SHOWER_DATE,'YYYYMMDD') SHOWER_DATE,
    (SELECT REGISTRY_NUM FROM BT_PLANNER WHERE REGISTRY_NUM = RH.REGISTRY_NUM AND ROWNUM = 1) BRIDAL_TOOLKIT,
    IS_PUBLIC, CREATE_COUNTRY, REGISTRANT_BG, COREGISTRANT_BG
    FROM REG_HEADERS RH, BBB_CORE.BBB_REGISTRY
    WHERE REGISTRY_NUM = p_RegNum
      AND REGISTRY_NUM = REGISTRY_ID(+);

    OPEN v_Cursor3 FOR
    SELECT FIRST_NAME_COPY, GENDER, DECOR
    FROM REG_BABY
    WHERE REGISTRY_NUM = p_RegNum AND SEQ = 1;

    OPEN v_Cursor4 FOR
    SELECT STORE_NUM, CONTACT_FLAG
    FROM REG_PREF_STORE
    WHERE REGISTRY_NUM = p_RegNum AND ROW_STATUS = 'A';

    cur_Info := v_Cursor;
    cur_Header :=  v_Cursor2;
    cur_Baby := v_Cursor3;
    cur_PrefStore := v_Cursor4;

    EXCEPTION
        WHEN registry_not_found THEN
                RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
        WHEN OTHERS THEN
            RAISE;

END GET_REG_INFO2;


PROCEDURE GET_REG_INFO_BY_PROFILEID( p_siteFlag IN VARCHAR, p_ProfileId IN VARCHAR2, cur_Info OUT T_CURSOR)
IS

    v_Cursor T_CURSOR;

    reg_count            NUMBER    := 0;
    v_sProfileIdExists    VARCHAR2(1) := 'N';

BEGIN


    OPEN v_Cursor FOR
    SELECT ATG_PROFILE_ID, REGISTRY_NUM, NM_ADDR_SUB_TYPE, EVENT_TYPE, EVENT_DT,
        FIRST_NM_COPY, LAST_NM_COPY, MAIDEN_COPY,
        ADDR1, ADDR2, COMPANY, CITY, STATE_CD, ZIP_CD,
        EMAIL_ADDR EMAIL_ADDR, DAY_PHONE, EVE_PHONE, DAY_PHONE_EXT, EVE_PHONE_EXT,
        REGISTRANT_NAME, COREGISTRANT_NAME, GIFTSREGISTERED, GIFTSPURCHASED, RANK
    FROM
    (
        SELECT RN.ATG_PROFILE_ID, RN.REGISTRY_NUM, RN.NM_ADDR_SUB_TYPE, RH.EVENT_TYPE, RH.EVENT_DT,
            RN.FIRST_NM_COPY, 
            --RN.LAST_NM_COPY, 
            TRIM(INITCAP(RN.LAST_NM_COPY)|| ' ' ||INITCAP(RN.LAST_NM2)) LAST_NM_COPY,
            RN.MAIDEN_COPY,
            RN.ADDR1, RN.ADDR2, RN.COMPANY, RN.CITY, RN.STATE_CD, RN.ZIP_CD,
            RN.EMAIL_ADDR EMAIL_ADDR, RN.DAY_PHONE, RN.EVE_PHONE, RN.DAY_PHONE_EXT, RN.EVE_PHONE_EXT,
            (SELECT INITCAP(NVL(FIRST_NM_COPY, FIRST_NM)) || ' ' || INITCAP(NVL(LAST_NM_COPY, LAST_NM))|| ' ' ||INITCAP(LAST_NM2) FROM REG_NAMES WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND NM_ADDR_SUB_TYPE = 'RE') REGISTRANT_NAME,
            (SELECT INITCAP(NVL(FIRST_NM_COPY, FIRST_NM)) || ' ' || INITCAP(NVL(LAST_NM_COPY, LAST_NM))|| ' ' ||INITCAP(LAST_NM2) FROM REG_NAMES WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND NM_ADDR_SUB_TYPE = 'CO') COREGISTRANT_NAME,
            --(SELECT NVL(SUM(qty_requested),0) FROM reg_details WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND action_cd <> 'D') GIFTSREGISTERED,
            --(SELECT NVL(SUM(case when qty_fulfilled < 0 then 0
            --                else qty_fulfilled end ),0) + NVL(SUM(QTY_PURCH_RESRV),0 ) FROM REG_DETAILS WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND action_cd <> 'D') GIFTSPURCHASED,
            GET_GIFTS_REGISTERED(RN.REGISTRY_NUM) GIFTSREGISTERED,
            GET_GIFTS_PURCHASED(RN.REGISTRY_NUM) GIFTSPURCHASED,            
            'F' || ROW_NUMBER() OVER (ORDER BY RH.EVENT_DT) RANK
        FROM REG_NAMES RN, REG_HEADERS RH
        WHERE
            RN.REGISTRY_NUM = RH.REGISTRY_NUM AND
            GET_IN_COUNTRY( RN.REGISTRY_NUM, p_siteFlag, 2 ) = 1 AND
            RH.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' ) AND
            RN.ATG_PROFILE_ID = p_ProfileId    AND
            RH.ACTION_CD <> 'D' AND
            RH.STATUS_CD NOT IN  ('I','C','H') AND
            RH.ON_LINE_REG_FLAG = 'Y' AND
        --  TRUNC(RH.EVENT_DT) >= TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD'))
            TRUNC(NVL(RH.EVENT_DT, TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')))) >= TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD'))
        UNION
        SELECT RN.ATG_PROFILE_ID, RN.REGISTRY_NUM, RN.NM_ADDR_SUB_TYPE, RH.EVENT_TYPE, RH.EVENT_DT,
            RN.FIRST_NM_COPY, 
            --RN.LAST_NM_COPY,
            TRIM(INITCAP(RN.LAST_NM_COPY)|| ' ' ||INITCAP(RN.LAST_NM2)) LAST_NM_COPY, 
            RN.MAIDEN_COPY,
            RN.ADDR1, RN.ADDR2, RN.COMPANY, RN.CITY, RN.STATE_CD, RN.ZIP_CD,
            RN.EMAIL_ADDR EMAIL_ADDR, RN.DAY_PHONE, RN.EVE_PHONE, RN.DAY_PHONE_EXT, RN.EVE_PHONE_EXT,
            (SELECT INITCAP(NVL(FIRST_NM_COPY, FIRST_NM)) || ' ' || INITCAP(NVL(LAST_NM_COPY, LAST_NM))|| ' ' ||INITCAP(LAST_NM2) FROM REG_NAMES WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND NM_ADDR_SUB_TYPE = 'RE') REGISTRANT_NAME,
            (SELECT INITCAP(NVL(FIRST_NM_COPY, FIRST_NM)) || ' ' || INITCAP(NVL(LAST_NM_COPY, LAST_NM))|| ' ' ||INITCAP(LAST_NM2) FROM REG_NAMES WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND NM_ADDR_SUB_TYPE = 'CO') COREGISTRANT_NAME,
            --(SELECT NVL(SUM(qty_requested),0) FROM reg_details WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND action_cd <> 'D') GIFTSREGISTERED,
            --(SELECT NVL(SUM(case when qty_fulfilled < 0 then 0
            --                else qty_fulfilled end ),0) + NVL(SUM(QTY_PURCH_RESRV),0 ) FROM REG_DETAILS WHERE REGISTRY_NUM = RN.REGISTRY_NUM AND action_cd <> 'D') GIFTSPURCHASED,
            GET_GIFTS_REGISTERED(RN.REGISTRY_NUM) GIFTSREGISTERED,
            GET_GIFTS_PURCHASED(RN.REGISTRY_NUM) GIFTSPURCHASED,
            'P' || ROW_NUMBER() OVER (ORDER BY RH.EVENT_DT DESC) RANK
        FROM REG_NAMES RN, REG_HEADERS RH
        WHERE
            RN.REGISTRY_NUM = RH.REGISTRY_NUM AND
            GET_IN_COUNTRY( RN.REGISTRY_NUM, p_siteFlag, 2 ) = 1 AND
            RH.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' ) AND
            RN.ATG_PROFILE_ID = p_ProfileId    AND
            RH.ACTION_CD <> 'D' AND
            RH.STATUS_CD NOT IN  ('I','C','H') AND
            RH.ON_LINE_REG_FLAG = 'Y' AND
            TRUNC(RH.EVENT_DT) < TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD'))
    )
    ORDER BY RANK;

    cur_Info := v_Cursor;

    EXCEPTION
        WHEN OTHERS THEN
            RAISE;

END GET_REG_INFO_BY_PROFILEID;

PROCEDURE GET_REG_STATUSES_BY_PROFILEID( p_siteFlag IN VARCHAR, p_ProfileId IN VARCHAR2, cur_Info OUT T_CURSOR)
IS

    v_Cursor T_CURSOR;

    reg_count            NUMBER    := 0;
    v_sProfileIdExists    VARCHAR2(1) := 'N';

BEGIN

    OPEN v_Cursor FOR
    SELECT RN.REGISTRY_NUM, RH.STATUS_CD, decode(RH.STATUS_CD, 'L', 'lead', 'A', 'active', 'I', 'inactive', 'H', 'hold', 'C', 'canceled', 'D', 'deleted', 'unknown') STATUS_DESC
    FROM REG_NAMES RN, REG_HEADERS RH
    WHERE RN.REGISTRY_NUM = RH.REGISTRY_NUM
    AND GET_IN_COUNTRY( RN.REGISTRY_NUM, p_siteFlag, 2 ) = 1
    AND RH.EVENT_TYPE IN ( SELECT EVENT_TYPE FROM REG_TYPES WHERE INSTR( NVL( SITE_FLAG, '1' ), p_siteFlag ) > 0 AND INCLUDE_IN_SEARCH = 'Y' )
    AND RN.ATG_PROFILE_ID = p_ProfileId
    -- AND RH.ACTION_CD <> 'D'  -- RETURN ALL STATUSES ASSOCIATED TO A PROFILE ( 9/12/2013 ATG ).
    AND RH.ON_LINE_REG_FLAG = 'Y'
    ORDER BY RH.EVENT_DT DESC;

    cur_Info := v_Cursor;

    EXCEPTION
        WHEN OTHERS THEN
            RAISE;

END GET_REG_STATUSES_BY_PROFILEID;

PROCEDURE IMPORT_REGISTRY( p_RegNum IN NUMBER, p_vPass IN VARCHAR2, p_email IN VARCHAR2, p_firstNm IN VARCHAR2, p_lastNm IN VARCHAR2, p_phone1 IN VARCHAR2, p_phone2 IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_sProfileID IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_sRecordUpdated OUT VARCHAR2, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 )
IS
    v_nCount                NUMBER            :=    0;
    v_nRegNameCount         NUMBER          :=    0;
    v_sErrorExists          VARCHAR2(1)     :=    'N';
    v_sErrorMessage         VARCHAR2(100)   :=    '';
    v_sRecordUpdated        VARCHAR2(2)        :=    '';
    reg_count               NUMBER            := 0;
    registry_not_found      EXCEPTION;
    v_sMatchNmSubType       VARCHAR2(2)       :=    '';
    v_nCount2               NUMBER          :=    0;
    v_PasswordHint          VARCHAR2(60)    :=    NULL;
    v_isCoRegExists         NUMBER          :=    0;
    v_coRegProfileId        VARCHAR2(40)    :=    NULL;
    v_regProfileId          VARCHAR2(40)    :=    NULL;
    v_isCoRegImported       VARCHAR2(1)     :=    'N';
    v_isRegImported         VARCHAR2(1)     :=    'N';
    v_isRegHeaderUpdated    VARCHAR2(1)     :=    'N';
BEGIN

    SELECT COUNT(*) INTO reg_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    SELECT COUNT(*) INTO v_nCount FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum AND UPPER(TRIM(PASSWORD)) = UPPER(p_vPass);

    IF v_nCount > 0 THEN
        /*PASSWORD VALIDATED*/

        BEGIN
            /*Check if email matches RE or CO and update corresponding profile id*/
            /*Both the registrant and co-registrant can have the same email address*/
            SELECT count(*) INTO v_nRegNameCount FROM REG_NAMES WHERE REGISTRY_NUM = p_RegNum AND UPPER(TRIM(EMAIL_ADDR)) = UPPER(TRIM(p_email));

            IF v_nRegNameCount > 1 THEN

                UPDATE REG_NAMES
                SET    ATG_PROFILE_ID = p_sProfileID, ROW_XNG_DT = GET_GMT_DATE2(), ROW_XNG_USR = p_RowXngUser,
                ACTION_CD = 'C', PROCESS_FLAG = 'W', LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                LAST_TRANS_STORE = p_LastTransStore, LAST_MAINT_PROG = p_LastMaintProg, LAST_MAINT_USR = p_RowXngUser
                WHERE REGISTRY_NUM = p_RegNum
                AND NM_ADDR_SUB_TYPE in ( 'RE', 'CO' );

            ELSE

                SELECT NM_ADDR_SUB_TYPE INTO v_sMatchNmSubType FROM REG_NAMES WHERE REGISTRY_NUM = p_RegNum AND UPPER(TRIM(EMAIL_ADDR)) = UPPER(TRIM(p_email));

                UPDATE REG_NAMES
                SET    ATG_PROFILE_ID = p_sProfileID, ROW_XNG_DT = GET_GMT_DATE2(), ROW_XNG_USR = p_RowXngUser,
                ACTION_CD = 'C', PROCESS_FLAG = 'W', LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                LAST_TRANS_STORE = p_LastTransStore, LAST_MAINT_PROG = p_LastMaintProg, LAST_MAINT_USR = p_RowXngUser
                WHERE REGISTRY_NUM = p_RegNum
                AND NM_ADDR_SUB_TYPE = UPPER(v_sMatchNmSubType);

            END IF;

            v_sRecordUpdated := UPPER(v_sMatchNmSubType);
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    /*Email not found, so update RE profile id and email if it does not already have a profile id, ELSE update CO*/
                    SELECT COUNT(*) INTO v_nCount2 FROM REG_NAMES WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'RE' AND ATG_PROFILE_ID IS NULL;

                    /*If co-reg imports their registry before the registrant, the co-reg profile id will updated in profile id of RE record( confirmed by John lawlor)*/
                    IF v_nCount2 > 0 THEN
                        UPDATE REG_NAMES
                        SET    ATG_PROFILE_ID = p_sProfileID, EMAIL_ADDR = UPPER(TRIM(p_email)), ROW_XNG_DT = GET_GMT_DATE2(),
                        ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_USR = p_RowXngUser, LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                        LAST_TRANS_STORE = p_LastTransStore, LAST_MAINT_PROG = p_LastMaintProg, LAST_MAINT_USR = p_RowXngUser
                        WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'RE';

                        v_sRecordUpdated := 'RE';
                    ELSE
                        UPDATE REG_NAMES
                        SET    ATG_PROFILE_ID = p_sProfileID, EMAIL_ADDR = UPPER(TRIM(p_email)), ROW_XNG_DT = GET_GMT_DATE2(),
                        ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_USR = p_RowXngUser, LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                        LAST_TRANS_STORE = p_LastTransStore, LAST_MAINT_PROG = p_LastMaintProg, LAST_MAINT_USR = p_RowXngUser
                        WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'CO';

                        v_sRecordUpdated := 'CO';
                    END IF;

                WHEN OTHERS THEN
                    RAISE;

        END;

        /* CLEAR PASSWORD */
        BEGIN
            /* Check if reg has imported the registry */
            SELECT ATG_PROFILE_ID INTO v_regProfileId FROM REG_NAMES WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'RE';
            IF v_regProfileId IS NULL OR v_regProfileId = '' THEN
                v_isRegImported := 'N';
            ELSE
                v_isRegImported := 'Y';
            END IF;

            /* Check if co-reg has imported the registry */
            SELECT COUNT(*) INTO v_isCoRegExists FROM REG_NAMES WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'CO';
            IF v_isCoRegExists > 0 THEN
                SELECT ATG_PROFILE_ID INTO v_coRegProfileId FROM REG_NAMES WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'CO';
                IF v_coRegProfileId IS NULL OR v_coRegProfileId = '' THEN
                    v_isCoRegImported := 'N';
                ELSE
                    v_isCoRegImported := 'Y';
                END IF;
            END IF;

            IF v_isRegImported = 'Y' AND v_isCoRegImported = 'Y' THEN

                /* Clear password only if password hint is empty.*/
                SELECT PASSWORD_HINT INTO v_PasswordHint FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
                IF v_PasswordHint IS NULL OR v_PasswordHint = '' THEN
                    UPDATE REG_HEADERS
                    SET PASSWORD = NULL,
                    ROW_XNG_DT = GET_GMT_DATE2(), ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_USR = p_RowXngUser,
                    LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                    LAST_MAINT_USER = p_RowXngUser,
                    LAST_TRANS_STORE = p_LastTransStore, LAST_MAINT_PROG = p_LastMaintProg
                    WHERE REGISTRY_NUM = p_RegNum;

                    v_isRegHeaderUpdated := 'Y';
                END IF;
            END IF;
        END;

        /*Updated Audit field in reg_header*/
        /*Sankar suggested doing this*/
        /*It will help production support identify where this regsitry got touched*/
        IF v_isRegHeaderUpdated = 'N' THEN
            UPDATE REG_HEADERS SET
                ROW_XNG_DT = GET_GMT_DATE2(), ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_USR = p_RowXngUser,
                LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                LAST_MAINT_USER = p_RowXngUser,
                LAST_TRANS_STORE = p_LastTransStore, LAST_MAINT_PROG = p_LastMaintProg
                WHERE REGISTRY_NUM = p_RegNum;
        END IF;

    ELSE
        v_sErrorExists     :=     'Y';
        v_sErrorMessage    :=    'Invalid password';
    END IF;

    p_sRecordUpdated := v_sRecordUpdated;
    p_sErrorExists    :=    v_sErrorExists;
    p_sErrorMessage    :=    v_sErrorMessage;

EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END IMPORT_REGISTRY;

PROCEDURE LINK_REGISTRY_TO_ATG_PROFILE( p_RegNum IN NUMBER, p_registrantType IN VARCHAR2,p_profileID IN VARCHAR2,p_Email IN VARCHAR2, p_RowXngUser in VARCHAR2, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 )
IS
    v_nCount                    NUMBER                              := 0;
    v_sErrorExists              VARCHAR2(1)                         := 'N';
    v_sErrorMessage             VARCHAR2(100)                       := '';
    v_reg_count                 NUMBER                              := 0;
    v_existing_atgProfileId     REG_NAMES.ATG_PROFILE_ID%TYPE;
    v_existing_email            REG_NAMES.EMAIL_ADDR%TYPE;
    v_process_cd                PROCESS_SUPPORT.PROCESS_CD%TYPE     := 'atgws';
    v_process_sub_cd            PROCESS_SUPPORT.PROCESS_SUB_CD%TYPE := 'LINK_REGPROF' ;
    v_message                   PROCESS_SUPPORT.MESSAGE%TYPE        := null;

    registry_not_found  EXCEPTION;

--Use Corporate Store Number 990 and CSRFIX for LAST_MAINT_PROG for Registry updates made by CSR.

BEGIN

    SELECT COUNT(*) INTO v_reg_count
    FROM REG_NAMES
    WHERE REGISTRY_NUM = p_RegNum
    AND NM_ADDR_SUB_TYPE = p_registrantType;

    IF v_reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    IF v_reg_count > 0 THEN

        --Write existing/new ATG Profile Id, email Id'to process_support for Audit purpose if Update is sucessful.

        SELECT ATG_PROFILE_ID, EMAIL_ADDR INTO v_existing_atgProfileId, v_existing_email
        FROM REG_NAMES
        WHERE REGISTRY_NUM = p_RegNum
        AND NM_ADDR_SUB_TYPE = p_registrantType;

        UPDATE REG_NAMES
            SET    ATG_PROFILE_ID = p_profileID,
            EMAIL_ADDR = UPPER(TRIM(p_Email)),
            ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_DT = GET_GMT_DATE2(),
            LAST_TRANS_STORE = 990,
            LAST_MAINT_PROG = 'CSRFIX',
            ROW_XNG_USR = p_RowXngUser,
            LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
            LAST_MAINT_USR = p_RowXngUser
            WHERE REGISTRY_NUM = p_RegNum
            AND NM_ADDR_SUB_TYPE = p_registrantType;

    END IF;

    --Log transactions for Audit if update is sucessful.

    IF SQL%ROWCOUNT > 0 THEN
        v_message := SUBSTR('Update Sucessful. RegistryNum: ' || p_RegNum || ' Registrant Type: '|| p_registrantType|| ' New ProfileId: ' || p_profileID ||' Existing ProfileId: '|| v_existing_atgProfileId || ' New Email Id: ' || p_Email || ' Existing Email Id: ' || v_existing_email, 1, 512);

        INSERT INTO PROCESS_SUPPORT
        ( id, process_cd, process_sub_cd, message, row_xng_usr )
        VALUES
        ( process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER );
    END IF;


    p_sErrorExists      :=    v_sErrorExists;
    p_sErrorMessage     :=    v_sErrorMessage;

EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END LINK_REGISTRY_TO_ATG_PROFILE;

PROCEDURE UPD_COREG_PROFILE_BY_REGNUM( p_RegNum IN NUMBER, p_sCoRegProfileID IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 )
IS
    v_nCount        NUMBER            :=    0;
    v_sErrorExists    VARCHAR2(1)        :=    'N';
    v_sErrorMessage    VARCHAR2(100)    :=    '';
    reg_count                NUMBER    := 0;
    registry_not_found    EXCEPTION;
BEGIN

    UPDATE REG_NAMES
        SET    ATG_PROFILE_ID = p_sCoRegProfileID,
        ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_DT = GET_GMT_DATE2(),
        LAST_TRANS_STORE = p_LastTransStore,
        LAST_MAINT_PROG = p_LastMaintProg,
        ROW_XNG_USR = p_RowXngUser,
        LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
        LAST_MAINT_USR = p_RowXngUser
        WHERE REGISTRY_NUM = p_RegNum AND NM_ADDR_SUB_TYPE = 'CO';

    p_sErrorExists    :=    v_sErrorExists;
    p_sErrorMessage    :=    v_sErrorMessage;

EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END UPD_COREG_PROFILE_BY_REGNUM;

PROCEDURE UPD_COREG_PROFILE_BY_EMAIL( p_Email IN VARCHAR2, p_sCoRegProfileID IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2, cur_Info OUT T_CURSOR, p_sErrorExists OUT VARCHAR2, p_sErrorMessage OUT VARCHAR2 )
IS
    v_nCount            NUMBER            :=    0;
    v_sErrorExists        VARCHAR2(1)        :=    'N';
    v_sErrorMessage        VARCHAR2(100)    :=    '';
    reg_count            NUMBER    := 0;
    v_Cursor             T_CURSOR;
    registry_not_found    EXCEPTION;
BEGIN

    UPDATE REG_NAMES
        SET    ATG_PROFILE_ID = p_sCoRegProfileID, ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_DT = GET_GMT_DATE2(),
        LAST_TRANS_STORE = p_LastTransStore,
        LAST_MAINT_PROG = p_LastMaintProg,
        ROW_XNG_USR = p_RowXngUser, LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE), LAST_MAINT_USR = p_RowXngUser
        WHERE UPPER(EMAIL_ADDR) = UPPER(TRIM(p_Email)) AND NM_ADDR_SUB_TYPE = 'CO';

    OPEN v_Cursor FOR
    SELECT REGISTRY_NUM, EVENT_TYPE, EVENT_DT FROM REG_HEADERS
    WHERE REGISTRY_NUM IN ( SELECT REGISTRY_NUM FROM REG_NAMES WHERE UPPER(EMAIL_ADDR) = UPPER(TRIM(p_Email)) AND NM_ADDR_SUB_TYPE = 'CO' );

    p_sErrorExists    :=    v_sErrorExists;
    p_sErrorMessage    :=    v_sErrorMessage;
    cur_Info := v_Cursor;
EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found for email::' || p_Email );
    WHEN OTHERS THEN
        RAISE;

END UPD_COREG_PROFILE_BY_EMAIL;

PROCEDURE SET_ANNC_CARD_COUNT( p_RegNum IN NUMBER, p_nCount IN NUMBER, p_RowXngUser IN VARCHAR2 )
IS
    reg_count                NUMBER    := 0;
    registry_not_found    EXCEPTION;
BEGIN
    SELECT COUNT(*) INTO reg_count FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegNum;
    IF reg_count = 0 THEN
        RAISE registry_not_found;
    END IF;

    UPDATE REG_HEADERS SET
                ANNC_CARDS_REQ = p_nCount,
                ROW_XNG_DT = GET_GMT_DATE2(), ACTION_CD = 'C', PROCESS_FLAG = 'W', ROW_XNG_USR = p_RowXngUser,
                LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
                LAST_MAINT_USER = p_RowXngUser
            WHERE REGISTRY_NUM = p_RegNum;

EXCEPTION
    WHEN registry_not_found THEN
            RAISE_APPLICATION_ERROR( -20501, 'Registry not found::' || p_RegNum );
    WHEN OTHERS THEN
        RAISE;

END SET_ANNC_CARD_COUNT;

PROCEDURE GET_REGISTRY_PWD( p_RegNum IN NUMBER, p_password OUT VARCHAR2, p_email OUT VARCHAR2 )
IS

BEGIN

    SELECT EMAIL_ADDR into p_email
    FROM REG_NAMES
    WHERE REGISTRY_NUM = p_RegNum
    AND NM_ADDR_SUB_TYPE = 'RE';

    SELECT PASSWORD into p_password
    FROM REG_HEADERS
    WHERE REGISTRY_NUM = p_RegNum;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_password := '';
        p_email := '';

    WHEN OTHERS THEN
        RAISE;

END GET_REGISTRY_PWD;

PROCEDURE UPD_REG_NAMES_BY_PROFILE_ID(
                        p_ProfileId IN VARCHAR,
                        p_LastName IN VARCHAR, p_FirstName IN VARCHAR,
                        p_Phone1 IN VARCHAR, p_Phone2 IN VARCHAR,
                        p_Phone1Ext IN VARCHAR, p_Phone2Ext IN VARCHAR,
                        p_Email IN VARCHAR,
                        p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2)
IS
    v_addrNum            NUMBER                := 0;
    v_addrType            VARCHAR2(2)                := NULL;
    reg_count            NUMBER    := 0;
    co_count            NUMBER    := 0;
    registry_not_found    EXCEPTION;
    v_JDADateNow    NUMBER        := 0; -- call function to get this.

BEGIN

    SELECT GET_JDADATE_NOW( SYSDATE ) JDADateNow INTO v_JDADateNow FROM DUAL;

    IF p_Phone1 IS NULL AND p_Phone2 IS NULL THEN

        UPDATE REG_NAMES
            SET
            LAST_NM = UPPER(p_LastName),
            FIRST_NM = UPPER(p_FirstName),
            LAST_NM_COPY = p_LastName,
            FIRST_NM_COPY = p_FirstName,
            EMAIL_ADDR = UPPER(TRIM(p_Email)),
            ROW_XNG_DT = GET_GMT_DATE2(),
            LAST_MAINT_DT_TM = v_JDADateNow,
            LAST_TRANS_STORE = p_LastTransStore,
            LAST_MAINT_PROG = p_LastMaintProg,
            ACTION_CD = 'C',
            PROCESS_FLAG = 'W',
            ROW_XNG_USR = p_RowXngUser,
            LAST_MAINT_USR = p_RowXngUser
            WHERE
            ATG_PROFILE_ID = p_ProfileId;

    ELSIF p_Phone1 IS NOT NULL AND p_Phone2 IS NOT NULL THEN

        UPDATE REG_NAMES
            SET
            DAY_PHONE = p_Phone1,
            EVE_PHONE = p_Phone2,
            LAST_NM = UPPER(p_LastName),
            FIRST_NM = UPPER(p_FirstName),
            LAST_NM_COPY = p_LastName,
            FIRST_NM_COPY = p_FirstName,
            EMAIL_ADDR = UPPER(TRIM(p_Email)),
            ROW_XNG_DT = GET_GMT_DATE2(),
            LAST_MAINT_DT_TM = v_JDADateNow,
            LAST_TRANS_STORE = p_LastTransStore,
            LAST_MAINT_PROG = p_LastMaintProg,
            ACTION_CD = 'C',
            PROCESS_FLAG = 'W',
            ROW_XNG_USR = p_RowXngUser,
            LAST_MAINT_USR = p_RowXngUser
            WHERE
            ATG_PROFILE_ID = p_ProfileId;
    ELSIF p_Phone1 IS NOT NULL AND p_Phone2 IS NULL THEN

        UPDATE REG_NAMES
            SET
            DAY_PHONE = p_Phone1,
            LAST_NM = UPPER(p_LastName),
            FIRST_NM = UPPER(p_FirstName),
            LAST_NM_COPY = p_LastName,
            FIRST_NM_COPY = p_FirstName,
            EMAIL_ADDR = UPPER(TRIM(p_Email)),
            ROW_XNG_DT = GET_GMT_DATE2(),
            LAST_MAINT_DT_TM = v_JDADateNow,
            LAST_TRANS_STORE = p_LastTransStore,
            LAST_MAINT_PROG = p_LastMaintProg,
            ACTION_CD = 'C',
            PROCESS_FLAG = 'W',
            ROW_XNG_USR = p_RowXngUser,
            LAST_MAINT_USR = p_RowXngUser
            WHERE
            ATG_PROFILE_ID = p_ProfileId;

    ELSIF p_Phone1 IS NULL AND p_Phone2 IS NOT NULL THEN

        UPDATE REG_NAMES
            SET
            EVE_PHONE = p_Phone2,
            LAST_NM = UPPER(p_LastName),
            FIRST_NM = UPPER(p_FirstName),
            LAST_NM_COPY = p_LastName,
            FIRST_NM_COPY = p_FirstName,
            EMAIL_ADDR = UPPER(TRIM(p_Email)),
            ROW_XNG_DT = GET_GMT_DATE2(),
            LAST_MAINT_DT_TM = v_JDADateNow,
            LAST_TRANS_STORE = p_LastTransStore,
            LAST_MAINT_PROG = p_LastMaintProg,
            ACTION_CD = 'C',
            PROCESS_FLAG = 'W',
            ROW_XNG_USR = p_RowXngUser,
            LAST_MAINT_USR = p_RowXngUser
            WHERE
            ATG_PROFILE_ID = p_ProfileId;
    ELSE
        RAISE_APPLICATION_ERROR( -20000, 'If clause not met. One of the if clauses have to satisfy.' );
    END IF;

    EXCEPTION
        WHEN OTHERS THEN
            RAISE;

END UPD_REG_NAMES_BY_PROFILE_ID;

PROCEDURE VALIDATE_REGISTRY_PASS_EMAIL( p_Email IN VARCHAR2, p_Password IN VARCHAR2, p_isValidAccount OUT VARCHAR2 )
IS
    v_nCount            NUMBER            :=    0;
    v_isValidAccount    VARCHAR2(1)        := 'N';
    v_password          REG_HEADERS.PASSWORD%TYPE;

CURSOR cur_validate_pwd IS
SELECT PASSWORD
FROM REG_HEADERS
WHERE REGISTRY_NUM IN
(   SELECT DISTINCT REGISTRY_NUM
    FROM REG_NAMES
    WHERE UPPER(EMAIL_ADDR) = UPPER(TRIM(p_Email) )
    AND ( NM_ADDR_SUB_TYPE = 'RE' OR NM_ADDR_SUB_TYPE = 'CO')
)
AND PASSWORD IS NOT NULL
AND UPPER(PASSWORD) LIKE UPPER(TRIM(p_Password)) || '%';

BEGIN

    OPEN cur_validate_pwd;
    LOOP
    FETCH cur_validate_pwd INTO v_password;
    EXIT WHEN cur_validate_pwd%NOTFOUND;

        IF UPPER(TRIM(v_password)) =  UPPER(TRIM(p_Password)) THEN
            v_nCount := 1;
            EXIT;
        END IF;

    END LOOP;
    CLOSE cur_validate_pwd;

    IF v_nCount > 0 THEN
        v_isValidAccount :=    'Y';
    END IF;

    p_isValidAccount :=    v_isValidAccount;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;

END VALIDATE_REGISTRY_PASS_EMAIL;

PROCEDURE DISABLE_FUTURE_SHIPPING(p_RegistryNum IN VARCHAR2, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser in VARCHAR2)
IS

BEGIN

    UPDATE REG_NAMES
        SET ACTION_CD = 'D', PROCESS_FLAG = 'W', ROW_XNG_DT = GET_GMT_DATE2(),
        LAST_TRANS_STORE = p_LastTransStore, LAST_MAINT_PROG = p_LastMaintProg,
        ROW_XNG_USR = p_RowXngUser, LAST_MAINT_DT_TM = GET_JDADATE_NOW(SYSDATE),
        LAST_MAINT_USR = p_RowXngUser
        WHERE REGISTRY_NUM = p_RegistryNum AND NM_ADDR_SUB_TYPE = 'FU';

    EXCEPTION
        WHEN OTHERS THEN
            RAISE;

END DISABLE_FUTURE_SHIPPING;

PROCEDURE COPY_REGISTRY( p_siteFlag in VARCHAR, p_src_reg_num IN NUMBER, p_target_reg_num IN NUMBER, p_createProg IN VARCHAR, p_LastTransStore IN NUMBER, p_LastMaintProg IN VARCHAR2, p_RowXngUser IN VARCHAR2, p_NoOfItemsAdded OUT NUMBER)
IS

    PRAGMA AUTONOMOUS_TRANSACTION;

p_message                   VARCHAR2(5000);
Invalid_src_registry        EXCEPTION;
Invalid_target_registry     EXCEPTION;
Source_registry_empty       EXCEPTION;
v_line_item_count        NUMBER  := 0;
v_qty_requested                NUMBER  := 0;

CURSOR cur_reg_item_list IS
SELECT R.SKU,
    R.QTY_REQUESTED,
    NULL REFERENCE_ID,
    'REG' ITEM_TYPE,
    NULL ASSEMBLY_SELECTED,
    NULL ASSEMBLY_PRICE,
    NULL LTL_DELIVERY_SERVICE,
    NULL LTL_DELIVERY_PRICE,
    NULL PERSONALIZATION_CODE,
    NULL PERSONALIZATION_PRICE,
    NULL CUSTOMIZATION_PRICE,
    NULL PERSONALIZATION_DESCRIP,
    NULL IMAGE_URL,
    NULL IMAGE_URL_THUMB,
    NULL MOB_IMAGE_URL,
    NULL MOB_IMAGE_URL_THUMB
FROM REG_DETAILS R
WHERE R.ACTION_CD <> 'D' AND R.REGISTRY_NUM = p_src_reg_num
UNION
SELECT R2.SKU,
        R2.QTY_REQUESTED,
        R2.REFERENCE_ID,
        R2.ITEM_TYPE,
    R2.ASSEMBLY_SELECTED,
    R2.ASSEMBLY_PRICE,
    R2.LTL_DELIVERY_SERVICE,
    R2.LTL_DELIVERY_PRICE,
    R2.PERSONALIZATION_CODE,
    R2.PERSONALIZATION_PRICE,
    R2.CUSTOMIZATION_PRICE,
    R2.PERSONALIZATION_DESCRIP,
    R2.IMAGE_URL,
    R2.IMAGE_URL_THUMB,
    R2.MOB_IMAGE_URL,
    R2.MOB_IMAGE_URL_THUMB
FROM REG_DETAILS2 R2
WHERE R2.ACTION_CD <> 'D' AND R2.REGISTRY_NUM = p_src_reg_num;

cur_rec cur_reg_item_list%ROWTYPE;

BEGIN
    --Check if both Source and Target Registries belongs to the site passed in.
    
    IF GET_IN_COUNTRY(p_src_reg_num, p_siteFlag) <> 1 THEN
        RAISE Invalid_src_registry;
    END IF;
    
    IF GET_IN_COUNTRY(p_target_reg_num, p_siteFlag, 2) <> 1 THEN
        RAISE Invalid_target_registry;
    END IF;
    
    p_NoOfItemsAdded := 0;

    OPEN cur_reg_item_list;
    LOOP
        FETCH cur_reg_item_list INTO cur_rec;
        EXIT WHEN cur_reg_item_list%NOTFOUND;

    v_line_item_count := v_line_item_count + 1;

        IF cur_rec.item_type = 'PER' THEN
              CONTINUE;
        END IF;

        IF cur_rec.item_type IS NULL THEN
              CONTINUE;
        END IF;

    v_qty_requested := cur_rec.qty_requested;

    IF (v_qty_requested = 0) THEN
        --outside registry item
        v_qty_requested := 1;
    END IF;
        
        --Call add_reg_item2 to add each item to target Registry.
        --Pass "0" for p_last_maint_dt_tm as a place holder becasue GET_JDADATE_NOW will get the last_maint_dt_tm.
    ADD_REG_ITEM2( p_target_reg_num, cur_rec.sku, v_qty_requested,
        cur_rec.reference_id,  cur_rec.item_type, cur_rec.assembly_selected, cur_rec.assembly_price, cur_rec.ltl_delivery_service, 
        cur_rec.ltl_delivery_price, cur_rec.personalization_code, cur_rec.personalization_price, cur_rec.customization_price, cur_rec.personalization_descrip,  
        cur_rec.image_url, cur_rec.image_url_thumb, cur_rec.mob_image_url, cur_rec.mob_image_url_thumb,
        0, p_createProg, p_LastTransStore, p_LastMaintProg, p_RowXngUser, p_siteFlag);

        --Accumulate quantity added.
        p_NoOfItemsAdded := p_NoOfItemsAdded + v_qty_requested;

    END LOOP;
    CLOSE cur_reg_item_list;
    
    IF v_line_item_count = 0 THEN

    RAISE Source_registry_empty;  
    ELSE
        COMMIT;
        --Log in Process support for Audit.
        p_Message := 'RegCopy Successful. SourceRegistryNum: '|| p_src_reg_num ||' TargetRegistryNum: '|| p_target_reg_num || ' Number Of Items Added: '|| p_NoOfItemsAdded ||' createProg: ' || p_createProg || ' LastTransStore: ' ||p_LastTransStore || ' LastMaintProg: ' ||p_LastMaintProg || ' RowXngUser: ' || p_RowXngUser;        
        INSERT INTO PROCESS_SUPPORT (id, process_cd, process_sub_cd, message,row_xng_dt,row_xng_usr,row_status)
        VALUES (process_support_seq.NEXTVAL, 'webservice', 'atgws', RTRIM(SUBSTR(p_Message,1,512)),SYSDATE,'ECOMADMIN','A');
        COMMIT;   
        
    END IF;
EXCEPTION    
    WHEN Invalid_src_registry THEN
            RAISE_APPLICATION_ERROR( -20501, 'Source registry does not belongs to the concept::' || p_src_reg_num );
    WHEN Invalid_target_registry THEN
            RAISE_APPLICATION_ERROR( -20502, 'Target registry does not belongs to the concept::' || p_target_reg_num );
    WHEN Source_registry_empty THEN
            RAISE_APPLICATION_ERROR( -20503, 'Source registry is empty. Source registry::' ||p_src_reg_num || ' Target registry::' ||p_target_reg_num );            
    WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR( -20504, 'Registry not found. Source registry::' ||p_src_reg_num || ' Target registry::' ||p_target_reg_num );
    WHEN OTHERS THEN
        --Write to Process Support.
        ROLLBACK;
        p_NoOfItemsAdded := 0;
        p_Message := 'RegCopy Failed. ' ||SUBSTR(SQLERRM || ' ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200) || ' SourceRegistryNum: '|| p_src_reg_num ||' TargetRegistryNum: '|| p_target_reg_num || ' createProg: ' || p_createProg || ' LastTransStore: ' ||p_LastTransStore || ' LastMaintProg: ' ||p_LastMaintProg || ' RowXngUser: ' || p_RowXngUser;        
        INSERT INTO PROCESS_SUPPORT (id, process_cd, process_sub_cd, message,row_xng_dt,row_xng_usr,row_status)
        VALUES (process_support_seq.NEXTVAL, 'webservice', 'atgws', RTRIM(SUBSTR(p_Message,1,512)),SYSDATE,'ECOMADMIN','I');
        COMMIT;        
        RAISE_APPLICATION_ERROR( -20505, 'RegCopy Failed. ' ||SUBSTR(SQLERRM || ' ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200) || ' SourceRegistryNum: '|| p_src_reg_num ||' TargetRegistryNum: '|| p_target_reg_num || ' createProg: ' || p_createProg || ' LastTransStore: ' ||p_LastTransStore || ' LastMaintProg: ' ||p_LastMaintProg || ' RowXngUser: ' || p_RowXngUser);
    
END COPY_REGISTRY; 

---------------------------------------------
-- UTILITY FUNCTIONS
---------------------------------------------

PROCEDURE CREATE_SUPPORT_REC( p_process_cd IN VARCHAR, p_sub_code IN varchar, p_message IN varchar, p_rec_id OUT NUMBER )
IS

    v_process_rec_id    NUMBER(14) := 0;

BEGIN

    SELECT PROCESS_SUPPORT_SEQ.NEXTVAL INTO v_process_rec_id FROM DUAL;

    INSERT INTO PROCESS_SUPPORT
    ( id, process_cd, process_sub_cd, message, row_xng_usr )
    VALUES
    ( v_process_rec_id, p_process_cd, p_sub_code, p_message, USER );

    p_rec_id := v_process_rec_id;

END CREATE_SUPPORT_REC;

PROCEDURE SEND_ERROR_EMAIL
IS

v_subject          VARCHAR2(1000) := '-';
v_msg_body         long;
v_count number := 0;
v_o_n_success_flag NUMBER;

BEGIN

SELECT COUNT(*) INTO v_count FROM PROCESS_SUPPORT WHERE PROCESS_CD = 'webservice' AND PROCESS_SUB_CD = 'atgws' AND
TRUNC(ROW_XNG_DT) = TRUNC(SYSDATE-1);

v_msg_body := 'AtgWs web service error count on ' || TRUNC(SYSDATE) || ' = ' || v_count;
v_subject := 'AtgWs web service error count on ' || TRUNC(SYSDATE) || ' = ' || v_count;
--EMailUtilities.Mail_Multi_Recipient
--('ITWebAppSupport@bedbath.com', 'AtgWs@att.com', v_subject, v_msg_body, 'AtgWs', null, null, v_o_n_success_flag);

END SEND_ERROR_EMAIL;

---------------------------------------------
-- COUPONS
---------------------------------------------
PROCEDURE GET_REGULAR_COUPONS( p_SiteFlag IN VARCHAR, p_EmailAddr IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_OrderID NUMBER := 0;
    v_Cursor T_CURSOR;

BEGIN

    --Expired coupons needs to returned as per the Coupon Wallet project.
    OPEN v_Cursor FOR
    SELECT CD.entry_cd, CD.descrip as coupon_name, to_char(CASE WHEN CCVW.alt_end_dt < CD.end_dt THEN CCVW.alt_end_dt ELSE CD.end_dt END, 'MM/DD/YYYY HH24:MI:SS') as expiration_dt,
    to_char(START_DT, 'MM/DD/YYYY HH24:MI:SS') as start_dt
    FROM coupon_def CD, coupon_code_validate_web CCVW, coupon_code_usage_web CCUW
    WHERE CCVW.entry_cd = CD.entry_cd
        AND CCVW.entry_cd = CCUW.entry_cd(+)
        AND CCVW.email_addr = CCUW.email_addr(+)
        AND CD.start_dt < sysdate
        AND CD.class = 'I'
        AND CCVW.email_addr = UPPER(TRIM(p_EmailAddr))
        AND NVL(CCVW.alt_end_dt, CD.end_dt) > SYSDATE AND CD.end_dt > SYSDATE
        AND ( CD.use_limit > CCUW.use_cnt OR CCUW.use_cnt IS NULL )
        AND CD.row_status = 'A'
        AND CCVW.row_status = 'A'
        AND INSTR( NVL( CD.SITE_FLAG, '1' ), p_SiteFlag ) > 0;

    cur_Info := v_Cursor;

END GET_REGULAR_COUPONS;

PROCEDURE IS_VALID_ENTRY_CD( p_SiteFlag IN VARCHAR, p_EntryCd IN VARCHAR,  p_sIsValid OUT VARCHAR2 )
IS
    v_sIsValid        VARCHAR2(1) := 'N';
    p_count         NUMBER         :=    0;
BEGIN

    SELECT COUNT(*) INTO p_count
    FROM coupon_def CD
    WHERE CD.start_dt < sysdate
        AND CD.end_dt > SYSDATE
        AND CD.class = 'P'
        AND CD.row_status = 'A'
        AND INSTR( NVL( CD.SITE_FLAG, '1' ), p_SiteFlag ) > 0
        AND ENTRY_CD = p_EntryCd;

    IF p_count > 0 THEN
        v_sIsValid := 'Y';
    END IF;

    p_sIsValid := v_sIsValid;

END IS_VALID_ENTRY_CD;

PROCEDURE VERIFY_EMAIL_COUPON( p_SiteFlag IN VARCHAR2, p_sEmailAddr IN VARCHAR2, p_sCampaign IN VARCHAR2, p_sCouponCode IN VARCHAR2, p_nRetCode OUT NUMBER, p_sRetCodeDesc OUT VARCHAR2 )
IS
    v_nRetCode        NUMBER             :=     -1;
    v_sRetCodeDesc    VARCHAR2(20)    :=    '';
    v_nUseCount        NUMBER             :=    0;

    v_sCampEndDate    VARCHAR2(30)    :=    '';
    v_sStatus        VARCHAR2(20)    :=    '';
    v_nActiveCoupon    NUMBER             :=    0;

    v_sEmailAddr    VARCHAR2(100)    :=    '';
    v_nUseLimit      NUMBER             :=    0;

    COUPON_VALIDATED                 CONSTANT     NUMBER    := 0;    --ENTRY_CD AND EMAIL_ADDR EXISTS IN COUPON_CODE_VALIDATE_WEB
    COUPON_ACTIVATED_SUCCESSFULLY     CONSTANT     NUMBER    := 1;
    COUPON_ALREADY_ACTIVATED         CONSTANT     NUMBER    := 2;
    INVALID_COUPON_EMAIL_ADDR         CONSTANT     NUMBER    := 3;
    EXPIRED_COUPON                     CONSTANT     NUMBER    := 4;
    COUPON_REDEEMED                 CONSTANT     NUMBER    := 5;
    COUPON_ACTIVATION_ERROR         CONSTANT     NUMBER    := 6;
    COUPON_VALIDATED2                CONSTANT     NUMBER    := 7;    --ENTRY_CD AND EMAIL_ADDR EXISTS IN ONLINE_COUPON_EMAIL_ADDR

BEGIN

    v_sEmailAddr := UPPER(TRIM(p_sEmailAddr));

    BEGIN
        SELECT USE_CNT INTO v_nUseCount
        FROM COUPON_CODE_USAGE_WEB
        WHERE ENTRY_CD = p_sCouponCode
        AND EMAIL_ADDR = v_sEmailAddr;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_nUseCount := 0;
    END;

    SELECT USE_LIMIT INTO v_nUseLimit FROM COUPON_DEF WHERE ENTRY_CD = p_sCouponCode;

    IF v_nUseCount >= v_nUseLimit THEN
        v_nRetCode := COUPON_REDEEMED;
    ELSE

        --v_nRetCode    :=    INVALID_COUPON_EMAIL_ADDR;

        BEGIN
            SELECT to_char(CASE WHEN CCVW.alt_end_dt < CF.end_dt THEN CCVW.alt_end_dt ELSE CF.end_dt END, 'MM/DD/YYYY HH24:MI:SS') ALT_END_DT,
                CCVW.ROW_STATUS AS ROW_STATUS,
                CASE WHEN NVL(CCVW.alt_end_dt, CF.end_dt) + 1 > SYSDATE AND CF.end_dt + 1 > SYSDATE THEN 1 ELSE 0 END AS ACTIVE_COUPON
                INTO v_sCampEndDate, v_sStatus, v_nActiveCoupon
                FROM COUPON_CODE_VALIDATE_WEB CCVW, COUPON_DEF CF
                WHERE CCVW.ENTRY_CD = p_sCouponCode
                AND CCVW.EMAIL_ADDR = v_sEmailAddr
                AND CF.ENTRY_CD = CCVW.ENTRY_CD
                AND INSTR( NVL( CF.SITE_FLAG, '1' ), p_SiteFlag ) > 0;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_sStatus := '';
        END;

        IF v_sStatus = '' OR v_sStatus IS NULL THEN
            v_nRetCode := CHK_ONL_CPN_RECIPIENTS( p_SiteFlag, v_sEmailAddr, p_sCampaign, p_sCouponCode );
        ELSIF v_nActiveCoupon = 0 THEN
            v_nRetCode := EXPIRED_COUPON;
        ELSIF v_sStatus = 'I' THEN
            v_nRetCode := COUPON_VALIDATED;
        ELSIF v_sStatus = 'A' THEN
            v_nRetCode := COUPON_ALREADY_ACTIVATED;
        END IF;

    END IF;

    CASE v_nRetCode
        WHEN COUPON_VALIDATED THEN v_sRetCodeDesc := 'Validated';
        WHEN COUPON_ACTIVATED_SUCCESSFULLY THEN v_sRetCodeDesc := 'Activated';
        WHEN COUPON_ALREADY_ACTIVATED THEN v_sRetCodeDesc := 'Activated';
        WHEN INVALID_COUPON_EMAIL_ADDR THEN v_sRetCodeDesc := 'Invalid';
        WHEN EXPIRED_COUPON THEN v_sRetCodeDesc := 'Expired';
        WHEN COUPON_REDEEMED THEN v_sRetCodeDesc := 'Redeemed';
        WHEN COUPON_ACTIVATION_ERROR THEN v_sRetCodeDesc := 'Error';
        WHEN COUPON_VALIDATED2 THEN v_sRetCodeDesc := 'Not Activated';
        ELSE v_sRetCodeDesc := '';
    END CASE;

    p_nRetCode := v_nRetCode;
    p_sRetCodeDesc := v_sRetCodeDesc;

END VERIFY_EMAIL_COUPON;

FUNCTION CHK_ONL_CPN_RECIPIENTS( p_SiteFlag IN VARCHAR2, sEmailAddr IN VARCHAR, sCampaign IN VARCHAR, sCouponCode IN VARCHAR ) RETURN NUMBER
IS
    v_sIsNotExpired     VARCHAR2(1)     :=    '0';
    v_nRetCode             NUMBER            := -1;
    v_sStatus             VARCHAR2(10)    := '';
BEGIN

    BEGIN
        SELECT OC.ROW_STATUS AS ROW_STATUS,
        CASE WHEN CF.END_DT > SYSDATE THEN 1 ELSE 0 END AS IS_NOT_EXPIRED
        INTO v_sStatus, v_sIsNotExpired
        FROM ONLINE_COUPON_EMAIL_ADDR OC, COUPON_DEF CF
        WHERE OC.ENTRY_CD = sCouponCode
        AND OC.EMAIL_ADDR = UPPER(TRIM(sEmailAddr))
        AND CF.ENTRY_CD = OC.ENTRY_CD
        AND INSTR( NVL( CF.SITE_FLAG, '1' ), p_SiteFlag ) > 0;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_sStatus := '';
    END;

    IF v_sStatus = '' OR v_sStatus IS NULL THEN
        v_nRetCode := 3;    --INVALID_COUPON_EMAIL_ADDR
    ELSIF v_sIsNotExpired <> '1' THEN
        v_nRetCode := 4;    --EXPIRED_COUPON
    ELSE
        v_nRetCode := 7; --COUPON_VALIDATED2 BUT NOT ACTIVATED
    END IF;

    RETURN v_nRetCode;
END CHK_ONL_CPN_RECIPIENTS;

PROCEDURE ACTIVATE_COUPON( p_SiteFlag IN VARCHAR2, p_sEmailAddr IN VARCHAR2, p_sCouponCode IN VARCHAR2, p_nRetCode OUT NUMBER, p_sRetCodeDesc OUT VARCHAR2 )
IS
    v_nRetCode        NUMBER             :=     0;
    v_sRetCodeDesc    VARCHAR2(20)    :=    'Activated';
BEGIN

    UPDATE COUPON_CODE_VALIDATE_WEB
        SET ROW_STATUS = 'A',
        ROW_XNG_DT = SYSDATE
        WHERE ENTRY_CD = p_sCouponCode
        AND EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr));

    p_nRetCode := v_nRetCode;
    p_sRetCodeDesc := v_sRetCodeDesc;

END ACTIVATE_COUPON;

PROCEDURE COPY_ACTIVATE_COUPON( p_SiteFlag IN VARCHAR2, p_sEmailAddr IN VARCHAR2, p_sCouponCode IN VARCHAR2, p_CampaignEndDate IN VARCHAR2, p_nRetCode OUT NUMBER, p_sRetCodeDesc OUT VARCHAR2 )
IS
    v_nRetCode            NUMBER             :=     7;
    v_sRetCodeDesc        VARCHAR2(20)    :=    'Activated';
    v_CampaignEndDate    VARCHAR2(20)    :=    '';
BEGIN

    v_CampaignEndDate := p_CampaignEndDate;

    IF v_CampaignEndDate IS NULL OR v_CampaignEndDate = '' THEN
        SELECT TO_CHAR(TRUNC(END_DT)) INTO v_CampaignEndDate FROM COUPON_DEF
        WHERE ENTRY_CD = p_sCouponCode;
    END IF;


    INSERT INTO COUPON_CODE_VALIDATE_WEB
            ( entry_cd, email_addr, validation_key, alt_end_dt, generation_usr, generation_date, row_xng_dt, row_xng_usr, row_status )
            VALUES (
            p_sCouponCode, UPPER(TRIM(p_sEmailAddr)), 'ONLINECOUPON', TO_DATE( v_CampaignEndDate ), USER, SYSDATE, SYSDATE, USER, 'A' );

    UPDATE COUPON_CODE_VALIDATE_WEB
        SET ROW_STATUS = 'A',
        ROW_XNG_DT = SYSDATE
        WHERE ENTRY_CD = p_sCouponCode
        AND EMAIL_ADDR = UPPER(TRIM(p_sEmailAddr));

    p_nRetCode := v_nRetCode;
    p_sRetCodeDesc := v_sRetCodeDesc;

END COPY_ACTIVATE_COUPON;

PROCEDURE APPLY_COUPON_TO_USAGE_WEB( p_OrderNum IN VARCHAR, p_OriginCd IN VARCHAR, p_SiteFlag IN VARCHAR, p_couponCd IN VARCHAR, p_emailAddr IN VARCHAR, p_UniqueCouponCode IN VARCHAR )
IS

    v_recordFound    BOOLEAN := TRUE;
    v_emailAddr        coupon_code_usage_web.email_addr%TYPE := '';
    v_useCnt        coupon_code_usage_web.use_cnt%TYPE := 0;
    v_OrderID coupon_code_usage_web.order_id%TYPE := NULL;

BEGIN

    IF p_OrderNum IS NOT NULL THEN

        v_OrderID := TO_NUMBER( SUBSTR( p_OrderNum, 4, LENGTH(p_OrderNum) - 3 ) );

    END IF;

    BEGIN
        SELECT email_addr, use_cnt INTO v_emailAddr, v_useCnt
        FROM coupon_code_usage_web
        WHERE entry_cd = p_couponCd
        AND email_addr = UPPER(TRIM(p_emailAddr));

        EXCEPTION
            WHEN OTHERS THEN
                v_recordFound := FALSE;
    END;

    IF v_recordFound THEN
        v_useCnt := v_useCnt + 1;

        UPDATE coupon_code_usage_web SET
            use_cnt = v_useCnt,
            unique_coupon_code = p_UniqueCouponCode,
            row_xng_dt = SYSDATE,
            row_xng_usr = USER,
            order_id = v_OrderID
        WHERE entry_cd = p_couponCd
            AND email_addr = UPPER(TRIM(p_emailAddr));
    ELSE
        INSERT into coupon_code_usage_web ( entry_cd, email_addr, use_cnt, unique_coupon_code, order_id, row_xng_dt, row_xng_usr, row_status )
        VALUES ( p_couponCd, UPPER(TRIM(p_emailAddr)), 1, p_UniqueCouponCode, v_OrderID, SYSDATE, USER, 'A' );
    END IF;


END APPLY_COUPON_TO_USAGE_WEB;

---------------------------------------------
---FUNCTIONS
---------------------------------------------

FUNCTION GET_EVENTTYPE_DESC( p_sEventType IN VARCHAR2 ) RETURN VARCHAR2
IS

    v_sEventTypeDesc    VARCHAR2(50)    := '';

BEGIN

    IF UPPER(p_sEventType) = 'BRD' THEN
        v_sEventTypeDesc := 'Wedding';
    ELSIF UPPER(p_sEventType) = 'COM' THEN
        v_sEventTypeDesc := 'Commitment Ceremony';
    ELSIF UPPER(p_sEventType) = 'ANN' THEN
        v_sEventTypeDesc := 'Anniversary';
    ELSIF UPPER(p_sEventType) = 'HSW' THEN
        v_sEventTypeDesc := 'Housewarming';
    ELSIF UPPER(p_sEventType) = 'COL' THEN
        v_sEventTypeDesc := 'College';
    ELSIF UPPER(p_sEventType) = 'BIR' THEN
        v_sEventTypeDesc := 'Birthday';
    ELSIF UPPER(p_sEventType) = 'RET' THEN
        v_sEventTypeDesc := 'Retirement';
    ELSIF UPPER(p_sEventType) = 'PNH' THEN
        v_sEventTypeDesc := 'Pack and Hold';
    ELSIF UPPER(p_sEventType) = 'OTH' THEN
        v_sEventTypeDesc := 'Other';
    ELSIF UPPER(p_sEventType) = 'BA1' THEN
        v_sEventTypeDesc := 'Baby';
    ELSIF UPPER(p_sEventType) = 'BR1' THEN
        v_sEventTypeDesc := 'Birthday';
    ELSIF UPPER(p_sEventType) = 'OT1' THEN
        v_sEventTypeDesc := 'Other';
    ELSE
        v_sEventTypeDesc := 'p_sEventType';
        --RAISE_APPLICATION_ERROR( -20000, 'Invalid Event type' );
    END IF;

    RETURN v_sEventTypeDesc;

END GET_EVENTTYPE_DESC;

FUNCTION GET_JDA_DEPT_SORT_SEQ( p_JDADeptID IN NUMBER, p_siteFlag IN VARCHAR ) RETURN NUMBER
IS

v_JDADeptID NUMBER := p_JDADeptID;
v_SortSeq   NUMBER := 999;

BEGIN

    IF  p_siteFlag = '1' THEN
        CASE v_JDADeptID
            WHEN 610 THEN v_SortSeq := 0;
            WHEN 605 THEN v_SortSeq := 1;
            WHEN 705 THEN v_SortSeq := 2;
            WHEN 560 THEN v_SortSeq := 3;
            WHEN 565 THEN v_SortSeq := 4;
            WHEN 570 THEN v_SortSeq := 5;
            WHEN 575 THEN v_SortSeq := 6;
            WHEN 550 THEN v_SortSeq := 7;
            WHEN 305 THEN v_SortSeq := 8;
            WHEN 310 THEN v_SortSeq := 9;
            WHEN 360 THEN v_SortSeq := 10;
            WHEN 370 THEN v_SortSeq := 11;
            WHEN 365 THEN v_SortSeq := 12;
            WHEN 380 THEN v_SortSeq := 13;
            WHEN 580 THEN v_SortSeq := 14;
            WHEN 410 THEN v_SortSeq := 15;
            WHEN 315 THEN v_SortSeq := 16;
            WHEN 100 THEN v_SortSeq := 17;
            WHEN 105 THEN v_SortSeq := 18;
            WHEN 165 THEN v_SortSeq := 19;
            WHEN 170 THEN v_SortSeq := 20;
            WHEN 175 THEN v_SortSeq := 21;
            WHEN 115 THEN v_SortSeq := 22;
            WHEN 120 THEN v_SortSeq := 23;
            WHEN 110 THEN v_SortSeq := 24;
            WHEN 735 THEN v_SortSeq := 25;
            WHEN 725 THEN v_SortSeq := 26;
            WHEN 720 THEN v_SortSeq := 27;
            WHEN 710 THEN v_SortSeq := 28;
            WHEN 190 THEN v_SortSeq := 29;
            WHEN 320 THEN v_SortSeq := 30;
            WHEN 195 THEN v_SortSeq := 31;
            WHEN 200 THEN v_SortSeq := 32;
            WHEN 730 THEN v_SortSeq := 33;
            WHEN 740 THEN v_SortSeq := 34;
            WHEN 750 THEN v_SortSeq := 35;
            WHEN 755 THEN v_SortSeq := 36;
            WHEN 760 THEN v_SortSeq := 37;
            WHEN 770 THEN v_SortSeq := 38;
            WHEN 780 THEN v_SortSeq := 39;
            WHEN 590 THEN v_SortSeq := 40;
            WHEN 325 THEN v_SortSeq := 41;
            WHEN 500 THEN v_SortSeq := 42;
            WHEN 722 THEN v_SortSeq := 43;
            WHEN 765 THEN v_SortSeq := 44;
            WHEN 871 THEN v_SortSeq := 45;
            WHEN 872 THEN v_SortSeq := 46;
            WHEN 873 THEN v_SortSeq := 47;
            WHEN 874 THEN v_SortSeq := 48;
            WHEN 875 THEN v_SortSeq := 49;
            WHEN 876 THEN v_SortSeq := 50;
            WHEN 877 THEN v_SortSeq := 51;
            WHEN 878 THEN v_SortSeq := 52;
            WHEN 879 THEN v_SortSeq := 53;
            WHEN 880 THEN v_SortSeq := 54;
            WHEN 881 THEN v_SortSeq := 55;
            WHEN 882 THEN v_SortSeq := 56;
            WHEN 883 THEN v_SortSeq := 57;
            WHEN 884 THEN v_SortSeq := 58;
            WHEN 885 THEN v_SortSeq := 59;
            WHEN 886 THEN v_SortSeq := 60;
            WHEN 887 THEN v_SortSeq := 61;
            WHEN 888 THEN v_SortSeq := 62;
            WHEN 889 THEN v_SortSeq := 63;
            WHEN 890 THEN v_SortSeq := 64;
            WHEN 891 THEN v_SortSeq := 65;
            WHEN 892 THEN v_SortSeq := 66;
            WHEN 790 THEN v_SortSeq := 67;
            WHEN 1 THEN v_SortSeq := 68;
            WHEN 700 THEN v_SortSeq := 69;
            WHEN 5 THEN v_SortSeq := 70;
            WHEN 8 THEN v_SortSeq := 71;
            WHEN 9 THEN v_SortSeq := 72;
            WHEN 10 THEN v_SortSeq := 73;
            WHEN 791 THEN v_SortSeq := 74;
            WHEN 792 THEN v_SortSeq := 75;
            WHEN 793 THEN v_SortSeq := 76;
            WHEN 794 THEN v_SortSeq := 77;
            WHEN 795 THEN v_SortSeq := 78;
            WHEN 800 THEN v_SortSeq := 79;
            WHEN 810 THEN v_SortSeq := 80;
            WHEN 820 THEN v_SortSeq := 81;
            WHEN 830 THEN v_SortSeq := 82;
            WHEN 901 THEN v_SortSeq := 83;
            WHEN 905 THEN v_SortSeq := 84;
            WHEN 908 THEN v_SortSeq := 85;
            WHEN 715 THEN v_SortSeq := 86;
            WHEN 785 THEN v_SortSeq := 87;
            WHEN 840 THEN v_SortSeq := 88;
            WHEN 955 THEN v_SortSeq := 89;
            ELSE v_SortSeq := 999;
        END CASE;
    ELSE
        CASE v_JDADeptID
            WHEN 874 THEN v_SortSeq := 0;
            WHEN 875 THEN v_SortSeq := 1;
            WHEN 876 THEN v_SortSeq := 2;
            WHEN 877 THEN v_SortSeq := 3;
            WHEN 871 THEN v_SortSeq := 4;
            WHEN 872 THEN v_SortSeq := 5;
            WHEN 873 THEN v_SortSeq := 6;
            WHEN 889 THEN v_SortSeq := 7;
            WHEN 879 THEN v_SortSeq := 8;
            WHEN 878 THEN v_SortSeq := 9;
            WHEN 888 THEN v_SortSeq := 10;
            WHEN 880 THEN v_SortSeq := 11;
            WHEN 881 THEN v_SortSeq := 12;
            WHEN 882 THEN v_SortSeq := 13;
            WHEN 883 THEN v_SortSeq := 14;
            WHEN 884 THEN v_SortSeq := 15;
            WHEN 885 THEN v_SortSeq := 16;
            WHEN 886 THEN v_SortSeq := 17;
            WHEN 887 THEN v_SortSeq := 18;
            WHEN 891 THEN v_SortSeq := 18;
            WHEN 890 THEN v_SortSeq := 20;
            WHEN 892 THEN v_SortSeq := 21;
            ELSE v_SortSeq := 999;
        END CASE;
    END IF;

    RETURN v_SortSeq;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;

END GET_JDA_DEPT_SORT_SEQ;

FUNCTION GET_NEXT_REGNUM RETURN NUMBER
IS

    v_regNum            reg_headers.REGISTRY_NUM%TYPE    := -1;
    v_originUseCd        reg_next.ORIGIN_CD_USE%TYPE;
    v_regUpdateSeq        reg_next.REG_UPDATE_SEQ%TYPE    := 0;

    v_paramValue        ecom_global_params.PARAM_VALUE%TYPE;

    v_availableKeyFound    BOOLEAN    := false;
    v_entryUsedCnt        NUMBER    := 0;

    REG_NEXT_UPDATE_FAIL    EXCEPTION;

    PRAGMA AUTONOMOUS_TRANSACTION;

BEGIN

    SELECT TRIM( param_value ) INTO v_paramValue FROM ecom_global_params where PARAM_NAME = 'reg_origin_code';
    IF LENGTH( v_paramValue ) > 3 THEN
        v_originUseCd := SUBSTR( v_paramValue, 1, 3 );
    ELSE
        v_originUseCd := v_paramValue;
    END IF;

    v_availableKeyFound := false;
    WHILE NOT v_availableKeyFound LOOP

        SELECT REG_UPDATE_SEQ.NextVal INTO v_regUpdateSeq FROM DUAL;

        -- MARK NEXT REGISTRY NUMBER AS USED.
        UPDATE REG_NEXT SET
            available_flag = 'N',
            reg_update_seq = v_regUpdateSeq,
            row_xng_dt = SYSDATE,
            row_xng_usr = USER,
            row_status = 'I'
        WHERE registry_num = ( SELECT MIN( registry_num ) registry_num FROM reg_next WHERE available_flag = 'Y' AND ORIGIN_CD_USE = v_originUseCd )
        AND ORIGIN_CD_USE = v_originUseCd;

        -- IF RECORD WAS NOT UPDATED,
        -- UPDATING OF MULTIPLE RECORDS NOT POSSIBLE, REGISTRY_NUM IS PRIMARY KEY.

        -- IF NO RECORD WAS UPDATED, THAT MEANS ALL ENTRIES IN REG_NEXT HAVE BEEN USED. GENERATE ERROR, CALLING PROGRAM WILL LOG
        IF SQL%ROWCOUNT != 1 THEN
            RAISE REG_NEXT_UPDATE_FAIL;
        END IF;

        -- GET REGISTRY NUMBER OF RECORD UPDATED ABOVE. NO_DATA_FOUND CONDITION IS HIGHLY UNLIKELY.
        SELECT REGISTRY_NUM INTO v_regNum FROM REG_NEXT
        WHERE REG_UPDATE_SEQ = v_regUpdateSeq AND ORIGIN_CD_USE = v_originUseCd;

        -- A REGISTRY NUMBER IS STILL AVAILABLE, TEST IF REGISTRY NUMBER WAS JUST USED IN REG_HEADERS.
        IF v_regNum > 1 THEN
            BEGIN
                -- IF COUNT > 0 THEN RECORD WAS TAKEN BETWEEN UPDATE OF REG_NEXT AND REG_HEADERS. CONTINUE LOOPING.
                -- ANY EXCEPTION FROM EXECUTING A COUNT IS NOT GOOD, GENERATE AN EXCEPTION FOR CALLING PREOGRAM TO LOG.
                SELECT COUNT( REGISTRY_NUM ) entry_used_cnt INTO v_entryUsedCnt FROM REG_HEADERS WHERE REGISTRY_NUM = v_regNum;

                IF v_entryUsedCnt = 0 THEN
                    v_availableKeyFound := true;
                END IF;
            END;
        END IF;
    END LOOP;

    COMMIT;

    RETURN v_regNum;

EXCEPTION
    WHEN REG_NEXT_UPDATE_FAIL THEN
        RAISE_APPLICATION_ERROR( -20001, 'ERROR ran out of available registry Numbers!' );
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR( -20002, 'Could not select registry number in REG_NEXT' );
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR( -20002, SQLERRM || ' ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE );

END GET_NEXT_REGNUM;

FUNCTION GET_JDADATE_NOW( p_datetime IN DATE ) RETURN NUMBER
IS

    v_JDADateNow    NUMBER    := 0;

BEGIN

    -- RETURNS THE SAME VALUE AS JDADateNow in RegFuncs.asp
    SELECT TO_NUMBER( TO_CHAR( GET_GMT_DATE(), 'YYYYMMDDHH24MISS' ) ) JDADateNow INTO v_JDADateNow FROM DUAL;

    RETURN v_JDADateNow;

END GET_JDADATE_NOW;

FUNCTION GET_GMT_DATE RETURN DATE
IS
    v_new_time DATE := SYSDATE;
    v_time_zone_offset varchar2(10) := '-05:00';
    v_time_zone_abbrev varchar2(3) := 'EDT';

BEGIN
    SELECT SUBSTR( TZ_OFFSET( 'US/Eastern' ), 1, 6 ) into v_time_zone_offset FROM DUAL;

    IF v_time_zone_offset = '-05:00' THEN
        v_time_zone_abbrev := 'EST';
    ELSE
        v_time_zone_abbrev := 'EDT';
    END IF;

    SELECT NEW_TIME( SYSDATE, v_time_zone_abbrev, 'GMT') into v_new_time FROM DUAL;

    RETURN v_new_time;
END GET_GMT_DATE;

FUNCTION GET_GMT_DATE2 RETURN DATE
IS
    v_new_time DATE := SYSDATE;
    v_time_zone_offset varchar2(10) := '-05:00';
    v_time_zone_abbrev varchar2(3) := 'EDT';
    --v_sys_time DATE := (SYSDATE + 10)  + 08/24;

BEGIN
    SELECT SUBSTR( TZ_OFFSET( 'US/Eastern' ), 1, 6 ) into v_time_zone_offset FROM DUAL;

    IF v_time_zone_offset = '-05:00' THEN
        v_time_zone_abbrev := 'EST';
    ELSE
        v_time_zone_abbrev := 'EDT';
    END IF;
    
    SELECT NEW_TIME( SYSDATE, v_time_zone_abbrev, 'GMT') into v_new_time FROM DUAL;

    --SELECT NEW_TIME( v_sys_time, v_time_zone_abbrev, 'GMT') into v_new_time FROM DUAL;
    
    --Add 1 hour to jump the time from 2 to 3 AM during  Daylight saving time change hour.
    --This code covers till 2026.
    --Need to add additional years if this function is still being used after 2026.
    
    IF TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2016031302') AND ('2016031303') 
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2017031202') AND ('2017031203')
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2018031102') AND ('2018031103')
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2019031002') AND ('2019031003')
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2020030802') AND ('2020030803')
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2021031402') AND ('2021031403') 
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2022031302') AND ('2022031303') 
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2023031202') AND ('2023031203') 
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2024031002') AND ('2024031003')  
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2025030902') AND ('2025030903')
       OR TO_CHAR(v_new_time, 'YYYYMMDDHH') BETWEEN ('2026030802') AND ('2026030803') THEN
       
        v_new_time := v_new_time + 1/24;
        
    END IF;
    

    RETURN v_new_time;
    
END GET_GMT_DATE2;

FUNCTION FILTER_OPTIONS_SPLIT (p_filterOptions IN VARCHAR2, p_filterPos NUMBER ) RETURN VARCHAR2
IS

    v_filterOpt    VARCHAR2(100)    := NULL;

BEGIN

    SELECT TRIM(REGEXP_SUBSTR(p_filterOptions,'[^;]+', 1, p_filterPos )) INTO v_filteropt FROM DUAL;
        
    RETURN v_filterOpt;
    
EXCEPTION
   WHEN OTHERS THEN
     --Return Null incase of errors.
     RETURN v_filterOpt;

END FILTER_OPTIONS_SPLIT;

FUNCTION REG_SEARCH_STATE_FILTER( p_stateToCheck VARCHAR2, p_statesIncluded VARCHAR2, p_SiteFlag CHAR ) RETURN NUMBER
IS
/*
Name       : REG_SEARCH_EVENT_FILTER
version    : 1.0
Type       : Function 
Instance   : All
Platform   : Oracle / SUN Solaris
Programmer : Satish Bathula 
Date       : 3/17/2014 
Main Task  : Determine if a state should be filtered out. Allow the same SQL to be used when filtering is applied or not.
            --a. If "state:All" is passed then validate the reg_names.state_cd (p_stateToCheck) against states table.
            --b. Else Check if  reg_names.state_cd in the p_statesIncluded list ( string check ).         
            --c. Check if that state_cd valid for the site specified ( sql against states table ).

-- Modification History:
-- Programmer   Date        Modification
*/

v_stateIsIncluded       NUMBER(1)  := 0; -- 1 EVENT IS INCLUDED; 0 EVENT IS NOT INCLUDED
v_statesCheckCount      NUMBER     := 0;
v_stateCount            NUMBER     := 0;
v_RegstatesCount        NUMBER     := 0;
v_stateToCheck          VARCHAR2 (100);


BEGIN

    v_stateToCheck := TRIM(p_stateToCheck);    
    v_stateCount   := INSTR(p_statesIncluded, v_stateToCheck);
    
     IF p_statesIncluded = 'state:All' THEN    
        --Always return TRUE if "All" is passed in.
        v_stateIsIncluded := 1;
    ELSIF  v_stateCount > 0 THEN
        
        SELECT COUNT(state_cd) INTO v_RegstatesCount FROM states
        WHERE  state_cd = v_stateToCheck
        AND INSTR( NVL( site_flag, '1' ), p_SiteFlag ) > 0;            
            
        IF v_RegstatesCount > 0 THEN
             v_stateIsIncluded := 1;
        END IF;
    END IF;
    
   
    RETURN v_stateIsIncluded;

EXCEPTION
    WHEN OTHERS THEN
        RETURN v_stateIsIncluded;
        
END REG_SEARCH_STATE_FILTER;

FUNCTION REG_SEARCH_EVENT_FILTER( p_eventTypeToCheck VARCHAR2, p_eventsIncluded VARCHAR2, p_SiteFlag CHAR ) RETURN NUMBER
IS
/*
Name       : REG_SEARCH_EVENT_FILTER
version    : 1.0
Type       : Function 
Instance   : All
Platform   : Oracle / SUN Solaris
Programmer : Satish Bathula 
Date       : 3/17/2014 
Main Task  : Determine if a reg event type should be filtered out. Allow the same SQL to be used when filtering is applied or not.
            --a. If "eventType:All" is passed then validate the reg_headers.event_type (p_eventTypeToCheck) against reg_types.
            --b. Else Check if  reg_headers.event_type in the p_eventsIncluded list ( string check ).         
            --c. Check if that event type valid for the site specified ( sql against reg_types ).

-- Modification History:
-- Programmer   Date        Modification
        
*/

v_eventIsIncluded       NUMBER(1)  := 0; -- 1 EVENT IS INCLUDED; 0 EVENT IS NOT INCLUDED
v_eventsCheckCount      NUMBER     := 0;
v_eventCount            NUMBER     := 0;
v_RegeventsCount        NUMBER     := 0;
v_eventTypeToCheck      VARCHAR2(100);


BEGIN
    
    v_eventTypeToCheck := TRIM(p_eventTypeToCheck);
    v_eventCount       := INSTR(p_eventsIncluded, v_eventTypeToCheck); 
    
    IF p_eventsIncluded = 'eventType:All' OR v_eventCount > 0 THEN
                
        SELECT count(EVENT_TYPE) INTO v_RegeventsCount FROM REG_TYPES 
        WHERE  EVENT_TYPE = v_eventTypeToCheck
        AND INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0 
        AND INCLUDE_IN_SEARCH = 'Y';
            
         IF v_RegeventsCount > 0 THEN
              v_eventIsIncluded := 1;
         END IF;
    END IF;   

    RETURN v_eventIsIncluded;

EXCEPTION
    WHEN OTHERS THEN
        RETURN v_eventIsIncluded;
        
END REG_SEARCH_EVENT_FILTER;

FUNCTION GET_PRODUCT_DESC( p_sku IN NUMBER, p_cntryCd CHAR) RETURN VARCHAR2
IS

    v_productDesc    item_desc_translation.product_title%TYPE  := NULL;

BEGIN

    SELECT product_title INTO v_productDesc  
    FROM item_desc_translation 
    WHERE sku = p_sku 
    AND country_cd = p_cntryCd
    AND row_status = 'A';

    RETURN v_productDesc;
    
EXCEPTION
WHEN OTHERS THEN
    RETURN v_productDesc;

END GET_PRODUCT_DESC;

FUNCTION GET_PRODUCT_PRICE( p_sku IN NUMBER, p_cntryCd CHAR ) RETURN NUMBER
IS

    --Return 99999.99 when there is no MX price.
    v_jda_retail_price    sku_pricing_other.jda_retail_price%TYPE := 99999.99;

BEGIN

    SELECT jda_retail_price INTO v_jda_retail_price 
    FROM sku_pricing_other 
    WHERE sku_id = p_sku 
    AND country_cd = p_cntryCd
    AND row_status = 'A';

    RETURN v_jda_retail_price;

EXCEPTION
WHEN OTHERS THEN
    RETURN v_jda_retail_price;
    
END GET_PRODUCT_PRICE;

FUNCTION GET_GIFTS_REGISTERED( p_RegNum IN NUMBER) RETURN NUMBER
IS

    v_gifts_registered    reg_details.qty_requested%TYPE := 0;
    v_gifts_registered2   reg_details.qty_requested%TYPE := 0;

BEGIN   
    
     --GET GIFTS REGISTERED COUNT
    SELECT NVL(SUM(qty_requested),0) INTO v_gifts_registered
    FROM reg_details
    WHERE registry_num = p_RegNum
    AND action_cd <> 'D';
    
    --GET  PERSONALIZED GIFTS REGISTERED COUNT. (DRACO).
    SELECT NVL(SUM(qty_requested),0) INTO v_gifts_registered2
    FROM reg_details2
    WHERE registry_num = p_RegNum
    AND action_cd <> 'D';
    
    v_gifts_registered := v_gifts_registered + v_gifts_registered2;  

    RETURN v_gifts_registered;

EXCEPTION
WHEN OTHERS THEN
    RETURN v_gifts_registered;
    
END GET_GIFTS_REGISTERED;

FUNCTION GET_GIFTS_PURCHASED( p_RegNum IN NUMBER) RETURN NUMBER
IS

    v_gifts_purchased    reg_details.qty_fulfilled%TYPE := 0;
    v_gifts_purchased2   reg_details.qty_fulfilled%TYPE := 0;

BEGIN
    
    
      --GET PURCHASED COUNT
    SELECT NVL(SUM(case when qty_fulfilled < 0 then 0
                        else qty_fulfilled end ),0) + NVL(SUM(QTY_PURCH_RESRV),0 )
    INTO v_gifts_purchased
    FROM REG_DETAILS
    WHERE REGISTRY_NUM = p_RegNum
    AND action_cd <> 'D';
    
    --GET  PERSONALIZED GIFTS PURCHASED COUNT. (DRACO).
    SELECT NVL(SUM(case when qty_fulfilled < 0 then 0
                        else qty_fulfilled end ),0) + NVL(SUM(QTY_PURCH_RESRV),0 )
    INTO v_gifts_purchased2
    FROM REG_DETAILS2
    WHERE REGISTRY_NUM = p_RegNum
    AND action_cd <> 'D';

    v_gifts_purchased := v_gifts_purchased + v_gifts_purchased2;       
    
    RETURN v_gifts_purchased;

EXCEPTION
WHEN OTHERS THEN
    RETURN v_gifts_purchased;
    
END GET_GIFTS_PURCHASED;

---------------------------------------------
-- MEMBER ACCOUNTS
---------------------------------------------

-- FOR MEMBER LOGIN - CHKLOGIN.
PROCEDURE GET_MEMBER_INFO( p_username IN varchar, p_origin_cd IN varchar, cur_MemberInfo OUT T_CURSOR )
IS
    v_Cursor T_CURSOR;

BEGIN

    OPEN v_Cursor FOR
    SELECT first_nm, last_nm, pswd, mbr_num
    FROM members
    WHERE email_addr = UPPER(p_username)
        AND origin_cd = p_origin_cd;

    cur_MemberInfo := v_Cursor;

END GET_MEMBER_INFO;

PROCEDURE GET_ACCT_PWD( p_userName IN VARCHAR, p_originCd IN VARCHAR, p_password OUT VARCHAR )
IS

BEGIN

    SELECT pswd into p_password
    FROM members
    WHERE email_addr = UPPER(p_userName)
    AND origin_cd = p_originCd;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_password := '';

    WHEN OTHERS THEN
        RAISE;

END GET_ACCT_PWD;

PROCEDURE GET_ALL_ACCT_PWD( p_Email IN VARCHAR, cur_Info OUT T_CURSOR )
IS
    v_Cursor T_CURSOR;
BEGIN

    OPEN v_Cursor FOR
    SELECT pswd
    FROM members
    WHERE email_addr = UPPER(p_Email);

    cur_Info := v_Cursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;

END GET_ALL_ACCT_PWD;

END ATGWS;
/
SHOW ERROR;