<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
    <c:when test="${empty param._action}">

<form id="bbb_list_cat_jda" name="bbb_list_cat_jda">

	<input type="hidden" bean="?" id="bbb_list_cat_jda__action" name="_action"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_rule_id" name="rule_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_list_cat_id" name="list_cat_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_jda_dept_id" name="jda_dept_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_jda_sub_dept_id" name="jda_sub_dept_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_jda_class" name="jda_class"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_facet_rule_id" name="facet_rule_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_facet_id" name="facet_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_facet_value_id" name="facet_value_id"></input>
	<input type="hidden" bean="?" id="bbb_list_cat_jda_sequence_num" name="sequence_num"></input>

</form>

    </c:when>
    <c:otherwise>
{
	"status":"ok",
	"data":[
		{
			"rule_id":${empty param.rule_id?0:param.rule_id},
			"list_cat_id":${empty param.list_cat_id?0:param.list_cat_id},
			"jda_dept_id":${empty param.jda_dept_id?0:param.jda_dept_id},
			"jda_sub_dept_id":${empty param.jda_sub_dept_id?0:param.jda_sub_dept_id},
			"jda_class":${empty param.jda_class?0:param.jda_class},
			"list_facet_rule_id":${empty param.facet_rule_id?0:param.facet_rule_id},
			"list_facet_id":${empty param.facet_id?0:param.facet_id},
			"list_facet_value_id":${empty param.facet_value_id?0:param.facet_value_id},
			"sequence_num":${empty param.sequence_num?0:param.sequence_num},
			"create_user":"user1",
			"create_date":"01/01/2016",
			"last_mod_user":"user1",
			"last_mod_date":"01/01/2016"
		}
	]

}
    </c:otherwise>
</c:choose>

