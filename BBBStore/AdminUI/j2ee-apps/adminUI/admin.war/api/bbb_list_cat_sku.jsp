<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
    <c:when test="${empty param._action}">

<form id="bbb_list_cat_sku" name="bbb_list_cat_sku">

	<input type="hidden" bean="?" id="bbb_list_cat_sku__action" name="_action"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_sku_list_cat_id" name="list_cat_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_sku_sku_id" name="sku_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_sku_sequence_num" name="sequence_num"></input>

</form>

    </c:when>
    <c:otherwise>
{
	"status":"ok",
	"data":[
		{
			"list_cat_id":${empty param.list_cat_id?0:param.list_cat_id},
			"list_sku_id":${empty param.sku_id?0:param.sku_id},
			"sequence_num":${empty param.sequence_num?0:param.sequence_num},
			"rule_evaluation_cd":${empty param.rule_evaluation_cd?0:param.rule_evaluation_cd},
			"create_user":"user1",
			"create_date":"01/01/2016",
			"last_mod_user":"user1",
			"last_mod_date":"01/01/2016"
		}
	]

}
    </c:otherwise>
</c:choose>

