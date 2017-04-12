CREATE OR REPLACE PROCEDURE bbb_misc.bbb_list_cat_sku_xseq(p_list_cat_id IN VARCHAR2, p_rule_id VARCHAR2, p_sequence_num IN NUMBER, p_cursor OUT SYS_REFCURSOR)
AS

PRAGMA AUTONOMOUS_TRANSACTION;

BEGIN

UPDATE bbb_misc.bbb_list_cat_sku a 
SET sequence_num = (SELECT rn-1 FROM (

SELECT list_cat_id, rule_id, row_number() 
OVER (ORDER BY CASE WHEN list_cat_id = p_list_cat_id AND rule_id = p_rule_id THEN p_sequence_num ELSE sequence_num END, 
CASE WHEN list_cat_id = p_list_cat_id AND rule_id = p_rule_id THEN (SELECT p_sequence_num-sequence_num FROM bbb_misc.bbb_list_cat_sku WHERE list_cat_id = p_list_cat_id AND rule_id = p_rule_id) ELSE 0 END, list_cat_id, rule_id) rn 
FROM bbb_misc.bbb_list_cat_sku
WHERE list_cat_id = p_list_cat_id

) 
WHERE list_cat_id = a.list_cat_id 
AND rule_id = a.rule_id)
WHERE list_cat_id = p_list_cat_id;

COMMIT;

OPEN p_cursor FOR
SELECT list_cat_id, rule_id, sequence_num FROM bbb_misc.bbb_list_cat_sku
WHERE list_cat_id = p_list_cat_id
ORDER BY sequence_num;

END;
/

--variable rc refcursor;
--exec bbb_misc.bbb_list_cat_sku_xseq(1, 51, 2, :rc);
--print rc;

--select * from bbb_misc.bbb_list_cat_sku where list_cat_id = 1 order by sequence_num;