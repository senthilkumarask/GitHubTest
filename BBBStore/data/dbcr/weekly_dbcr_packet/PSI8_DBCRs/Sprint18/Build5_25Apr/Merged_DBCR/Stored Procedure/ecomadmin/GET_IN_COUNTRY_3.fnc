SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;
SET DEFINE OFF;


CREATE OR REPLACE FUNCTION ECOMADMIN.GET_IN_COUNTRY( p_RegistryNum NUMBER, p_SiteFlag CHAR, p_filter_flag INTEGER DEFAULT 0 ) RETURN NUMBER IS
/*
Name       : Get_in_Country
version    : GEMINI 3.0
Type       : Procedure
Instance   : All
Platform   : Oracle / SUN Solaris
Programmer : Glenn Chin
Date       : 5/30/2008
Main Task  : Determine if the state is in the country that you are calling from

Change History
Name/Date:    Glenn Chin - 5/30/2008.
Desc: New

Name/Date:    Sunil - 8/26/2008.
Desc: Updates SQL to fix select using ROWNUM

-- Programmer   Date        Modification
-- SB           3/19/2014   Added condition to validate Mexican registries.
*/

v_CountryFlag NUMBER(1) := 0;

BEGIN

 IF (p_filter_flag = 1 OR p_filter_flag = 3) THEN

 -- IF the CREATE_COUNTRY matches the p_SiteFlag THEN RETURN 1

 	SELECT COUNT(*) INTO v_CountryFlag FROM REG_HEADERS
 	WHERE REGISTRY_NUM = p_RegistryNum
 	AND CREATE_COUNTRY = DECODE(p_SiteFlag, '1', 'USA', '2', 'USA', '3', 'CAN', '5', 'MEX');

 	IF v_CountryFlag != 0 
 	THEN
		RETURN 1;	
 	END IF;

 END IF;

 IF (p_filter_flag = 2 OR p_filter_flag = 3) THEN

 -- IF there are active REG_NAMES records, and they all have a NULL STATE_CD THEN RETURN 1

	SELECT COUNT(*) INTO v_CountryFlag FROM
 	(
		SELECT COUNT(*) ACTIVE_COUNT, SUM(DECODE(RN.STATE_CD, NULL, 1, 0)) NULL_STATE_COUNT 
		FROM REG_NAMES RN
     		WHERE RN.REGISTRY_NUM = p_RegistryNum
      		AND ( RN.NM_ADDR_NUM IN ( 1, 3 ) OR ( RN.NM_ADDR_NUM = 4 AND RN.AS_OF_DT > 0 AND RN.AS_OF_DT <= TO_NUMBER( TO_CHAR( SYSDATE, 'yyyymmdd' ) ) ) )
      		AND ROW_STATUS = 'A'
      		AND ACTION_CD <> 'D'
 	)
 	WHERE ACTIVE_COUNT > 0 AND ACTIVE_COUNT = NULL_STATE_COUNT;
--        AND EXISTS (SELECT 1 FROM REG_HEADERS WHERE REGISTRY_NUM = p_RegistryNum AND CREATE_COUNTRY IS NULL);

 	IF v_CountryFlag != 0 THEN
		RETURN 1;
 	END IF;

 END IF;

 IF p_SiteFlag = '1' OR p_SiteFlag = '2' OR p_SiteFlag = '3' THEN

     SELECT CASEFLAG INTO v_CountryFlag FROM
     (
     SELECT CASE WHEN RN.STATE_CD IN
        (
            SELECT STATE_CD FROM STATES
            WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0
        ) THEN 1 ELSE 0 END AS CASEFLAG
     FROM REG_NAMES RN
     WHERE RN.REGISTRY_NUM = p_RegistryNum
      AND ( RN.NM_ADDR_NUM IN ( 1, 3 ) OR ( RN.NM_ADDR_NUM = 4 AND RN.AS_OF_DT > 0 AND RN.AS_OF_DT <= TO_NUMBER( TO_CHAR( SYSDATE, 'yyyymmdd' ) ) ) )
      AND ROW_STATUS = 'A'
      AND ACTION_CD <> 'D'
      AND RN.STATE_CD IS NOT NULL
     ORDER BY RN.NM_ADDR_NUM DESC
     )
     WHERE ROWNUM = 1;
 
 ELSIF (p_SiteFlag = '5') THEN
    --Mexico Site validation.
    SELECT CASEFLAG INTO v_CountryFlag FROM
     (
     SELECT CASE WHEN RN.STATE_CD IN
        (
            SELECT STATE_CD FROM STATES
            WHERE INSTR( NVL( SITE_FLAG, '1' ), p_SiteFlag ) > 0
        ) THEN 1 ELSE 0 END AS CASEFLAG
     FROM REG_NAMES RN
     WHERE RN.REGISTRY_NUM = p_RegistryNum
      AND ( RN.NM_ADDR_NUM IN ( 1, 3 ) OR ( RN.NM_ADDR_NUM = 4 AND RN.AS_OF_DT > 0 AND RN.AS_OF_DT <= TO_NUMBER( TO_CHAR( SYSDATE, 'yyyymmdd' ) ) ) )
      AND ROW_STATUS = 'A'
      AND ACTION_CD <> 'D'
      AND RN.STATE_CD IS NOT NULL
      AND RN.CNTRY = 'MX'
     ORDER BY RN.NM_ADDR_NUM DESC
     )
     WHERE ROWNUM = 1;
 END IF;

RETURN v_CountryFlag;

EXCEPTION
    WHEN NO_DATA_FOUND THEN        
        RETURN v_CountryFlag;

END GET_IN_COUNTRY;
/
SHOW ERROR;