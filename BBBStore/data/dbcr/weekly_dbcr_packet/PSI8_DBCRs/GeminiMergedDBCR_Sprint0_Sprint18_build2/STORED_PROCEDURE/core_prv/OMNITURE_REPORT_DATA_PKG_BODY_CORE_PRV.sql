-- Build Version : PSI8 Release Sprint11 Build2
SET ECHO ON;
set DEFINE OFF;
set SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Monday-January-04-2015   
--  DDL for Package Body OMNITURE_REPORT_DATA_PKG
--------------------------------------------------------

CREATE OR REPLACE
PACKAGE BODY BBB_CORE_PRV.OMNITURE_REPORT_DATA_PKG 
AS
PROCEDURE ARCHIVE_OMNITURE_DATA(p_report_id in VARCHAR, p_concept IN VARCHAR, p_records_moved OUT NUMBER)
IS

Invalid_Concept     EXCEPTION;
v_record_moved	    NUMBER  := 0;
CURSOR cur_omniture_report_data IS
SELECT R.ID,
  R.REPORT_ID,
  R.REPORT_SUITE,
	R.PERIOD,
	R.CONCEPT,
	R.BOOST_SCORE,
	R.PRODUCT_ID,
	R.KEYWORD
FROM BBB_OMNITURE_REPORT_DATA R
WHERE R.REPORT_ID <> p_report_id AND R.CONCEPT = p_concept;

cur_rec cur_omniture_report_data%ROWTYPE;

BEGIN
    --Check if the concept passed as input is a valid one.
	IF p_concept <> 'BEDBATHUS' AND p_concept <> 'BUYBUYBABY' AND p_concept <> 'BEDBATHCA' THEN
		RAISE Invalid_Concept;
    END IF;

	-- If the cursonr is not already opened, then open the cursor
    IF NOT cur_omniture_report_data%ISOPEN THEN 
      OPEN cur_omniture_report_data; 
	END IF;
	
	LOOP
		
    FETCH cur_omniture_report_data INTO cur_rec;
		EXIT WHEN cur_omniture_report_data%NOTFOUND;
		INSERT INTO OMNITURE_ARCHIVED_DATA (ID, REPORT_ID, REPORT_SUITE, PERIOD, CONCEPT, BOOST_SCORE, PRODUCT_ID, KEYWORD) VALUES (cur_rec.ID, 
		cur_rec.REPORT_ID, cur_rec.REPORT_SUITE, cur_rec.PERIOD, cur_rec.CONCEPT, cur_rec.BOOST_SCORE, cur_rec.PRODUCT_ID, cur_rec.KEYWORD);
		v_record_moved := v_record_moved + 1;

    END LOOP;
    CLOSE cur_omniture_report_data;
	
	-- Once older records have been copied into OMNITURE_ARCHIVED_DATA, delete those records from BBB_OMNITURE_REPORT_DATA
	DELETE FROM BBB_OMNITURE_REPORT_DATA T WHERE T.REPORT_ID <> p_report_id AND T.CONCEPT = p_concept;
	p_records_moved := v_record_moved;
    
EXCEPTION    
    WHEN Invalid_Concept THEN
            RAISE_APPLICATION_ERROR( -20501, 'Invalid value passed as concept :: ' || p_concept );
    WHEN OTHERS THEN
            RAISE;
        
END ARCHIVE_OMNITURE_DATA; 
  
END OMNITURE_REPORT_DATA_PKG;
/
SHOW ERROR;
COMMIT;