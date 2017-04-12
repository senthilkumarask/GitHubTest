<%@ page session="false" %>

<%@ page language="java" import="java.sql.*,java.io.*,java.util.*,javax.naming.InitialContext,javax.naming.Context,javax.sql.DataSource,javax.naming.NameNotFoundException,com.google.gson.Gson,com.google.gson.GsonBuilder,java.net.*,java.text.DateFormat.*,oracle.jdbc.OracleTypes"%>
<%
	wizardRepository(request, response);
%>
<%!

// Controller
// NOTE: this controller was built as a JSP page rather than a servlet for the following reasons:
// 	 1. Java compiler or IDE not required. notepad or vi only
//       2. Servlet configuration not required. copy deployment
//	 3. Time constraints

public void wizardRepository(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {

	response.setContentType("application/json; charset=utf-8");

	Utility utility = new Utility();
	Json json = new Json();

	Config.DATA_CENTER = "DC3";
	Config.DATA_SOURCE = utility.getCookie(request.getCookies(), "data_source", "MiscDS").getValue();
	Config.EXACT_SEARCH = false;

	Config.BBB_SEQUENCE = "bbb_misc.bbb_sequence";
	Config.BBB_LIST_SEQUENCE = "bbb_misc.bbb_sequence";
	Config.BBB_LIST_TYPE_SEQUENCE = "bbb_misc.bbb_sequence";
	Config.BBB_LIST_CATEGORY_SEQUENCE = "bbb_misc.bbb_sequence";
	Config.BBB_LIST_RULES_SEQUENCE = "bbb_misc.bbb_sequence";
	Config.BBB_LIST_RULES_SKU_SEQUENCE = "bbb_misc.bbb_sequence";

//	if (1+1==2) {
//		response.getWriter().print(json.error(new MessageException(67, "test")));
//		return;
//	}

//	if (1+1==2) {
//		response.getWriter().print("<");
//		return;
//	}

	try {

		DatabaseAdapter dba = new OracleAdapter(Config.DATA_SOURCE);

		String action = utility.NVL(request.getParameter("_action"), "");

		String table = "";
		String method = "";

		try {
			String[] part = action.split("\\.");
			table = part[0];
			method = part[1];
		} catch (Exception e) {}

		if (table.equals("bbb_list")) {

			BBB_LIST repository = new BBB_LIST(dba);
			BBB_LIST.Record record = repository.new Record();

			record.list_id = request.getParameter("list_id");
			record.clone_list_id = request.getParameter("clone_list_id");
			record.type_name = request.getParameter("type_name");
			record.sub_type_code = request.getParameter("sub_type_code");
			record.sub_type_name = request.getParameter("sub_type_name");
		        record.display_name = request.getParameter("display_name");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.is_disabled = utility.parseBoolean(request.getParameter("is_disabled"), false);
			record.allow_duplicates = utility.parseBoolean(request.getParameter("allow_duplicates"), false);
			record.site_flag = request.getParameter("site_flag");
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_category")) {

			BBB_LIST_CATEGORY repository = new BBB_LIST_CATEGORY(dba);
			BBB_LIST_CATEGORY.Record record = repository.new Record();

			record.list_id = request.getParameter("list_id");
			record.parent_list_cat_id = request.getParameter("parent_list_cat_id");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.list_cat_id = request.getParameter("list_cat_id");
			record.name = request.getParameter("name");
			record.category_url = request.getParameter("category_url");
			record.image_url = request.getParameter("image_url");
			record.suggested_qty = request.getParameter("suggested_qty");
			record.primary_parent_cat_id = request.getParameter("primary_parent_cat_id");
			record.primary_parent_cat_name = request.getParameter("primary_parent_cat_name");
		        record.display_name = request.getParameter("display_name");
			record.threshold_qty = request.getParameter("threshold_qty");
			record.threshold_amt = request.getParameter("threshold_amt");
			record.service_type_cd = request.getParameter("service_type_cd");
			record.is_disabled = utility.parseBoolean(request.getParameter("is_disabled"), false);
			record.is_deleted = utility.parseBoolean(request.getParameter("is_deleted"), false);
			record.is_config_complete = utility.parseBoolean(request.getParameter("is_config_complete"), false);
			record.is_visible_on_checklist = utility.parseBoolean(request.getParameter("is_visible_on_checklist"), false);
			record.is_visible_on_reg_list = utility.parseBoolean(request.getParameter("is_visible_on_reg_list"), false);
			record.is_child_prd_needed_to_disp = utility.parseBoolean(request.getParameter("is_child_prd_needed_to_disp"), false);
			record.baby_category_url = request.getParameter("baby_category_url");
			record.baby_image_url = request.getParameter("baby_image_url");
			record.ca_category_url = request.getParameter("ca_category_url");
			record.ca_image_url = request.getParameter("ca_image_url");
			record.tbs_category_url = request.getParameter("tbs_category_url");
			record.tbs_image_url = request.getParameter("tbs_image_url");

			record.mob_category_url = request.getParameter("mob_category_url");
			record.mob_baby_category_url = request.getParameter("mob_baby_category_url");
			record.mob_ca_category_url = request.getParameter("mob_ca_category_url");
			record.mob_image_url = request.getParameter("mob_image_url");
			record.mob_baby_image_url = request.getParameter("mob_baby_image_url");
			record.mob_ca_image_url = request.getParameter("mob_ca_image_url");
			record.tbs_baby_category_url = request.getParameter("tbs_baby_category_url");
			record.tbs_ca_category_url = request.getParameter("tbs_ca_category_url");
			record.tbs_baby_image_url = request.getParameter("tbs_baby_image_url");
			record.tbs_ca_image_url = request.getParameter("tbs_ca_image_url");

			record.url_override = utility.parseBoolean(request.getParameter("url_override"), false); 
			record.baby_url_override = utility.parseBoolean(request.getParameter("baby_url_override"), false);
			record.ca_url_override = utility.parseBoolean(request.getParameter("ca_url_override"), false);
			record.mob_url_override = utility.parseBoolean(request.getParameter("mob_url_override"), false);
			record.mob_baby_url_override = utility.parseBoolean(request.getParameter("mob_baby_url_override"), false);
			record.mob_ca_url_override = utility.parseBoolean(request.getParameter("mob_ca_url_override"), false);
			record.tbs_url_override = utility.parseBoolean(request.getParameter("tbs_url_override"), false);
			record.tbs_baby_url_override = utility.parseBoolean(request.getParameter("tbs_baby_url_override"), false);
			record.tbs_ca_url_override = utility.parseBoolean(request.getParameter("tbs_ca_url_override"), false);

			record.facet_id_pkg_cnt = request.getParameter("facet_id_pkg_cnt");

			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_chldcat")) {

			BBB_LIST_CHLDCAT repository = new BBB_LIST_CHLDCAT(dba);
			BBB_LIST_CHLDCAT.Record record = repository.new Record();

			record.list_id = request.getParameter("list_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_cat_chldcat")) {

			BBB_LIST_CAT_CHLDCAT repository = new BBB_LIST_CAT_CHLDCAT(dba);
			BBB_LIST_CAT_CHLDCAT.Record record = repository.new Record();

			record.list_cat_id = request.getParameter("list_cat_id");
			record.child_list_cat_id = request.getParameter("child_list_cat_id");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_cat_sku")) {

			BBB_LIST_CAT_SKU repository = new BBB_LIST_CAT_SKU(dba);
			BBB_LIST_CAT_SKU.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.sku_id = request.getParameter("sku_id");
			record.rule_evaluation_cd = request.getParameter("rule_evaluation_cd");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}

		else if (table.equals("bbb_list_rules_sku_cat")) {

			BBB_LIST_RULES_SKU_CAT repository = new BBB_LIST_RULES_SKU_CAT(dba);
			BBB_LIST_RULES_SKU_CAT.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.sku_id = request.getParameter("sku_id");
			record.rule_evaluation_cd = request.getParameter("rule_evaluation_cd");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_cat_eph")) {

			BBB_LIST_CAT_EPH repository = new BBB_LIST_CAT_EPH(dba);
			BBB_LIST_CAT_EPH.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.eph_node_id = request.getParameter("eph_node_id");
			record.facet_rule_id = request.getParameter("facet_rule_id");
			record.facet_rule_name = request.getParameter("facet_rule_name");
			record.facet_value_pair_list = request.getParameter("facet_value_pair_list");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_rules_eph_cat")) {

		//	1 = EPH based rules 
		//	2 = JDA based rules
		//	3 = FACET based rules 

			BBB_LIST_RULES_EPH_CAT repository = new BBB_LIST_RULES_EPH_CAT(dba);
			BBB_LIST_RULES_EPH_CAT.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.eph_node_id = request.getParameter("eph_node_id");
			record.rule_type_cd = request.getParameter("rule_type_cd");
			record.facet_rule_name = request.getParameter("facet_rule_name");
			record.facet_value_pair_list = request.getParameter("facet_value_pair_list");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.include_all = utility.parseLong(request.getParameter("include_all"), 0);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_cat_jda")) {

			BBB_LIST_CAT_JDA repository = new BBB_LIST_CAT_JDA(dba);
			BBB_LIST_CAT_JDA.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.jda_class = request.getParameter("jda_class");
			record.facet_rule_id = request.getParameter("facet_rule_id");
			record.facet_rule_name = request.getParameter("facet_rule_name");
			record.facet_value_pair_list = request.getParameter("facet_value_pair_list");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_rules_jda_cat")) {

			BBB_LIST_RULES_JDA_CAT repository = new BBB_LIST_RULES_JDA_CAT(dba);
			BBB_LIST_RULES_JDA_CAT.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.jda_class = request.getParameter("jda_class");
			record.rule_type_cd = request.getParameter("rule_type_cd");
			record.facet_rule_name = request.getParameter("facet_rule_name");
			record.facet_value_pair_list = request.getParameter("facet_value_pair_list");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_cat_facet")) {

			BBB_LIST_CAT_FACET repository = new BBB_LIST_CAT_FACET(dba);
			BBB_LIST_CAT_FACET.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.facet_rule_id = request.getParameter("facet_rule_id");
			record.facet_rule_name = request.getParameter("facet_rule_name");
			record.facet_value_pair_list = request.getParameter("facet_value_pair_list");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_rules_facet_cat")) {

			BBB_LIST_RULES_FACET_CAT repository = new BBB_LIST_RULES_FACET_CAT(dba);
			BBB_LIST_RULES_FACET_CAT.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.list_cat_id = request.getParameter("list_cat_id");
			record.rule_type_cd = request.getParameter("rule_type_cd");
			record.facet_rule_name = request.getParameter("facet_rule_name");
			record.facet_value_pair_list = request.getParameter("facet_value_pair_list");
			record.sequence_num = utility.parseLong(request.getParameter("sequence_num"), -1);
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
           			else if (method.equals("insert")) {
					response.getWriter().print(json.insert(repository.insert(record)));
				}
				else if (method.equals("update")) {
					response.getWriter().print(json.update(repository.update(record)));
				}
				else if (method.equals("delete")) {
					response.getWriter().print(json.delete(repository.delete(record)));
				}
				else if (method.equals("xsequence")) {
					response.getWriter().print(json.xsequence(repository.xsequence(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}	
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_type")) {

			BBB_LIST_TYPE repository = new BBB_LIST_TYPE(dba);
			BBB_LIST_TYPE.Record record = repository.new Record();

			record.type_id = request.getParameter("type_id");
			record.type_name = request.getParameter("type_name");
			record.sub_type_code = request.getParameter("sub_type_code");
			record.sub_type_name = request.getParameter("sub_type_name");
			record.create_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.create_date = null;
			record.last_mod_user = utility.NVL(request.getRemoteUser(), "AdminIU").toUpperCase();
			record.last_mod_date = null;

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("dcs_sku")) {

			DCS_SKU repository = new DCS_SKU(dba);
			DCS_SKU.Record record = repository.new Record();

			record.sku_id = request.getParameter("sku_id");
			record.display_name = request.getParameter("display_name");
			record.xmax = utility.parseLong(request.getParameter("max"), 1024);

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_dept")) {

			BBB_DEPT repository = new BBB_DEPT(dba);
			BBB_DEPT.Record record = repository.new Record();

			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.descrip = request.getParameter("descrip");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_sub_dept")) {

			BBB_SUB_DEPT repository = new BBB_SUB_DEPT(dba);
			BBB_SUB_DEPT.Record record = repository.new Record();

			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.descrip = request.getParameter("descrip");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_class")) {

			BBB_CLASS repository = new BBB_CLASS(dba);
			BBB_CLASS.Record record = repository.new Record();

			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.jda_class = request.getParameter("jda_class");
			record.descrip = request.getParameter("descrip");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_eph_node")) {

			BBB_EPH_NODE repository = new BBB_EPH_NODE(dba);
			BBB_EPH_NODE.Record record = repository.new Record();

			record.parent_node_id = request.getParameter("parent_node_id");
			record.eph_node_id = request.getParameter("eph_node_id");
			record.display_name = request.getParameter("display_name");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_facet_rules")) {

			BBB_FACET_RULES repository = new BBB_FACET_RULES(dba);
			BBB_FACET_RULES.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.facet_rule_name = request.getParameter("facet_rule_name");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_list_rules")) {

			BBB_LIST_RULES repository = new BBB_LIST_RULES(dba);
			BBB_LIST_RULES.Record record = repository.new Record();

			record.rule_id = request.getParameter("rule_id");
			record.rule_type_cd = request.getParameter("rule_type_cd");
			record.eph_node_id = utility.NVL(request.getParameter("eph_node_id"), request.getParameter("eph_id"));
			record.jda_dept_id= request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.jda_class = request.getParameter("jda_class");
			record.facet_rule_name = request.getParameter("facet_rule_name");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_facet_types")) {

			BBB_FACET_TYPES repository = new BBB_FACET_TYPES(dba);
			BBB_FACET_TYPES.Record record = repository.new Record();

			record.eph_node_id = utility.NVL(request.getParameter("eph_node_id"), request.getParameter("eph_id"));
			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.jda_class = request.getParameter("jda_class");
			record.facet_id = request.getParameter("facet_id");
			record.description = request.getParameter("description");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_facet_value_pairs")) {

			BBB_FACET_VALUE_PAIRS repository = new BBB_FACET_VALUE_PAIRS(dba);
			BBB_FACET_VALUE_PAIRS.Record record = repository.new Record();

			record.eph_node_id = utility.NVL(request.getParameter("eph_node_id"), request.getParameter("eph_id"));
			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.jda_class = request.getParameter("jda_class");
			record.facet_id = request.getParameter("facet_id");
			record.facet_value_desc = request.getParameter("facet_value_desc");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_eph_facet_value_pairs")) {

			BBB_EPH_FACET_VALUE_PAIRS repository = new BBB_EPH_FACET_VALUE_PAIRS(dba);
			BBB_EPH_FACET_VALUE_PAIRS.Record record = repository.new Record();

			record.eph_node_id = utility.NVL(request.getParameter("eph_node_id"), request.getParameter("eph_id"));
			record.facet_id = request.getParameter("facet_id");
			record.facet_value_id = request.getParameter("facet_value_id");
			record.facet_value_desc = request.getParameter("facet_value_desc");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
		else if (table.equals("bbb_jda_facet_value_pairs")) {

			BBB_JDA_FACET_VALUE_PAIRS repository = new BBB_JDA_FACET_VALUE_PAIRS(dba);
			BBB_JDA_FACET_VALUE_PAIRS.Record record = repository.new Record();

			record.jda_dept_id = request.getParameter("jda_dept_id");
			record.jda_sub_dept_id = request.getParameter("jda_sub_dept_id");
			record.jda_class = request.getParameter("jda_class");
			record.facet_id = request.getParameter("facet_id");
			record.facet_value_id = request.getParameter("facet_value_id");
			record.facet_value_desc = request.getParameter("facet_value_desc");

			try {
				if (method.equals("select")) {
					response.getWriter().print(json.select(repository.select(record)));
				}
				else {
		     			throw new MessageException(-3, "unknown method");
				}
			} catch(MessageException me) {
				response.getWriter().print(json.error(me));
			}
		}
            	else {
        		response.getWriter().print(json.error(new Message(-2, "unknown table")));
        	}

	} catch (Exception e) {

		response.getWriter().print(json.error(e));
	}
}

interface DatabaseAdapter {

	Connection getConnection() throws Exception;
	Connection getConnection(String jndi) throws Exception;
}

interface DataRepository {

	ArrayList select(Object record) throws Exception;
}

public class Message {

	public Message(int id, String description) {
		this.id = id;
		this.description = description;
	}
	public int id;
	public String description;
}

class MessageException extends Exception {

	public MessageException(int id, String message)
	{
    		super(message);
		this.id = id;
		this.description = message;
	}
	public int id;
	public String description;
}

public class Json {

	public String error(Exception e) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("status", "error");

		jsonMap.put("message", new Message(-1, e.getMessage()));

		return gson.toJson(jsonMap);
	}

	public String error(MessageException e) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("status", "error");

		jsonMap.put("message", new Message(e.id, e.description));

		return gson.toJson(jsonMap);
	}

	public String error(Message message) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("status", "error");

		jsonMap.put("message", message);

		return gson.toJson(jsonMap);
	}

	public String select(ArrayList list) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("status", "ok");

		jsonMap.put("data", list);

		return gson.toJson(jsonMap);
	}

	public String insert(ArrayList list) {

		return select(list);
	}

	public String update(ArrayList list) {

		return select(list);
	}

	public String delete(ArrayList list) {

		return select(list);
	}

	public String xsequence(ArrayList list) {

		return select(list);
	}

	public String OK() {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("status", "ok");

		return gson.toJson(jsonMap);
	}
}

public class OracleAdapter implements DatabaseAdapter {

	private String jndi = null;

	public OracleAdapter(String jndi) {
		
		this.jndi = jndi;

	}

	public Connection getConnection() throws Exception {

		return getConnection(jndi);
	}

	public Connection getConnection(String jndi) throws Exception {

		Connection conn = null;
				  
		try {
			DataSource ds = null;

			InitialContext initCtx = new InitialContext();

			try {
				ds = (DataSource)initCtx.lookup(jndi);	
	
			} catch (NameNotFoundException e) {
			
				ds = (DataSource)initCtx.lookup("java:comp/env/jdbc/" + jndi);	
			}
																	
			conn = ds.getConnection();

		} catch (Exception e) {

			throw e;
		}

		return conn;
	}
}

public class BBB_LIST implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = "SELECT " + 
			     	"a.list_id, " + 
			     	"b.type_name, " + 
			     	"a.sub_type_code,  " +
                             	"b.sub_type_name,  " +
			     	"a.display_name,  " +
				"a.sequence_num, " + 
				"a.is_disabled, " +  
				"a.allow_duplicates, " + 
				"a.site_flag, " + 
				"a.create_user, " + 
				"a.create_date, " + 
				"a.last_mod_user, " + 
				"a.last_mod_date " +
		"FROM bbb_misc.bbb_list a, bbb_misc.bbb_list_type b WHERE a.sub_type_code = b.sub_type_code "; 

		if (!utility.isBlank(rec.list_id)) {

			sql += " AND list_id = ? ";
		}
		else if (!utility.isBlank(rec.site_flag)) {

			sql += " AND site_flag LIKE ? ";
		}

		sql += " ORDER BY sequence_num ";

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isBlank(rec.list_id)) {
				stmt.setString(1, rec.list_id);
			}
			else if (!utility.isBlank(rec.site_flag)) {
				stmt.setString(1, "%" + rec.site_flag + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.list_id = rs.getString("list_id");
				xrec.type_name = rs.getString("type_name");
				xrec.sub_type_code = rs.getString("sub_type_code");
				xrec.sub_type_name = rs.getString("sub_type_name");
				xrec.display_name = rs.getString("display_name");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.is_disabled = utility.parseBoolean(rs.getString("is_disabled"), false);
				xrec.allow_duplicates = utility.parseBoolean(rs.getString("allow_duplicates"), false);
				xrec.site_flag = rs.getString("site_flag");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql = "INSERT INTO bbb_misc.bbb_list( " +
				"list_id,  " +
			     	"sub_type_code,  " +
			     	"display_name,  " +
				"is_disabled, " +  
				"allow_duplicates, " + 
				"site_flag, " + 
				"sequence_num, " + 
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, ?, ?, ?, 2, ?, SYSDATE, ?, SYSDATE FROM DUAL ";

		if (sql != "") {

		//	throw new MessageException(1, sql);
		}

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(Config.getNextSequence(Config.BBB_LIST_SEQUENCE));

			if (rs1.next()) {

				rec.list_id = rs1.getString(1);
			}

			stmt2 = conn.prepareStatement(sql);

			stmt2.setString(1, rec.list_id);
			stmt2.setString(2, rec.sub_type_code);
			stmt2.setString(3, rec.display_name);
			stmt2.setString(4, (rec.is_disabled?"1":"0"));
			stmt2.setString(5, (rec.allow_duplicates?"1":"0"));
			stmt2.setString(6, rec.site_flag);
			stmt2.setString(7, rec.create_user);
			stmt2.setString(8, rec.last_mod_user);

			stmt2.executeUpdate();

			// clone functionality
			if (!utility.isBlank(rec.clone_list_id)) {

				sql = "INSERT INTO bbb_misc.bbb_list_chldcat ( " +
				"list_id,  " +
			     	"list_cat_id,  " +
				"sequence_num) " + 
		 		"SELECT ?, list_cat_id, sequence_num FROM bbb_misc.bbb_list_chldcat WHERE list_id = ? ";
					
				stmt3 = conn.prepareStatement(sql);

				stmt3.setString(1, rec.list_id);
				stmt3.setString(2, rec.clone_list_id);

				stmt3.executeUpdate();
			}

		} catch (Exception e) {

			int icode = -1;
			String message = e.getMessage();

			switch(utility.split(message, "\\:", 0)) {

				case "ORA-00001":
					icode = 4;
					message = "List type already exists";
					break;
				default:
					break;		
			}
			throw new MessageException(icode, message);
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list SET " +
			     	"sub_type_code = ?,  " +
			     	"display_name = ?,  " +
				"is_disabled = ?, " +  
				"allow_duplicates = ?, " + 
				"site_flag = ?, " + 
				"last_mod_user = ?, " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE list_id = ? ";
		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.sub_type_code);
			stmt1.setString(2, rec.display_name);
			stmt1.setString(3, (rec.is_disabled?"1":"0"));
			stmt1.setString(4, (rec.allow_duplicates?"1":"0"));
			stmt1.setString(5, rec.site_flag);
			stmt1.setString(6, rec.last_mod_user);
			stmt1.setString(7, rec.list_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			int icode = -1;
			String message = e.getMessage();

			switch(utility.split(message, "\\:", 0)) {

				case "ORA-00001":
					icode = 4;
					message = "List type already exists";
					break;
				default:
					break;		
			}
			throw new MessageException(icode, message);
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "DELETE FROM bbb_misc.bbb_list WHERE list_id = ?";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = 	"UPDATE bbb_misc.bbb_list a " + 
				"SET sequence_num = (SELECT rn-1 FROM ( " +
				"SELECT list_id, row_number() " + 
				"OVER (ORDER BY CASE WHEN list_id = ? THEN ? ELSE sequence_num END, " + 
				"CASE WHEN list_id = ? THEN (SELECT ?-sequence_num FROM bbb_misc.bbb_list WHERE list_id = ?) ELSE 0 END, list_id) rn " + 
				"FROM bbb_misc.bbb_list) " + 
				"WHERE list_id = a.list_id) ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_id);
			stmt1.setLong(2, rec.sequence_num);
			stmt1.setString(3, rec.list_id);
			stmt1.setLong(4, rec.sequence_num);
			stmt1.setString(5, rec.list_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence2(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_xseq(?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_id);
			stmt1.setLong(2, rec.sequence_num);
			stmt1.registerOutParameter(3, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(3);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String list_id;
		public String clone_list_id;
		public String type_name;
		public String sub_type_code;
		public String sub_type_name;
		public String display_name;
		public long sequence_num;
		public boolean is_disabled;
		public boolean allow_duplicates;
		public String site_flag;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}
}

public class BBB_LIST_CATEGORY implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_CATEGORY(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = "SELECT " + 
		//	"a.list_id, " +
		//	"a.parent_cat_id, " +
			"a.list_cat_id, " +
			"a.name, " +
			"a.category_url, " +
			"a.image_url, " +
			"a.suggested_qty, " +
			"a.primary_parent_cat_id, " +
			"(SELECT display_name FROM bbb_misc.bbb_list_category b WHERE list_cat_id = a.primary_parent_cat_id) primary_parent_cat_name, " +
			"a.display_name, " +
			"a.threshold_qty, " +
			"a.threshold_amt, " +
			"a.service_type_cd, " +
			"a.is_disabled, " +
			"a.is_deleted, " +
			"a.is_config_complete, " +
			"a.is_visible_on_checklist, " +
			"a.is_visible_on_reg_list, " +
			"a.is_child_prd_needed_to_disp, " +
			"a.baby_category_url, " +
			"a.baby_image_url, " +
			"a.ca_category_url, " +
			"a.ca_image_url, " +
			"a.tbs_category_url, " +
			"a.tbs_image_url, " +
			"a.mob_category_url, " +
			"a.mob_baby_category_url, " +
			"a.mob_ca_category_url, " +
			"a.mob_image_url, " +
			"a.mob_baby_image_url, " +
			"a.mob_ca_image_url, " +
			"a.tbs_baby_category_url, " +
			"a.tbs_ca_category_url, " +
			"a.tbs_baby_image_url, " +
			"a.tbs_ca_image_url, " +
			"a.url_override, " + 
			"a.baby_url_override, " +
			"a.ca_url_override, " +
			"a.mob_url_override, " +
			"a.mob_baby_url_override, " +
			"a.mob_ca_url_override, " +
			"a.tbs_url_override, " +
			"a.tbs_baby_url_override, " +
			"a.tbs_ca_url_override, " +
			"a.facet_id_pkg_cnt, " +
			"(SELECT description FROM bbb_core.bbb_facet_types b WHERE facet_id = a.facet_id_pkg_cnt) facet_id_pkg_cnt_description, " +
			"a.create_user, " +
			"a.create_date, " +
			"a.last_mod_user, " +
			"a.last_mod_date " +
		"FROM bbb_misc.bbb_list_category a WHERE 1 = 1 "; 

		rec.list_cat_id = Config.exactSearch(rec.list_cat_id);

		if (!utility.isNullOrEmpty(rec.list_cat_id) && rec.list_cat_id.indexOf("%") != -1) {

			sql += " AND list_cat_id LIKE ? ";
			sql += " ORDER BY list_cat_id ";
		}
		else if (!utility.isNullOrEmpty(rec.list_cat_id)) {

			sql += " AND list_cat_id = ? ";
			sql += " ORDER BY list_cat_id ";
		}
		else if (!utility.isNullOrEmpty(rec.display_name)) {

			sql += " AND UPPER(display_name) LIKE ? ";
			sql += " ORDER BY display_name ";
		}

		if (sql != "") {

		//	throw new MessageException(1, sql);
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isNullOrEmpty(rec.list_cat_id)) {
				stmt.setString(1, utility.ltrim(rec.list_cat_id.toUpperCase().trim()));
			}
			else if (!utility.isNullOrEmpty(rec.display_name)) {
				stmt.setString(1, rec.display_name.toUpperCase().trim() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				Record xrec = new Record();

				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.name = rs.getString("name");
				xrec.category_url = rs.getString("category_url");
				xrec.image_url = rs.getString("image_url");
				xrec.suggested_qty = rs.getString("suggested_qty");
				xrec.primary_parent_cat_id = rs.getString("primary_parent_cat_id");
				xrec.primary_parent_cat_name = rs.getString("primary_parent_cat_name");
				xrec.display_name = rs.getString("display_name");
				xrec.threshold_qty = rs.getString("threshold_qty");
				xrec.threshold_amt = rs.getString("threshold_amt");
				xrec.service_type_cd = rs.getString("service_type_cd");
				xrec.is_disabled = utility.parseBoolean(rs.getString("is_disabled"), false);
				xrec.is_deleted = utility.parseBoolean(rs.getString("is_deleted"), false);
				xrec.is_config_complete = utility.parseBoolean(rs.getString("is_config_complete"), false);
				xrec.is_visible_on_checklist = utility.parseBoolean(rs.getString("is_visible_on_checklist"), false);
				xrec.is_visible_on_reg_list = utility.parseBoolean(rs.getString("is_visible_on_reg_list"), false);
				xrec.is_child_prd_needed_to_disp = utility.parseBoolean(rs.getString("is_child_prd_needed_to_disp"), false);
				xrec.baby_category_url = rs.getString("baby_category_url");
				xrec.baby_image_url = rs.getString("baby_image_url");
				xrec.ca_category_url = rs.getString("ca_category_url");
				xrec.ca_image_url = rs.getString("ca_image_url");
				xrec.tbs_category_url = rs.getString("tbs_category_url");
				xrec.tbs_image_url = rs.getString("tbs_image_url");
				xrec.mob_category_url = rs.getString("mob_category_url");
				xrec.mob_baby_category_url = rs.getString("mob_baby_category_url");
				xrec.mob_ca_category_url = rs.getString("mob_ca_category_url");
				xrec.mob_image_url = rs.getString("mob_image_url");
				xrec.mob_baby_image_url = rs.getString("mob_baby_image_url");
				xrec.mob_ca_image_url = rs.getString("mob_ca_image_url");
				xrec.tbs_baby_category_url = rs.getString("tbs_baby_category_url");
				xrec.tbs_ca_category_url = rs.getString("tbs_ca_category_url");
				xrec.tbs_baby_image_url = rs.getString("tbs_baby_image_url");
				xrec.tbs_ca_image_url = rs.getString("tbs_ca_image_url");
				xrec.url_override = utility.parseBoolean(rs.getString("url_override"), false); 
				xrec.baby_url_override = utility.parseBoolean(rs.getString("baby_url_override"), false);
				xrec.ca_url_override = utility.parseBoolean(rs.getString("ca_url_override"), false);
				xrec.mob_url_override = utility.parseBoolean(rs.getString("mob_url_override"), false);
				xrec.mob_baby_url_override = utility.parseBoolean(rs.getString("mob_baby_url_override"), false);
				xrec.mob_ca_url_override = utility.parseBoolean(rs.getString("mob_ca_url_override"), false);
				xrec.tbs_url_override = utility.parseBoolean(rs.getString("tbs_url_override"), false);
				xrec.tbs_baby_url_override = utility.parseBoolean(rs.getString("tbs_baby_url_override"), false);
				xrec.tbs_ca_url_override = utility.parseBoolean(rs.getString("tbs_ca_url_override"), false);
				xrec.facet_id_pkg_cnt = rs.getString("facet_id_pkg_cnt");
				xrec.facet_id_pkg_cnt_description = rs.getString("facet_id_pkg_cnt_description");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.name)) {

			throw new MessageException(1000, "invalid name");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql = "INSERT INTO bbb_misc.bbb_list_category ( " +

				"list_cat_id, " + 
				"name, " + 
				"category_url, " + 
				"image_url, " + 
				"suggested_qty, " + 
				"primary_parent_cat_id, " + 
				"display_name, " + 
				"threshold_qty, " + 
				"threshold_amt, " + 
				"service_type_cd, " + 
				"is_disabled, " + 
				"is_deleted, " + 
				"is_config_complete, " + 
				"is_visible_on_checklist, " + 
				"is_visible_on_reg_list, " + 
				"is_child_prd_needed_to_disp, " + 
				"baby_category_url, " + 
				"baby_image_url, " + 
				"ca_category_url, " + 
				"ca_image_url, " + 
				"tbs_category_url, " + 
				"tbs_image_url, " + 
				"mob_category_url, " +
				"mob_baby_category_url, " +
				"mob_ca_category_url, " +
				"mob_image_url, " +
				"mob_baby_image_url, " +
				"mob_ca_image_url, " +
				"tbs_baby_category_url, " +
				"tbs_ca_category_url, " +
				"tbs_baby_image_url, " +
				"tbs_ca_image_url, " +
				"url_override, " + 
				"baby_url_override, " +
				"ca_url_override, " +
				"mob_url_override, " +
				"mob_baby_url_override, " +
				"mob_ca_url_override, " +
				"tbs_url_override, " +
				"tbs_baby_url_override, " +
				"tbs_ca_url_override, " +
				"facet_id_pkg_cnt, " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, SYSDATE, ?, SYSDATE FROM DUAL ";

		if (sql != "") {

		//	throw new MessageException(1, sql);
		}

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(Config.getNextSequence(Config.BBB_LIST_CATEGORY_SEQUENCE));

			if (rs1.next()) {

				rec.list_cat_id = rs1.getString(1);
			}

			stmt2 = conn.prepareStatement(sql);

			stmt2.setString(1, rec.list_cat_id);
			stmt2.setString(2, rec.name);
			stmt2.setString(3, rec.category_url);
			stmt2.setString(4, rec.image_url);
			stmt2.setString(5, rec.suggested_qty);
			stmt2.setString(6, rec.primary_parent_cat_id);
			stmt2.setString(7, rec.display_name);
			stmt2.setString(8, rec.threshold_qty);
			stmt2.setString(9, rec.threshold_amt);
			stmt2.setString(10, rec.service_type_cd);
			stmt2.setString(11, (rec.is_disabled?"1":"0"));
			stmt2.setString(12, (rec.is_deleted?"1":"0"));
			stmt2.setString(13, (rec.is_config_complete?"1":"0"));
			stmt2.setString(14, (rec.is_visible_on_checklist?"1":"0"));
			stmt2.setString(15, (rec.is_visible_on_reg_list?"1":"0"));
			stmt2.setString(16, (rec.is_child_prd_needed_to_disp?"1":"0"));
			stmt2.setString(17, rec.baby_category_url);
			stmt2.setString(18, rec.baby_image_url);
			stmt2.setString(19, rec.ca_category_url);
			stmt2.setString(20, rec.ca_image_url);
			stmt2.setString(21, rec.tbs_category_url);
			stmt2.setString(22, rec.tbs_image_url);
			stmt2.setString(23, rec.mob_category_url);
			stmt2.setString(24, rec.mob_baby_category_url);
			stmt2.setString(25, rec.mob_ca_category_url);
			stmt2.setString(26, rec.mob_image_url);
			stmt2.setString(27, rec.mob_baby_image_url);
			stmt2.setString(28, rec.mob_ca_image_url);
			stmt2.setString(29, rec.tbs_baby_category_url);
			stmt2.setString(30, rec.tbs_ca_category_url);
			stmt2.setString(31, rec.tbs_baby_image_url);
			stmt2.setString(32, rec.tbs_ca_image_url);
			stmt2.setString(33, (rec.url_override?"1":"0")); 
			stmt2.setString(34, (rec.baby_url_override?"1":"0"));
			stmt2.setString(35, (rec.ca_url_override?"1":"0"));
			stmt2.setString(36, (rec.mob_url_override?"1":"0"));
			stmt2.setString(37, (rec.mob_baby_url_override?"1":"0"));
			stmt2.setString(38, (rec.mob_ca_url_override?"1":"0"));
			stmt2.setString(39, (rec.tbs_url_override?"1":"0"));
			stmt2.setString(40, (rec.tbs_baby_url_override?"1":"0"));
			stmt2.setString(41, (rec.tbs_ca_url_override?"1":"0"));
			stmt2.setString(42, rec.facet_id_pkg_cnt);
			stmt2.setString(43, rec.create_user);
			stmt2.setString(44, rec.last_mod_user);

			stmt2.executeUpdate();


		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// add the category to a parent available
		if (!utility.isBlank(rec.list_id)) {

			BBB_LIST_CHLDCAT repository = new BBB_LIST_CHLDCAT(dba);
			BBB_LIST_CHLDCAT.Record xrec = repository.new Record();
			xrec.list_id = rec.list_id;

			xrec.list_cat_id = rec.list_cat_id;
			xrec.sequence_num = rec.sequence_num;
			repository.insert(xrec);
		}
		else if (!utility.isBlank(rec.parent_list_cat_id)) {

			BBB_LIST_CAT_CHLDCAT repository = new BBB_LIST_CAT_CHLDCAT(dba);
			BBB_LIST_CAT_CHLDCAT.Record xrec = repository.new Record();
			xrec.list_cat_id = rec.parent_list_cat_id;

			xrec.child_list_cat_id = rec.list_cat_id;
			xrec.sequence_num = rec.sequence_num;
			repository.insert(xrec);
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_category SET " +
				"name = ?,  " +
				"category_url = ?,  " +
				"image_url = ?,  " +
				"suggested_qty = ?,  " +
				"primary_parent_cat_id = ?,  " +
				"display_name = ?,  " +
				"threshold_qty = ?,  " +
				"threshold_amt = ?,  " +
				"service_type_cd = ?,  " +
				"is_disabled = ?,  " +
				"is_config_complete = ?,  " +
				"is_visible_on_checklist = ?,  " +
				"is_visible_on_reg_list = ?,  " +
				"is_child_prd_needed_to_disp = ?,  " +
				"baby_category_url = ?,  " +
				"baby_image_url = ?,  " +
				"ca_category_url = ?,  " +
				"ca_image_url = ?,  " +
				"tbs_category_url = ?,  " +
				"tbs_image_url = ?,  " +
				"mob_category_url = ?,  " +
				"mob_baby_category_url = ?,  " +
				"mob_ca_category_url = ?,  " +
				"mob_image_url = ?,  " +
				"mob_baby_image_url = ?,  " +
				"mob_ca_image_url = ?,  " +
				"tbs_baby_category_url = ?,  " +
				"tbs_ca_category_url = ?,  " +
				"tbs_baby_image_url = ?,  " +
				"tbs_ca_image_url = ?,  " +
				"url_override = ?, " + 
				"baby_url_override = ?, " +
				"ca_url_override = ?, " +
				"mob_url_override = ?, " +
				"mob_baby_url_override = ?, " +
				"mob_ca_url_override = ?, " +
				"tbs_url_override = ?, " +
				"tbs_baby_url_override = ?, " +
				"tbs_ca_url_override = ?, " +
				"facet_id_pkg_cnt = ?, " +
				"last_mod_user = ?, " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);

			stmt1.setString(1, rec.name);
			stmt1.setString(2, rec.category_url);
			stmt1.setString(3, rec.image_url);
			stmt1.setString(4, rec.suggested_qty);
			stmt1.setString(5, rec.primary_parent_cat_id);
			stmt1.setString(6, rec.display_name);
			stmt1.setString(7, rec.threshold_qty);
			stmt1.setString(8, rec.threshold_amt);
			stmt1.setString(9, rec.service_type_cd);
			stmt1.setString(10, (rec.is_disabled?"1":"0"));
			stmt1.setString(11, (rec.is_config_complete?"1":"0"));
			stmt1.setString(12, (rec.is_visible_on_checklist?"1":"0"));
			stmt1.setString(13, (rec.is_visible_on_reg_list?"1":"0"));
			stmt1.setString(14, (rec.is_child_prd_needed_to_disp?"1":"0"));
			stmt1.setString(15, rec.baby_category_url);
			stmt1.setString(16, rec.baby_image_url);
			stmt1.setString(17, rec.ca_category_url);
			stmt1.setString(18, rec.ca_image_url);
			stmt1.setString(19, rec.tbs_category_url);
			stmt1.setString(20, rec.tbs_image_url);
			stmt1.setString(21, rec.mob_category_url);
			stmt1.setString(22, rec.mob_baby_category_url);
			stmt1.setString(23, rec.mob_ca_category_url);
			stmt1.setString(24, rec.mob_image_url);
			stmt1.setString(25, rec.mob_baby_image_url);
			stmt1.setString(26, rec.mob_ca_image_url);
			stmt1.setString(27, rec.tbs_baby_category_url);
			stmt1.setString(28, rec.tbs_ca_category_url);
			stmt1.setString(29, rec.tbs_baby_image_url);
			stmt1.setString(30, rec.tbs_ca_image_url);
			stmt1.setString(31, (rec.url_override?"1":"0")); 
			stmt1.setString(32, (rec.baby_url_override?"1":"0"));
			stmt1.setString(33, (rec.ca_url_override?"1":"0"));
			stmt1.setString(34, (rec.mob_url_override?"1":"0"));
			stmt1.setString(35, (rec.mob_baby_url_override?"1":"0"));
			stmt1.setString(36, (rec.mob_ca_url_override?"1":"0"));
			stmt1.setString(37, (rec.tbs_url_override?"1":"0"));
			stmt1.setString(38, (rec.tbs_baby_url_override?"1":"0"));
			stmt1.setString(39, (rec.tbs_ca_url_override?"1":"0"));
			stmt1.setString(40, rec.facet_id_pkg_cnt);
			stmt1.setString(41, rec.last_mod_user);
			stmt1.setString(42, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_category SET is_deleted = 1 WHERE list_cat_id = ?";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String list_id;
		public String list_cat_id;
		public String parent_list_cat_id;
		public String child_list_cat_id;
		public long sequence_num;
		public String name;
		public String category_url;
		public String image_url;
		public String suggested_qty;
		public String primary_parent_cat_id;
		public String primary_parent_cat_name;
		public String display_name;
		public String threshold_qty;
		public String threshold_amt;
		public String service_type_cd;
		public boolean is_disabled;
		public boolean is_deleted;
		public boolean is_config_complete;
		public boolean is_visible_on_checklist;
		public boolean is_visible_on_reg_list;
		public boolean is_child_prd_needed_to_disp;
		public String baby_category_url;
		public String baby_image_url;
		public String ca_category_url;
		public String ca_image_url;
		public String tbs_category_url;
		public String tbs_image_url;
		public String mob_category_url;
		public String mob_baby_category_url;
		public String mob_ca_category_url;
		public String mob_image_url;
		public String mob_baby_image_url;
		public String mob_ca_image_url;
		public String tbs_baby_category_url;
		public String tbs_ca_category_url;
		public String tbs_baby_image_url;
		public String tbs_ca_image_url;
		public boolean url_override; 
		public boolean baby_url_override;
		public boolean ca_url_override;
		public boolean mob_url_override;
		public boolean mob_baby_url_override;
		public boolean mob_ca_url_override;
		public boolean tbs_url_override;
		public boolean tbs_baby_url_override;
		public boolean tbs_ca_url_override;
		public long eph;
		public long jda;
		public long facet;
		public String facet_id_pkg_cnt;
		public String facet_id_pkg_cnt_description;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}
}

public class BBB_LIST_CHLDCAT implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_CHLDCAT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		String sql = "SELECT " + 
			"a.list_id, " +
			"a.list_cat_id, " +
			"a.sequence_num, " +
			"b.name, " +
			"b.category_url, " +
			"b.image_url, " +
			"b.suggested_qty, " +
			"b.primary_parent_cat_id, " +
			"(SELECT display_name FROM bbb_misc.bbb_list_category c WHERE list_cat_id = b.primary_parent_cat_id) primary_parent_cat_name, " +
			"b.display_name, " +
			"b.threshold_qty, " +
			"b.threshold_amt, " +
			"b.service_type_cd, " +
			"b.is_disabled, " +
			"b.is_deleted, " +
			"b.is_config_complete, " +
			"b.is_visible_on_checklist, " +
			"b.is_visible_on_reg_list, " +
			"b.is_child_prd_needed_to_disp, " +
			"b.baby_category_url, " +
			"b.baby_image_url, " +
			"b.ca_category_url, " +
			"b.ca_image_url, " +
			"b.tbs_category_url, " +
			"b.tbs_image_url, " +
		//	"(SELECT count(*) FROM bbb_misc.bbb_list_cat_eph WHERE a.list_cat_id = list_cat_id) eph, " +
		//	"(SELECT count(*) FROM bbb_misc.bbb_list_cat_jda WHERE a.list_cat_id = list_cat_id) jda, " +
		//	"(SELECT count(*) FROM bbb_misc.bbb_list_cat_facet WHERE a.list_cat_id = list_cat_id) facet " +
			"(SELECT count(*) FROM bbb_misc.bbb_list_rules_eph_cat WHERE a.list_cat_id = list_cat_id) eph, " +
			"(SELECT count(*) FROM bbb_misc.bbb_list_rules_jda_cat WHERE a.list_cat_id = list_cat_id) jda, " +
			"(SELECT count(*) FROM bbb_misc.bbb_list_rules_facet_cat WHERE a.list_cat_id = list_cat_id) facet, " +
			"b.create_user, " +
			"b.create_date, " +
			"b.last_mod_user, " +
			"b.last_mod_date " +
		"FROM bbb_misc.bbb_list_chldcat a, bbb_misc.bbb_list_category b WHERE a.list_cat_id = b.list_cat_id AND a.list_id = ? ORDER BY a.sequence_num "; 

		if (sql != "") {

		//	throw new MessageException(1, sql);
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.list_id = rs.getString("list_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.name = rs.getString("name");
				xrec.category_url = rs.getString("category_url");
				xrec.image_url = rs.getString("image_url");
				xrec.suggested_qty = rs.getString("suggested_qty");
				xrec.primary_parent_cat_id = rs.getString("primary_parent_cat_id");
				xrec.primary_parent_cat_name = rs.getString("primary_parent_cat_name");
				xrec.display_name = rs.getString("display_name");
				xrec.threshold_qty = rs.getString("threshold_qty");
				xrec.threshold_amt = rs.getString("threshold_amt");
				xrec.service_type_cd = rs.getString("service_type_cd");
				xrec.is_disabled = utility.parseBoolean(rs.getString("is_disabled"), false);
				xrec.is_deleted = utility.parseBoolean(rs.getString("is_deleted"), false);
				xrec.is_config_complete = utility.parseBoolean(rs.getString("is_config_complete"), false);
				xrec.is_visible_on_checklist = utility.parseBoolean(rs.getString("is_visible_on_checklist"), false);
				xrec.is_visible_on_reg_list = utility.parseBoolean(rs.getString("is_visible_on_reg_list"), false);
				xrec.is_child_prd_needed_to_disp = utility.parseBoolean(rs.getString("is_child_prd_needed_to_disp"), false);
				xrec.baby_category_url = rs.getString("baby_category_url");
				xrec.baby_image_url = rs.getString("baby_image_url");
				xrec.ca_category_url = rs.getString("ca_category_url");
				xrec.ca_image_url = rs.getString("ca_image_url");
				xrec.tbs_category_url = rs.getString("tbs_category_url");
				xrec.tbs_image_url = rs.getString("tbs_image_url");
				xrec.eph = rs.getLong("eph");
				xrec.jda = rs.getLong("jda");
				xrec.facet = rs.getLong("facet");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "INSERT INTO bbb_misc.bbb_list_chldcat ( " +
				"list_id,  " +
			     	"list_cat_id,  " +
				"sequence_num) " + 
		 		"SELECT ?, ?, 2 FROM DUAL ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_id);
			stmt1.setString(2, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "DELETE FROM bbb_misc.bbb_list_chldcat WHERE list_id = ? AND list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_id);
			stmt1.setString(2, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = 	"UPDATE bbb_misc.bbb_list_chldcat a " + 
				"SET sequence_num = (SELECT rn-1 FROM ( " +
				"SELECT list_id, list_cat_id, row_number() " + 
				"OVER (ORDER BY CASE WHEN list_id = ? AND list_cat_id = ? THEN ? ELSE sequence_num END, " + 
				"CASE WHEN list_id = ? AND list_cat_id = ? THEN (SELECT ?-sequence_num FROM bbb_misc.bbb_list_chldcat WHERE list_id = ? AND list_cat_id = ?) ELSE 0 END, list_id, list_cat_id) rn " + 
				"FROM bbb_misc.bbb_list_chldcat " +
				"WHERE list_id = ?) " + 
				"WHERE list_id = a.list_id " + 
				"AND list_cat_id = a.list_cat_id) " +
				"WHERE list_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_id);
			stmt1.setString(2, rec.list_cat_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.setString(4, rec.list_id);
			stmt1.setString(5, rec.list_cat_id);
			stmt1.setLong(6, rec.sequence_num);
			stmt1.setString(7, rec.list_id);
			stmt1.setString(8, rec.list_cat_id);
			stmt1.setString(9, rec.list_id);
			stmt1.setString(10, rec.list_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence2(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_id)) {

			throw new MessageException(1000, "invalid list_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_chldcat_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_id);
			stmt1.setString(2, rec.list_cat_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String list_id;
		public String list_cat_id;
		public String parent_list_cat_id;
		public String child_list_cat_id;
		public long sequence_num;
		public String name;
		public String category_url;
		public String image_url;
		public String suggested_qty;
		public String primary_parent_cat_id;
		public String primary_parent_cat_name;
		public String display_name;
		public String threshold_qty;
		public String threshold_amt;
		public String service_type_cd;
		public boolean is_disabled;
		public boolean is_deleted;
		public boolean is_config_complete;
		public boolean is_visible_on_checklist;
		public boolean is_visible_on_reg_list;
		public boolean is_child_prd_needed_to_disp;
		public String baby_category_url;
		public String baby_image_url;
		public String ca_category_url;
		public String ca_image_url;
		public String tbs_category_url;
		public String tbs_image_url;
		public long eph;
		public long jda;
		public long facet;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}
}

public class BBB_LIST_CAT_CHLDCAT implements DataRepository  {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_CAT_CHLDCAT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		String sql = "SELECT " + 
			"a.list_cat_id, " +
			"a.child_list_cat_id, " +
			"a.sequence_num, " +
			"b.name, " +
			"b.category_url, " +
			"b.image_url, " +
			"b.suggested_qty, " +
			"b.primary_parent_cat_id, " +
			"(SELECT display_name FROM bbb_misc.bbb_list_category c WHERE list_cat_id = b.primary_parent_cat_id) primary_parent_cat_name, " +
			"b.display_name, " +
			"b.threshold_qty, " +
			"b.threshold_amt, " +
			"b.service_type_cd, " +
			"b.is_disabled, " +
			"b.is_deleted, " +
			"b.is_config_complete, " +
			"b.is_visible_on_checklist, " +
			"b.is_visible_on_reg_list, " +
			"b.is_child_prd_needed_to_disp, " +
			"b.baby_category_url, " +
			"b.baby_image_url, " +
			"b.ca_category_url, " +
			"b.ca_image_url, " +
			"b.tbs_category_url, " +
			"b.tbs_image_url, " +
		//	"(SELECT count(*) FROM bbb_misc.bbb_list_cat_eph WHERE a.list_cat_id = list_cat_id) eph, " +
		//	"(SELECT count(*) FROM bbb_misc.bbb_list_cat_jda WHERE a.list_cat_id = list_cat_id) jda, " +
		//	"(SELECT count(*) FROM bbb_misc.bbb_list_cat_facet WHERE a.list_cat_id = list_cat_id) facet " +
			"(SELECT count(*) FROM bbb_misc.bbb_list_rules_eph_cat WHERE a.list_cat_id = list_cat_id) eph, " +
			"(SELECT count(*) FROM bbb_misc.bbb_list_rules_jda_cat WHERE a.list_cat_id = list_cat_id) jda, " +
			"(SELECT count(*) FROM bbb_misc.bbb_list_rules_facet_cat WHERE a.list_cat_id = list_cat_id) facet, " +
			"b.create_user, " +
			"b.create_date, " +
			"b.last_mod_user, " +
			"b.last_mod_date " +
		"FROM bbb_misc.bbb_list_cat_chldcat a, bbb_misc.bbb_list_category b WHERE a.child_list_cat_id = b.list_cat_id AND a.list_cat_id = ? ORDER BY a.sequence_num "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.child_list_cat_id = rs.getString("child_list_cat_id");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.name = rs.getString("name");
				xrec.category_url = rs.getString("category_url");
				xrec.image_url = rs.getString("image_url");
				xrec.suggested_qty = rs.getString("suggested_qty");
				xrec.primary_parent_cat_id = rs.getString("primary_parent_cat_id");
				xrec.primary_parent_cat_name = rs.getString("primary_parent_cat_name");
				xrec.display_name = rs.getString("display_name");
				xrec.threshold_qty = rs.getString("threshold_qty");
				xrec.threshold_amt = rs.getString("threshold_amt");
				xrec.service_type_cd = rs.getString("service_type_cd");
				xrec.is_disabled = utility.parseBoolean(rs.getString("is_disabled"), false);
				xrec.is_deleted = utility.parseBoolean(rs.getString("is_deleted"), false);
				xrec.is_config_complete = utility.parseBoolean(rs.getString("is_config_complete"), false);
				xrec.is_visible_on_checklist = utility.parseBoolean(rs.getString("is_visible_on_checklist"), false);
				xrec.is_visible_on_reg_list = utility.parseBoolean(rs.getString("is_visible_on_reg_list"), false);
				xrec.is_child_prd_needed_to_disp = utility.parseBoolean(rs.getString("is_child_prd_needed_to_disp"), false);
				xrec.baby_category_url = rs.getString("baby_category_url");
				xrec.baby_image_url = rs.getString("baby_image_url");
				xrec.ca_category_url = rs.getString("ca_category_url");
				xrec.ca_image_url = rs.getString("ca_image_url");
				xrec.tbs_category_url = rs.getString("tbs_category_url");
				xrec.tbs_image_url = rs.getString("tbs_image_url");
				xrec.eph = rs.getLong("eph");
				xrec.jda = rs.getLong("jda");
				xrec.facet = rs.getLong("facet");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.child_list_cat_id)) {

			throw new MessageException(1000, "invalid child_list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		if (rec.list_cat_id.equals(rec.child_list_cat_id)) {

			throw new MessageException(1000, "cyclic relationship");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;

		String sql = "INSERT INTO bbb_misc.bbb_list_cat_chldcat ( " +
				"list_cat_id,  " +
			     	"child_list_cat_id,  " +
				"sequence_num) " + 
		 		"SELECT ?, ?, 2 FROM DUAL ";
		try {
			conn = dba.getConnection();

			conn.setAutoCommit(false);

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.child_list_cat_id);

			stmt1.executeUpdate();

			sql = "SELECT * FROM ( " +
			"SELECT list_cat_id, child_list_cat_id, CONNECT_BY_ISCYCLE cycle, " +  
			"SUBSTR(SYS_CONNECT_BY_PATH (list_cat_id, ','), 2)  AS path, " +
			"REGEXP_SUBSTR(SYS_CONNECT_BY_PATH(list_cat_id, ','), '[^,]+', 1, 1) root " +
			"FROM bbb_misc.bbb_list_cat_chldcat " +
			"START WITH list_cat_id = ? " + 
			"CONNECT  BY NOCYCLE PRIOR child_list_cat_id = list_cat_id " + 
			") WHERE cycle = 1 ";

			stmt2 = conn.prepareStatement(sql);

			stmt2.setString(1, rec.list_cat_id);

			rs1 = stmt2.executeQuery();

			if (rs1.next()) {

				conn.rollback();

				throw new MessageException(1000, "cyclic relationship");
			}

			conn.commit();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();

			} catch(SQLException ex) {}

			if (conn != null) {
				try {
					conn.rollback();
				} catch(SQLException ex) {}

				conn.setAutoCommit(true);
				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.child_list_cat_id)) {

			throw new MessageException(1000, "invalid child_list_cat_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "DELETE FROM bbb_misc.bbb_list_cat_chldcat WHERE list_cat_id = ? AND child_list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.child_list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.child_list_cat_id)) {

			throw new MessageException(1000, "invalid child_list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_cat_chldcat a " + 
				"SET sequence_num = (SELECT rn-1 FROM ( " +
				"SELECT list_cat_id, child_list_cat_id, row_number() " + 
				"OVER (ORDER BY CASE WHEN list_cat_id = ? AND child_list_cat_id = ? THEN ? ELSE sequence_num END, " + 
				"CASE WHEN list_cat_id = ? AND child_list_cat_id = ? THEN (SELECT ?-sequence_num FROM bbb_misc.bbb_list_cat_chldcat WHERE list_cat_id = ? AND child_list_cat_id = ?) ELSE 0 END, list_cat_id, child_list_cat_id) rn " + 
				"FROM bbb_misc.bbb_list_cat_chldcat " +
				"WHERE list_cat_id = ?) " +
				"WHERE list_cat_id = a.list_cat_id " + 
				"AND child_list_cat_id = a.child_list_cat_id) " +
				"WHERE list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.child_list_cat_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.setString(4, rec.list_cat_id);
			stmt1.setString(5, rec.child_list_cat_id);
			stmt1.setLong(6, rec.sequence_num);
			stmt1.setString(7, rec.list_cat_id);
			stmt1.setString(8, rec.child_list_cat_id);
			stmt1.setString(9, rec.list_cat_id);
			stmt1.setString(10, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence2(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.child_list_cat_id)) {

			throw new MessageException(1000, "invalid child_list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_cat_chldcat_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.child_list_cat_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String list_id;
		public String list_cat_id;
		public String parent_list_cat_id;
		public String child_list_cat_id;
		public long sequence_num;
		public String name;
		public String category_url;
		public String image_url;
		public String suggested_qty;
		public String primary_parent_cat_id;
		public String primary_parent_cat_name;
		public String display_name;
		public String threshold_qty;
		public String threshold_amt;
		public String service_type_cd;
		public boolean is_disabled;
		public boolean is_deleted;
		public boolean is_config_complete;
		public boolean is_visible_on_checklist;
		public boolean is_visible_on_reg_list;
		public boolean is_child_prd_needed_to_disp;
		public String baby_category_url;
		public String baby_image_url;
		public String ca_category_url;
		public String ca_image_url;
		public String tbs_category_url;
		public String tbs_image_url;
		public long eph;
		public long jda;
		public long facet;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}
}

public class BBB_LIST_CAT_SKU implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_CAT_SKU(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		String sql = "SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"a.sku_id, " +
			"a.rule_evaluation_cd, " +
			"a.sequence_num, " +
			"NVL(b.display_name, a.sku_id) display_name, " +
			"a.create_user, " +
			"a.create_date, " +
			"a.last_mod_user, " +
			"a.last_mod_date " +
		"FROM bbb_misc.bbb_list_cat_sku a, bbb_switch_a.dcs_sku b WHERE a.sku_id = b.sku_id(+) AND a.list_cat_id = ? ORDER BY a.sequence_num "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.sku_id = rs.getString("sku_id");
				xrec.rule_evaluation_cd = rs.getString("rule_evaluation_cd");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.display_name = rs.getString("display_name");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.sku_id)) {

			throw new MessageException(1000, "invalid sku_id");
		}

		if (utility.isBlank(rec.rule_evaluation_cd)) {

			throw new MessageException(1000, "invalid rule_evaluation_cd");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql = "INSERT INTO bbb_misc.bbb_list_cat_sku ( " +
				"rule_id,  " +
				"list_cat_id,  " +
			     	"sku_id,  " +
				"rule_evaluation_cd,  " +
				"sequence_num, " + 
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, ?, 2, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(Config.getNextSequence(Config.BBB_LIST_RULES_SKU_SEQUENCE));

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			stmt2 = conn.prepareStatement(sql);
			stmt2.setString(1, rec.rule_id);
			stmt2.setString(2, rec.list_cat_id);
			stmt2.setString(3, rec.sku_id);
			stmt2.setString(4, rec.rule_evaluation_cd);

			stmt2.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.sku_id)) {

			throw new MessageException(1000, "invalid sku_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_cat_sku SET " +
			     	"rule_evaluation_cd = ?,  " +
				"create_user = 'APPADMIN', " + 
				"create_date = SYSDATE, " + 
				"last_mod_user = 'APPADMIN', " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? AND list_cat_id = ? AND sku_id = ? ";
		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.rule_evaluation_cd);
			stmt1.setString(2, rec.rule_id);
			stmt1.setString(3, rec.list_cat_id);
			stmt1.setString(4, rec.sku_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;


		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.sku_id)) {

			throw new MessageException(1000, "invalid sku_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "DELETE FROM bbb_misc.bbb_list_cat_sku WHERE rule_id = ? AND list_cat_id = ? AND sku_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.rule_id);
			stmt1.setString(2, rec.list_cat_id);
			stmt1.setString(3, rec.sku_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.sku_id)) {

			throw new MessageException(1000, "invalid sku_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_cat_sku_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public String sku_id;
		public String display_name;
		public String rule_evaluation_cd;
		public long sequence_num;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}
}

public class BBB_LIST_RULES_SKU_CAT implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_RULES_SKU_CAT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		String sql = "SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"b.sku_id, " +
			"b.rule_evaluation_cd, " +
			"a.sequence_num, " +
			"NVL((SELECT display_name FROM bbb_switch_a.dcs_sku WHERE sku_id = b.sku_id), b.sku_id) display_name, " +
			"b.create_user, " +
			"b.create_date, " +
			"b.last_mod_user, " +
			"b.last_mod_date " +
		"FROM bbb_misc.bbb_list_rules_sku_cat a, bbb_misc.bbb_list_rules_sku b WHERE a.rule_id = b.rule_id AND a.list_cat_id = ? ORDER BY a.sequence_num "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.sku_id = rs.getString("sku_id");
				xrec.rule_evaluation_cd = rs.getString("rule_evaluation_cd");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.display_name = rs.getString("display_name");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.sku_id)) {

			throw new MessageException(1000, "invalid sku_id");
		}

		if (utility.isBlank(rec.rule_evaluation_cd)) {

			throw new MessageException(1000, "invalid rule_evaluation_cd");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql1 = "INSERT INTO bbb_misc.bbb_list_rules_sku ( " +
				"rule_id,  " +
			     	"sku_id,  " +
				"rule_evaluation_cd,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, ?, SYSDATE, ?, SYSDATE FROM DUAL ";

		String sql2 = "INSERT INTO bbb_misc.bbb_list_rules_sku_cat ( " +
				"rule_id,  " +
				"list_cat_id,  " +
				"sequence_num) " + 
		 		"SELECT ?, ?, REGEXP_REPLACE(?, '[^0-9]', '') FROM DUAL ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(Config.getNextSequence(Config.BBB_LIST_RULES_SKU_SEQUENCE));

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			stmt2 = conn.prepareStatement(sql1);
			stmt2.setString(1, rec.rule_id);
			stmt2.setString(2, rec.sku_id);
			stmt2.setString(3, rec.rule_evaluation_cd);
			stmt2.setString(4, rec.create_user);
			stmt2.setString(5, rec.last_mod_user);
			stmt2.executeUpdate();

			stmt3 = conn.prepareStatement(sql2);
			stmt3.setString(1, rec.rule_id);
			stmt3.setString(2, rec.list_cat_id);
			stmt3.setString(3, rec.rule_id);
			stmt3.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_rules_sku SET " +
			     	"rule_evaluation_cd = ?,  " +
				"last_mod_user = ?, " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? ";
		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.rule_evaluation_cd);
			stmt1.setString(2, rec.last_mod_user);
			stmt1.setString(3, rec.rule_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;

		String sql1 = "DELETE FROM bbb_misc.bbb_list_rules_sku_cat WHERE rule_id = ? ";
		String sql2 = "DELETE FROM bbb_misc.bbb_list_rules_sku WHERE rule_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.rule_id);
			stmt1.executeUpdate();

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.rule_id);
			stmt2.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_rules_sku_cat a " + 
				"SET sequence_num = (SELECT rn-1 FROM ( " +
				"SELECT list_cat_id, rule_id, row_number() " + 
				"OVER (ORDER BY CASE WHEN list_cat_id = ? AND rule_id = ? THEN ? ELSE sequence_num END, " + 
				"CASE WHEN list_cat_id = ? AND rule_id = ? THEN (SELECT ?-sequence_num FROM bbb_misc.bbb_list_rules_sku_cat WHERE list_cat_id = ? AND rule_id = ?) ELSE 0 END, list_cat_id, rule_id) rn " + 
				"FROM bbb_misc.bbb_list_rules_sku_cat " +
				"WHERE list_cat_id = ?) " +
				"WHERE list_cat_id = a.list_cat_id " +
				"AND rule_id = a.rule_id) " +
				"WHERE list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.setString(4, rec.list_cat_id);
			stmt1.setString(5, rec.rule_id);
			stmt1.setLong(6, rec.sequence_num);
			stmt1.setString(7, rec.list_cat_id);
			stmt1.setString(8, rec.rule_id);
			stmt1.setString(9, rec.list_cat_id);
			stmt1.setString(10, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence2(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_rules_sku_cat_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public String sku_id;
		public String display_name;
		public String rule_evaluation_cd;
		public long sequence_num;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}
}

public class BBB_LIST_TYPE implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_TYPE(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = 	"SELECT " + 
			     	"a.type_id, " + 
			     	"a.type_name, " + 
			     	"a.sub_type_code,  " +
                             	"a.sub_type_name,  " +
				"a.create_user, " + 
				"a.create_date, " + 
				"a.last_mod_user, " + 
				"a.last_mod_date " +
		"FROM bbb_misc.bbb_list_type a "; 

		sql += " ORDER BY type_name, sub_type_name ";

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();
		int index = 1;

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.type_id = rs.getString("type_id");
				xrec.type_name = rs.getString("type_name");
				xrec.sub_type_code = rs.getString("sub_type_code");
				xrec.sub_type_name = rs.getString("sub_type_name");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String type_id;
		public String type_name;
		public String sub_type_code;
		public String sub_type_name;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}
}

public class DCS_SKU implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public DCS_SKU(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = 	"SELECT * FROM (SELECT " + 
			     	"a.sku_id, " + 
			     	"a.display_name " + 
		"FROM bbb_switch_a.dcs_sku a WHERE 1=1 "; 

		rec.sku_id = Config.exactSearch(rec.sku_id);

		if (!utility.isBlank(rec.sku_id) && rec.sku_id.indexOf("%") != -1) {

			sql += " AND sku_id LIKE ? ";
			sql += " ORDER BY sku_id) ";
		}
		else if (!utility.isBlank(rec.sku_id)) {

			sql += " AND sku_id = ?  ";
			sql += " ORDER BY sku_id) ";
		}
		else if (!utility.isBlank(rec.display_name)) {

			sql += " AND UPPER(display_name) LIKE ? ";
			sql += " ORDER BY display_name) ";
		}
		else {
			sql += " ";
			sql += " ORDER BY display_name) ";
		}

		sql += " WHERE ROWNUM < ? ";

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();
		int index = 1;

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isBlank(rec.sku_id)) {
				stmt.setString(index++, rec.sku_id.toUpperCase());
			}
			else if (!utility.isBlank(rec.display_name)) {
				stmt.setString(index++, "%" + rec.display_name.toUpperCase() + "%");
			}

			stmt.setLong(index++, rec.xmax);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.sku_id = rs.getString("sku_id");
				xrec.display_name = rs.getString("display_name");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String sku_id;
		public String display_name;
		public long xmax;
	}
}

public class BBB_DEPT implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_DEPT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = 	"SELECT " + 
			     	"a.jda_dept_id, " + 
			     	"a.descrip, " +
				"(SELECT COUNT(*) FROM bbb_switch_a.bbb_sub_dept WHERE jda_dept_id = a.jda_dept_id) children " + 
		"FROM bbb_switch_a.bbb_dept a WHERE 1 = 1 "; 

		rec.jda_dept_id = Config.exactSearch(rec.jda_dept_id);

		if (!utility.isNullOrEmpty(rec.jda_dept_id) && rec.jda_dept_id.indexOf("%") != -1) {

			sql += " AND jda_dept_id LIKE ? ";
			sql += " ORDER BY jda_dept_id ";
		}
		else if (!utility.isNullOrEmpty(rec.jda_dept_id)) {

			sql += " AND jda_dept_id = ?  ";
			sql += " ORDER BY jda_dept_id ";
		}
		else if (!utility.isNullOrEmpty(rec.descrip)) {

			sql += " AND UPPER(descrip) LIKE ? ";
			sql += " ORDER BY descrip ";
		}
		else {
			sql += " ORDER BY descrip ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isNullOrEmpty(rec.jda_dept_id)) {
				stmt.setString(1, utility.ltrim(rec.jda_dept_id.toUpperCase().trim()));
			}
			else if (!utility.isNullOrEmpty(rec.descrip)) {
				stmt.setString(1, rec.descrip.toUpperCase().trim() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.jda_dept_id = rs.getString("jda_dept_id");
				xrec.descrip = rs.getString("descrip");
				xrec.children = rs.getLong("children");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String jda_dept_id;
		public String descrip;
		public long children;
	}
}

public class BBB_SUB_DEPT implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_SUB_DEPT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.jda_dept_id)) {

			throw new MessageException(1000, "invalid jda_dept_id");
		}

		String sql = 	"SELECT " + 
			     	"a.jda_dept_id, " + 
				"a.jda_sub_dept_id, " + 
			     	"a.descrip, " + 
				"(SELECT COUNT(*) FROM bbb_switch_a.bbb_class WHERE jda_dept_id = a.jda_dept_id AND jda_sub_dept_id = REGEXP_REPLACE(a.jda_sub_dept_id, '[0-9]*_', '')) children " +
		"FROM bbb_switch_a.bbb_sub_dept a WHERE jda_dept_id = ? "; 

		rec.jda_sub_dept_id = Config.exactSearch(rec.jda_sub_dept_id);

		if (!utility.isNullOrEmpty(rec.jda_sub_dept_id) && rec.jda_sub_dept_id.indexOf("%") != -1) {

			sql += " AND jda_sub_dept_id LIKE ? ";
			sql += " ORDER BY jda_dept_id, jda_sub_dept_id ";
		}
		else if (!utility.isNullOrEmpty(rec.jda_sub_dept_id)) {

			sql += " AND jda_sub_dept_id = ?  ";
			sql += " ORDER BY jda_dept_id, jda_sub_dept_id ";
		}
		else if (!utility.isNullOrEmpty(rec.descrip)) {

			sql += " AND UPPER(descrip) LIKE ? ";
			sql += " ORDER BY descrip ";
		}
		else {
			sql += " ORDER BY descrip ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.jda_dept_id);

			if (!utility.isNullOrEmpty(rec.jda_sub_dept_id)) {
				stmt.setString(2, utility.ltrim(rec.jda_sub_dept_id.toUpperCase().trim()));
			}
			else if (!utility.isNullOrEmpty(rec.descrip)) {
				stmt.setString(2, rec.descrip.toUpperCase().trim() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.jda_dept_id = rs.getString("jda_dept_id");
				xrec.jda_sub_dept_id = rs.getString("jda_sub_dept_id");
				xrec.descrip = rs.getString("descrip");
				xrec.children = rs.getLong("children");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String jda_dept_id;
		public String jda_sub_dept_id;
		public String descrip;
		public long children;
	}
}

public class BBB_CLASS implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_CLASS(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.jda_dept_id)) {

			throw new MessageException(1000, "invalid jda_dept_id");
		}

		if (utility.isBlank(rec.jda_sub_dept_id)) {

			throw new MessageException(1000, "invalid jda_sub_dept_id");
		}

		String sql = 	"SELECT " + 
			     	"a.jda_dept_id, " + 
		//		"a.jda_sub_dept_id, " + 
				"(SELECT jda_sub_dept_id FROM bbb_switch_a.bbb_sub_dept WHERE jda_dept_id = a.jda_dept_id AND jda_sub_dept_id = a.jda_dept_id || '_' || a.jda_sub_dept_id) jda_sub_dept_id, " +
				"a.jda_class, " +
			     	"a.descrip, " + 
				"0 children " +
		"FROM bbb_switch_a.bbb_class a WHERE jda_dept_id = ? AND jda_sub_dept_id = REGEXP_REPLACE(?, '[0-9]*_', '') "; 

		rec.jda_class = Config.exactSearch(rec.jda_class);

		if (!utility.isNullOrEmpty(rec.jda_class) && rec.jda_class.indexOf("%") != -1) {

			sql += " AND jda_class LIKE ? ";
			sql += " ORDER BY jda_dept_id, jda_sub_dept_id, jda_class ";
		}
		else if (!utility.isNullOrEmpty(rec.jda_class)) {

			sql += " AND jda_class = ?  ";
			sql += " ORDER BY jda_dept_id, jda_sub_dept_id, jda_class ";
		}
		else if (!utility.isNullOrEmpty(rec.descrip)) {

			sql += " AND UPPER(descrip) LIKE ? ";
			sql += " ORDER BY descrip ";
		}
		else {
			sql += " ORDER BY descrip ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.jda_dept_id);
			stmt.setString(2, rec.jda_sub_dept_id);

			if (!utility.isNullOrEmpty(rec.jda_class)) {
				stmt.setString(3, utility.ltrim(rec.jda_class.toUpperCase().trim()));
			}
			else if (!utility.isNullOrEmpty(rec.descrip)) {
				stmt.setString(3, rec.descrip.toUpperCase().trim() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.jda_dept_id = rs.getString("jda_dept_id");
				xrec.jda_sub_dept_id = rs.getString("jda_sub_dept_id");
				xrec.jda_class = rs.getString("jda_class");
				xrec.descrip = rs.getString("descrip");
				xrec.children = rs.getLong("children");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String jda_dept_id;
		public String jda_sub_dept_id;
		public String jda_class;
		public String descrip;
		public long children;
	}
}

public class BBB_EPH_NODE implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_EPH_NODE(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = 	"SELECT " + 
			     	"a.eph_node_id, " + 
			     	"a.display_name, " +
				"(SELECT COUNT(*) FROM bbb_misc.bbb_eph_node_parent WHERE parent_node_id = a.eph_node_id) children ";

		if (utility.isBlank(rec.parent_node_id)) {

			sql += "FROM bbb_misc.bbb_eph_node a WHERE is_root_node = 1 "; 
		}
		else {
			sql += "FROM bbb_misc.bbb_eph_node a, bbb_misc.bbb_eph_node_parent b WHERE b.parent_node_id = ? AND b.child_node_id = a.eph_node_id  "; 
		}

		rec.eph_node_id = Config.exactSearch(rec.eph_node_id);

		if (!utility.isNullOrEmpty(rec.eph_node_id) && rec.eph_node_id.indexOf("%") != -1) {

			sql += " AND eph_node_id LIKE ? ";
			sql += " ORDER BY eph_node_id ";
		}
		else if (!utility.isNullOrEmpty(rec.eph_node_id)) {

			sql += " AND eph_node_id = ?  ";
			sql += " ORDER BY eph_node_id ";
		}
		else if (!utility.isNullOrEmpty(rec.display_name)) {

			sql += " AND UPPER(display_name) LIKE ? ";
			sql += " ORDER BY display_name ";
		}
		else {
			sql += " ORDER BY display_name ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();
		int index = 1;

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isBlank(rec.parent_node_id)) {
				stmt.setString(1, rec.parent_node_id);
				index++;
			}

			if (!utility.isNullOrEmpty(rec.eph_node_id)) {
				stmt.setString(index, utility.ltrim(rec.eph_node_id.toUpperCase().trim()));
			}
			else if (!utility.isNullOrEmpty(rec.display_name)) {
				stmt.setString(index, rec.display_name.toUpperCase().trim() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.eph_node_id = rs.getString("eph_node_id");
				xrec.display_name = rs.getString("display_name");
				xrec.children = rs.getLong("children");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String parent_node_id;
		public String eph_node_id;
		public String display_name;
		public long children;
	}
}

public class BBB_FACET_RULES implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_FACET_RULES(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_facet_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +
		"SELECT " + 
			     	"a.rule_id, " + 
			     	"a.facet_rule_name, "  +
				"a.facet_value_pair_list, "  +
				"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = a.rule_id) facets " +
		"FROM bbb_misc.bbb_facet_rules a WHERE 1 = 1 "; 

		if (!utility.isBlank(rec.rule_id) && rec.rule_id.indexOf("%") != -1) {

			sql += " AND rule_id LIKE ? ";
			sql += " ORDER BY rule_id ";
		}
		else if (!utility.isBlank(rec.rule_id)) {

			sql += " AND rule_id = ?  ";
			sql += " ORDER BY rule_id ";
		}
		else if (!utility.isBlank(rec.facet_rule_name)) {

			sql += " AND UPPER(facet_rule_name) LIKE ? ";
			sql += " ORDER BY facet_rule_name ";
		}
		else {
			sql += " ORDER BY facet_rule_name ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isBlank(rec.rule_id)) {
				stmt.setString(1, rec.rule_id.toUpperCase());
			}
			else if (!utility.isBlank(rec.facet_rule_name)) {
				stmt.setString(1, rec.facet_rule_name.toUpperCase() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.facet_value_pair_list = rs.getString("facets");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String rule_id;
		public String facet_rule_name;
		public String facet_value_pair_list;
	}
}

public class BBB_LIST_RULES implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_RULES(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_list_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +
		"SELECT " + 
			     	"a.rule_id, " + 
				"a.rule_type_cd, " + 
			     	"a.facet_rule_name, "  +
				"a.facet_value_pair_list, "  +
				"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = a.rule_id) facets " +
		"FROM bbb_misc.bbb_list_rules a WHERE a.facet_rule_name IS NOT NULL AND a.facet_value_pair_list IS NOT NULL "; 

		if (Config.FACET_FILTER) {
			if (!utility.isBlank(rec.eph_node_id)) {

				sql += "AND (SELECT COUNT(*) FROM facets WHERE rule_id = a.rule_id) = " +
				"(SELECT COUNT(*) FROM facets f WHERE rule_id = a.rule_id " +
				"AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_eph_facets WHERE f.facet_id = facet_id AND f.facet_value_id = facet_value_id AND eph_id = ?) " +
				")";
			}
			else if (!utility.isBlank(rec.jda_dept_id)) {

				sql += "AND (SELECT COUNT(*) FROM facets WHERE rule_id = a.rule_id) = " +
				"(SELECT COUNT(*) FROM facets f WHERE rule_id = a.rule_id " +
				"AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_jda_facets WHERE f.facet_id = facet_id AND f.facet_value_id = facet_value_id ";

				if (!utility.isBlank(rec.jda_sub_dept_id)) {
					sql += "AND jda_sub_dept_id = ? ";
					if (!utility.isBlank(rec.jda_class)) {
						sql += "AND jda_class = ? ";
					}
					sql += ")";
				}
				else {
					sql += "AND jda_dept_id = ?) ";
				}

				sql += ")";
			}
		}

		if (!utility.isBlank(rec.rule_type_cd)) {

			sql += " AND rule_type_cd = ? ";
		}

		rec.rule_id = Config.exactSearch(rec.rule_id);

		if (!utility.isNullOrEmpty(rec.rule_id) && rec.rule_id.indexOf("%") != -1) {

			sql += " AND rule_id LIKE ? ";
			sql += " ORDER BY rule_id ";
		}
		else if (!utility.isNullOrEmpty(rec.rule_id)) {

			sql += " AND rule_id = ?  ";
			sql += " ORDER BY rule_id ";
		}
		else if (!utility.isNullOrEmpty(rec.facet_rule_name)) {

			sql += " AND UPPER(facet_rule_name) LIKE ? ";
			sql += " ORDER BY facet_rule_name ";
		}
		else {
			sql += " ORDER BY facet_rule_name ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();
		int index = 1;

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (Config.FACET_FILTER) {
				if (!utility.isBlank(rec.eph_node_id)) {
					stmt.setString(index++, rec.eph_node_id);
				}
				else if (!utility.isBlank(rec.jda_dept_id)) {

					if (!utility.isBlank(rec.jda_sub_dept_id)) {
						stmt.setString(index++, rec.jda_sub_dept_id);
						if (!utility.isBlank(rec.jda_class)) {
							stmt.setString(index++, rec.jda_class);
						}
					}
					else {
						stmt.setString(index++, rec.jda_dept_id);
					}
				}
			}

			if (!utility.isBlank(rec.rule_type_cd)) {

				stmt.setString(index++, rec.rule_type_cd);
			}

			if (!utility.isNullOrEmpty(rec.rule_id)) {
				stmt.setString(index++, utility.ltrim(rec.rule_id.toUpperCase().trim()));
			}
			else if (!utility.isNullOrEmpty(rec.facet_rule_name)) {
				stmt.setString(index++, rec.facet_rule_name.toUpperCase().trim() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.rule_type_cd = rs.getString("rule_type_cd");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.facets = rs.getString("facets");
				xrec.facet_value_pair_list = rs.getString("facet_value_pair_list");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String rule_id;
		public String rule_type_cd;
		public String eph_node_id;
		public String jda_dept_id;
		public String jda_sub_dept_id;
		public String jda_class;
		public String facet_rule_name;
		public String facets;
		public String facet_value_pair_list;
	}
}

public class BBB_FACET_TYPES implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_FACET_TYPES(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		String sql = 	"SELECT DISTINCT " + 
			     	"a.facet_id, " + 
			     	"a.description " + 
		"FROM bbb_core.bbb_facet_types a WHERE 1 = 1 "; 

		if (!utility.isBlank(rec.jda_dept_id)) {

			sql += "AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_jda_facets WHERE a.facet_id = facet_id AND jda_dept_id = ? "; 

			if (!utility.isBlank(rec.jda_sub_dept_id)) {

				sql += "AND jda_sub_dept_id = ? "; 

				if (!utility.isBlank(rec.jda_class)) {

					sql += "AND jda_class = ? "; 
				}
			}

			sql += ") ";
		}
		else if (!utility.isBlank(rec.eph_node_id)) {

			sql += "AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_eph_facets WHERE a.facet_id = facet_id AND eph_id = ? ) "; 
		}

		rec.facet_id = Config.exactSearch(rec.facet_id);

		if (!utility.isNullOrEmpty(rec.facet_id) && rec.facet_id.indexOf("%") != -1) {

			sql += " AND facet_id LIKE ? ";
			sql += " ORDER BY facet_id ";
		}
		else if (!utility.isNullOrEmpty(rec.facet_id)) {

			sql += " AND facet_id = ?  ";
			sql += " ORDER BY facet_id ";
		}
		else if (!utility.isNullOrEmpty(rec.description)) {

			sql += " AND UPPER(description) LIKE ? ";
			sql += " ORDER BY description ";
		}
		else {
			sql += " ORDER BY description ";
		}

		if (sql != "") {
		//	throw new Exception(sql);

		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();
		int index = 1;

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isBlank(rec.jda_dept_id)) {

				stmt.setString(index++, rec.jda_dept_id);

				if (!utility.isBlank(rec.jda_sub_dept_id)) {

					stmt.setString(index++, rec.jda_sub_dept_id);

					if (!utility.isBlank(rec.jda_class)) {

						stmt.setString(index++, rec.jda_class); 
					}
				}
			}
			else if (!utility.isBlank(rec.eph_node_id)) {

				stmt.setString(index++, rec.eph_node_id);
			} 

			if (!utility.isNullOrEmpty(rec.facet_id)) {
				stmt.setString(index, utility.ltrim(rec.facet_id.toUpperCase().trim()));
			}
			else if (!utility.isNullOrEmpty(rec.description)) {
				stmt.setString(index, rec.description.toUpperCase().trim() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.facet_id = rs.getString("facet_id");
				xrec.description = rs.getString("description");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String eph_node_id;
		public String jda_dept_id;
		public String jda_sub_dept_id;
		public String jda_class;
		public String facet_id;
		public String description;
	}
}


public class BBB_FACET_VALUE_PAIRS implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_FACET_VALUE_PAIRS(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.facet_id)) {

			throw new MessageException(1000, "invalid facet_id");
		}

		String sql = 	"SELECT DISTINCT " + 
			     	"a.facet_id, " + 
				"a.facet_value_id, " + 
			     	"a.facet_value_desc " + 
		"FROM bbb_misc.bbb_facet_value_pairs a WHERE 1 = 1 "; 

		if (!utility.isBlank(rec.jda_dept_id)) {

			sql += "AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_jda_facets WHERE a.facet_id = facet_id AND a.facet_value_id = facet_value_id AND jda_dept_id = ? "; 

			if (!utility.isBlank(rec.jda_sub_dept_id)) {

				sql += "AND jda_sub_dept_id = ? "; 

				if (!utility.isBlank(rec.jda_class)) {

					sql += "AND jda_class = ? "; 
				}
			}

			sql += ") ";
		}
		else if (!utility.isBlank(rec.eph_node_id)) {

			sql += "AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_eph_facets WHERE a.facet_id = facet_id AND a.facet_value_id = facet_value_id AND eph_id = ? ) "; 
		}

		if (!utility.isBlank(rec.facet_id) && rec.facet_id.indexOf("%") != -1) {
			sql += " AND facet_id LIKE ? ";
			sql += " ORDER BY facet_id ";
		}
		else if (!utility.isBlank(rec.facet_id)) {

			sql += " AND facet_id = ?  ";
			sql += " ORDER BY facet_value_desc ";
		}
		else if (!utility.isBlank(rec.facet_value_desc)) {

			sql += " AND UPPER(facet_value_desc) LIKE ? ";
			sql += " ORDER BY facet_value_desc ";
		}
		else {
			sql += " ORDER BY facet_value_desc ";
		}

		if (sql != "") {
		//	throw new Exception(sql);

		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();
		int index = 1;

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			if (!utility.isBlank(rec.jda_dept_id)) {

				stmt.setString(index++, rec.jda_dept_id);

				if (!utility.isBlank(rec.jda_sub_dept_id)) {

					stmt.setString(index++, rec.jda_sub_dept_id);

					if (!utility.isBlank(rec.jda_class)) {

						stmt.setString(index++, rec.jda_class); 
					}
				}
			}
			else if (!utility.isBlank(rec.eph_node_id)) {

				stmt.setString(index++, rec.eph_node_id);
			} 

			if (!utility.isBlank(rec.facet_id)) {
				stmt.setString(index, rec.facet_id.toUpperCase());
			}
			else if (!utility.isBlank(rec.facet_value_desc)) {
				stmt.setString(index, rec.facet_value_desc.toUpperCase() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.facet_id = rs.getString("facet_id");
				xrec.facet_value_id = rs.getString("facet_value_id");
				xrec.facet_value_desc = rs.getString("facet_value_desc");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String eph_node_id;
		public String jda_dept_id;
		public String jda_sub_dept_id;
		public String jda_class;
		public String facet_id;
		public String facet_value_id;
		public String facet_value_desc;
	}
}

public class BBB_EPH_FACET_VALUE_PAIRS implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_EPH_FACET_VALUE_PAIRS(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.eph_node_id)) {

			throw new MessageException(1000, "invalid eph_node_id");
		}

		if (utility.isBlank(rec.facet_id)) {

			throw new MessageException(1000, "invalid facet_id");
		}

		String sql = 	"SELECT DISTINCT " + 
				"a.eph_id, " +
			     	"a.facet_id, " + 
				"a.facet_value_id, " + 
			     	"a.facet_value_desc " + 
		"FROM bbb_misc.bbb_list_eph_facets a WHERE facet_id = ? AND eph_id = ? "; 

		if (!utility.isBlank(rec.facet_value_id) && rec.facet_value_id.indexOf("%") != -1) {
			sql += " AND facet_value_id LIKE ? ";
			sql += " ORDER BY facet_value_id ";
		}
		else if (!utility.isBlank(rec.facet_value_id)) {

			sql += " AND facet_value_id = ?  ";
			sql += " ORDER BY facet_value_id ";
		}
		else if (!utility.isBlank(rec.facet_value_desc)) {

			sql += " AND UPPER(facet_value_desc) LIKE ? ";
			sql += " ORDER BY facet_value_desc ";
		}
		else {
			sql += " ORDER BY facet_value_desc ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.facet_id);
			stmt.setString(2, rec.eph_node_id);

			if (!utility.isBlank(rec.facet_value_id)) {
				stmt.setString(3, rec.facet_value_id.toUpperCase());
			}
			else if (!utility.isBlank(rec.facet_value_desc)) {
				stmt.setString(3, rec.facet_value_desc.toUpperCase() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

			//	xrec.eph_node_id = rs.getString("eph_id");
				xrec.facet_id = rs.getString("facet_id");
				xrec.facet_value_id = rs.getString("facet_value_id");
				xrec.facet_value_desc = rs.getString("facet_value_desc");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String eph_node_id;
		public String facet_id;
		public String facet_value_id;
		public String facet_value_desc;
	}
}

public class BBB_JDA_FACET_VALUE_PAIRS implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_JDA_FACET_VALUE_PAIRS(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.jda_dept_id)) {

			throw new MessageException(1000, "invalid jda_dept_id");
		}

		if (utility.isBlank(rec.facet_id)) {

			throw new MessageException(1000, "invalid facet_id");
		}

		String sql = 	"SELECT DISTINCT " + 
				"a.jda_dept_id, " +
				"a.jda_sub_dept_id, " +
				"a.jda_class, " +
			     	"a.facet_id, " + 
				"a.facet_value_id, " + 
			     	"a.facet_value_desc " + 
		"FROM bbb_misc.bbb_list_jda_facets a WHERE facet_id = ? AND jda_dept_id = ? ";

		if (!utility.isBlank(rec.jda_sub_dept_id)) {

			sql += "AND jda_sub_dept_id = ? "; 

			if (!utility.isBlank(rec.jda_class)) {

				sql += "AND jda_class = ? "; 
			}
		}

		if (!utility.isBlank(rec.facet_value_id) && rec.facet_value_id.indexOf("%") != -1) {
			sql += " AND facet_value_id LIKE ? ";
			sql += " ORDER BY facet_value_id ";
		}
		else if (!utility.isBlank(rec.facet_value_id)) {

			sql += " AND facet_value_id = ?  ";
			sql += " ORDER BY facet_value_id ";
		}
		else if (!utility.isBlank(rec.facet_value_desc)) {

			sql += " AND UPPER(facet_value_desc) LIKE ? ";
			sql += " ORDER BY facet_value_desc ";
		}
		else {
			sql += " ORDER BY facet_value_desc ";
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();
		int index = 1;

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(index++, rec.facet_id);

			stmt.setString(index++, rec.jda_dept_id);

			if (!utility.isBlank(rec.jda_sub_dept_id)) {
				stmt.setString(index++, rec.jda_sub_dept_id);

				if (!utility.isBlank(rec.jda_class)) {
					stmt.setString(index++, rec.jda_class); 
				}
			}

			if (!utility.isBlank(rec.facet_value_id)) {
				stmt.setString(index, rec.facet_value_id.toUpperCase());
			}
			else if (!utility.isBlank(rec.facet_value_desc)) {
				stmt.setString(index, rec.facet_value_desc.toUpperCase() + "%");
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

			//	xrec.jda_dept_id = rs.getString("jda_dept_id");
			//	xrec.jda_sub_dept_id = rs.getString("jda_sub_dept_id");
			//	xrec.jda_class = rs.getString("jda_class");
				xrec.facet_id = rs.getString("facet_id");
				xrec.facet_value_id = rs.getString("facet_value_id");
				xrec.facet_value_desc = rs.getString("facet_value_desc");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public class Record {

		public String jda_dept_id;
		public String jda_sub_dept_id;
		public String jda_class;
		public String facet_id;
		public String facet_value_id;
		public String facet_value_desc;
	}
}

public class BBB_LIST_CAT_EPH implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_CAT_EPH(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}
/*
		String sql = "WITH facets AS ( " +
		"SELECT DISTINCT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " +
		"facet_value_id, " +
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM ( " +
		"SELECT " + 
		"rule_id, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 1) FACET_ID, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 2) FACET_VALUE_ID " +
		"FROM bbb_misc.bbb_facet_rules " +
		"CONNECT BY REGEXP_SUBSTR(facet_value_pair_list, '[^,]+', 1, LEVEL) IS NOT NULL " +
		") f )" +
*/
		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_facet_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +
		"SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"a.eph_node_id, " +
			"(SELECT display_name FROM bbb_misc.bbb_eph_node WHERE eph_node_id = a.eph_node_id) eph_display_name, " +
			"a.facet_rule_id, " +
			"b.facet_rule_name, " +
			"b.facet_value_pair_list, " + 
			"a.sequence_num, " +
			"(SELECT LISTAGG(child_node_id || CHR(9) || display_name, CHR(10)) WITHIN GROUP (ORDER BY LEVEL DESC) " + 
			"FROM ( " + 
			"SELECT child_node_id, parent_node_id " +  
			"FROM bbb_misc.bbb_eph_node_parent " +  
			"UNION " + 
			"SELECT eph_node_id, NULL " +  
			"FROM bbb_misc.bbb_eph_node WHERE is_root_node = 1 " + 
			") n1, bbb_misc.bbb_eph_node n2 " + 
			"WHERE n1.child_node_id = n2.eph_node_id " + 
			"START WITH child_node_id = a.eph_node_id " + 
			"CONNECT BY PRIOR parent_node_id = child_node_id) eph_nodes, " +
			"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = a.facet_rule_id) facets, " +
			"a.create_user, " +
			"a.create_date, " +
			"a.last_mod_user, " +
			"a.last_mod_date " +
		"FROM bbb_misc.bbb_list_cat_eph a, bbb_misc.bbb_facet_rules b " +
		"WHERE a.facet_rule_id = b.rule_id(+) AND a.list_cat_id = ? ORDER BY NVL(sequence_num, 999999) "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.facet_rule_id = rs.getString("facet_rule_id");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.eph_node_id = rs.getString("eph_node_id");
				xrec.eph_display_name = rs.getString("eph_display_name");
				xrec.eph_nodes = rs.getString("eph_nodes");
				xrec.facets = rs.getString("facets");
				xrec.facet_value_pair_list = rs.getString("facet_value_pair_list");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.eph_node_id)) {

			throw new MessageException(1000, "invalid eph_node_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		if (utility.isBlank(rec.facet_rule_id)) {

			// for bbb_list_cat_facet only
			if (utility.isBlank(rec.facet_rule_name)) {

			//	throw new MessageException(1000, "invalid facet_rule_name");
			}
		}

		Connection conn = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql2 = "INSERT INTO bbb_misc.bbb_facet_rules ( " +
				"rule_id,  " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		String sql3 = "INSERT INTO bbb_misc.bbb_list_cat_eph ( " +
				"rule_id,  " +
				"list_cat_id,  " +
			     	"eph_node_id,  " +
				"facet_rule_id,  " +
				"sequence_num, " + 
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, (SELECT MAX(rule_id) FROM bbb_misc.bbb_facet_rules WHERE rule_id = ?), " +
				"2, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(sql1);

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			if (utility.isBlank(rec.facet_rule_id) && !utility.isBlank(rec.facet_rule_name)) {

				stmt2 = conn.createStatement();

      				rs2 = stmt2.executeQuery(sql1);

				if (rs2.next()) {

					// facet_rule_id is NULL and facet_rule_name is not NULL 
					// insert new facet_rule

					rec.facet_rule_id = rs1.getString(1);

				//	rec.facet_value_pair_list = "";

					stmt3 = conn.prepareStatement(sql2);
					stmt3.setString(1, rec.facet_rule_id);
					stmt3.setString(2, rec.facet_rule_name);
					stmt3.setString(3, rec.facet_value_pair_list);

					stmt3.executeUpdate();
				}
			}

			stmt4 = conn.prepareStatement(sql3);
			stmt4.setString(1, rec.rule_id);
			stmt4.setString(2, rec.list_cat_id);
			stmt4.setString(3, rec.eph_node_id);
			stmt4.setString(4, rec.facet_rule_id);

			stmt4.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.eph_node_id)) {

			throw new MessageException(1000, "invalid eph_node_id");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		int index = 1;

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		String sql2 = "INSERT INTO bbb_misc.bbb_facet_rules ( " +
				"rule_id,  " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		String sql3 = "UPDATE bbb_misc.bbb_facet_rules SET " +
			     	"facet_rule_name = ?,  " +
				"facet_value_pair_list = ?,  " +
				"create_user = 'APPADMIN', " +
				"create_date = SYSDATE, " + 
				"last_mod_user = 'APPADMIN', " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? " +
				"AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_cat_eph WHERE list_cat_id = ? AND rule_id = ? AND facet_rule_id = ?) ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

			if (utility.isBlank(rec.facet_rule_id)) {

				if (!utility.isBlank(rec.facet_rule_name)) {

					// facet_rule_id is NULL and facet_rule_name is not NULL 
					// insert new facet_rule
				
      					rs1 = stmt1.executeQuery(sql1);

					if (rs1.next()) {

						rec.facet_rule_id = rs1.getString(1);
					}

				//	rec.facet_value_pair_list = "";

					stmt2 = conn.prepareStatement(sql2);
					stmt2.setString(1, rec.facet_rule_id);
					stmt2.setString(2, rec.facet_rule_name);
					stmt2.setString(3, rec.facet_value_pair_list);

					stmt2.executeUpdate();
				}
			}

			else if (!utility.isBlank(rec.facet_rule_id) && !rec.facet_rule_id.equals("0")) {

				// facet_rule_id is not NULL and not 0
				// update facet_rule only if it is already attached

				stmt3 = conn.prepareStatement(sql3);
				stmt3.setString(1, rec.facet_rule_name);
				stmt3.setString(2, rec.facet_value_pair_list);
				stmt3.setString(3, rec.facet_rule_id);
				stmt3.setString(4, rec.list_cat_id);
				stmt3.setString(5, rec.rule_id);
				stmt3.setString(6, rec.facet_rule_id);

				stmt3.executeUpdate();
			}

			// update the rule

			String sql4 = "UPDATE bbb_misc.bbb_list_cat_eph SET " + 
				"eph_node_id = ?,  ";

				if (utility.isBlank(rec.facet_rule_id) || !rec.facet_rule_id.equals("0") ) {

					sql4 += "facet_rule_id = ?,  ";
				}

			 sql4 += "create_user = 'APPADMIN', " + 
				"create_date = SYSDATE, " + 
				"last_mod_user = 'APPADMIN', " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? AND list_cat_id = ? ";

			stmt4 = conn.prepareStatement(sql4);
			stmt4.setString(index++, rec.eph_node_id);
			if (utility.isBlank(rec.facet_rule_id) || !rec.facet_rule_id.equals("0") ) {
				stmt4.setString(index++, rec.facet_rule_id);
			}
			stmt4.setString(index++, rec.rule_id);
			stmt4.setString(index++, rec.list_cat_id);

			stmt4.executeUpdate();


		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;


		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "DELETE FROM bbb_misc.bbb_list_cat_eph WHERE rule_id = ? AND list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.rule_id);
			stmt1.setString(2, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_cat_eph_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public String eph_node_id;
		public String eph_display_name;
		public String eph_nodes;
		public ArrayList eph_nodes_array;
		public String facet_rule_id;
		public String facet_rule_name;
		public String facet_value_pair_list;
		public String facets;
		public ArrayList facets_array;
		public long sequence_num;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}

	public class eph_node {

		public String eph_node_id;
		public String display_name;
	}

	public class facet {

		public String facet_id;
		public String facet_name;
		public String facet_value_id;
		public String facet_value_name;
	}
}

public class BBB_LIST_RULES_EPH_CAT implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_RULES_EPH_CAT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}
/*
		String sql = "WITH facets AS ( " +
		"SELECT DISTINCT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " +
		"facet_value_id, " +
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM ( " +
		"SELECT " + 
		"rule_id, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 1) FACET_ID, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 2) FACET_VALUE_ID " +
		"FROM bbb_misc.bbb_list_rules " +
		"CONNECT BY REGEXP_SUBSTR(facet_value_pair_list, '[^,]+', 1, LEVEL) IS NOT NULL " +
		") f )" +
*/
		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_list_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +
		"SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"b.eph_node_id, " +
			"(SELECT display_name FROM bbb_misc.bbb_eph_node WHERE eph_node_id = b.eph_node_id) eph_display_name, " +
			"c.rule_type_cd, " +
			"c.facet_rule_name, " +
			"c.facet_value_pair_list, " + 
			"a.sequence_num, " +
			"(SELECT LISTAGG(child_node_id || CHR(9) || display_name || CHR(9) || (SELECT COUNT(*) FROM bbb_misc.bbb_eph_node_parent WHERE parent_node_id = n2.eph_node_id), CHR(10)) WITHIN GROUP (ORDER BY LEVEL DESC) " + 
			"FROM ( " + 
			"SELECT child_node_id, parent_node_id " +  
			"FROM bbb_misc.bbb_eph_node_parent " +  
			"UNION " + 
			"SELECT eph_node_id, NULL " +  
			"FROM bbb_misc.bbb_eph_node WHERE is_root_node = 1 " + 
			") n1, bbb_misc.bbb_eph_node n2 " + 
			"WHERE n1.child_node_id = n2.eph_node_id " + 
			"START WITH child_node_id = b.eph_node_id " + 
			"CONNECT BY PRIOR parent_node_id = child_node_id) eph_nodes, " +
			"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = a.rule_id) facets, " +
			"(SELECT COUNT(*) FROM bbb_misc.bbb_eph_node_parent WHERE parent_node_id = b.eph_node_id) children, " +
			"c.create_user, " +
			"c.create_date, " +
			"c.last_mod_user, " +
			"c.last_mod_date " +
		"FROM bbb_misc.bbb_list_rules_eph_cat a, bbb_misc.bbb_list_rules_eph b, bbb_misc.bbb_list_rules c " +
		"WHERE a.list_cat_id = ? AND a.rule_id = b.rule_id AND a.rule_id = c.rule_id ORDER BY NVL(a.sequence_num, 999999) "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.rule_type_cd = rs.getString("rule_type_cd");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.eph_node_id = rs.getString("eph_node_id");
				xrec.eph_display_name = rs.getString("eph_display_name");
				xrec.eph_nodes = rs.getString("eph_nodes");
				xrec.facets = rs.getString("facets");
				xrec.facet_value_pair_list = rs.getString("facet_value_pair_list");
				xrec.children = rs.getLong("children");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.eph_node_id)) {

			throw new MessageException(1000, "invalid eph_node_id");
		}

		if (rec.include_all == 1) {

			return insertAll(record);
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		String sql2 = "INSERT INTO bbb_misc.bbb_list_rules ( " +
				"rule_id,  " +
				"rule_type_cd, " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"VALUES (?, 1, ?, ?, ?, SYSDATE, ?, SYSDATE) ";

		String sql3 = "INSERT INTO bbb_misc.bbb_list_rules_eph ( " +
				"rule_id,  " +
			     	"eph_node_id)  " +
		 		"VALUES (?, ?) ";

		String sql4 = "INSERT INTO bbb_misc.bbb_list_rules_eph_cat ( " +
				"rule_id,  " +
				"list_cat_id,  " +
				"sequence_num)  " +
		 		"VALUES (?, ?, REGEXP_REPLACE(?, '[^0-9]', '')) ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(sql1);

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.rule_id);
			stmt2.setString(2, rec.facet_rule_name);
			stmt2.setString(3, rec.facet_value_pair_list);
			stmt2.setString(4, rec.create_user);
			stmt2.setString(5, rec.last_mod_user);
			stmt2.executeUpdate();

			stmt3 = conn.prepareStatement(sql3);
			stmt3.setString(1, rec.rule_id);
			stmt3.setString(2, rec.eph_node_id);
			stmt3.executeUpdate();

			stmt4 = conn.prepareStatement(sql4);
			stmt4.setString(1, rec.rule_id);
			stmt4.setString(2, rec.list_cat_id);
			stmt4.setString(3, rec.rule_id);
			stmt4.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	private ArrayList insertAll(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.eph_node_id)) {

			throw new MessageException(1000, "invalid eph_node_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		Statement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmt6 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ArrayList list = new ArrayList();
		Record xrec = null;

		String sql1 = "UPDATE bbb_misc.bbb_list_category SET last_mod_user = last_mod_user WHERE list_cat_id = ? ";

        	String sql2 = "SELECT child_node_id FROM (SELECT child_node_id, level lvl FROM " +
        		"(SELECT child_node_id, parent_node_id " +   
        		"FROM bbb_misc.bbb_eph_node_parent np " + 
            		"UNION " + 
            		"SELECT eph_node_id, NULL " +   
            		"FROM bbb_misc.bbb_eph_node n WHERE is_root_node = 1 " + 
			"AND NOT EXISTS (SELECT 1 FROM bbb_misc.bbb_eph_node_parent WHERE n.eph_node_id = child_node_id) " +
            		") n1, bbb_misc.bbb_eph_node n2 " +  
            		"WHERE n1.child_node_id = n2.eph_node_id " +  
            		"START WITH child_node_id = ? " + 
            		"CONNECT BY PRIOR child_node_id = parent_node_id ) " +
			"WHERE child_node_id NOT IN (SELECT eph_node_id FROM bbb_misc.bbb_list_rules_eph_cat a, bbb_misc.bbb_list_rules_eph b  WHERE a.rule_id = b.rule_id AND list_cat_id = ?) " +
			"ORDER BY lvl ";

		String sql3 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		String sql4 = "INSERT INTO bbb_misc.bbb_list_rules ( " +
				"rule_id,  " +
				"rule_type_cd, " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"VALUES (?, 1, ?, ?, ?, SYSDATE, ?, SYSDATE) ";

		String sql5 = "INSERT INTO bbb_misc.bbb_list_rules_eph ( " +
				"rule_id,  " +
			     	"eph_node_id)  " +
		 		"VALUES (?, ?) ";

		String sql6 = "INSERT INTO bbb_misc.bbb_list_rules_eph_cat ( " +
				"rule_id,  " +
				"list_cat_id,  " +
				"sequence_num)  " +
		 		"VALUES (?, ?, REGEXP_REPLACE(?, '[^0-9]', '')) ";

		try {
			conn = dba.getConnection();

			conn.setAutoCommit(false);

			// blocking update
			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.executeUpdate();

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.eph_node_id);
			stmt2.setString(2, rec.list_cat_id);
      			rs1 = stmt2.executeQuery();

			while (rs1.next()) {

				xrec = new Record();
				xrec.list_cat_id = rec.list_cat_id;
				xrec.create_user = rec.create_user;
				xrec.last_mod_user = rec.last_mod_user;
				xrec.facet_rule_name = rec.facet_rule_name;
				xrec.facet_value_pair_list = rec.facet_value_pair_list;
				xrec.eph_node_id = rs1.getString(1);

				stmt3 = conn.createStatement();
      				rs2 = stmt3.executeQuery(sql3);

				if (rs2.next()) {

					xrec.rule_id = rs2.getString(1);
				}
				xrec.sequence_num = utility.parseLong(xrec.rule_id, 0);

				list.add(xrec);

				stmt4 = conn.prepareStatement(sql4);
				stmt4.setString(1, xrec.rule_id);
				stmt4.setString(2, xrec.facet_rule_name);
				stmt4.setString(3, xrec.facet_value_pair_list);
				stmt4.setString(4, xrec.create_user);
				stmt4.setString(5, xrec.last_mod_user);
				stmt4.executeUpdate();

				stmt5 = conn.prepareStatement(sql5);
				stmt5.setString(1, xrec.rule_id);
				stmt5.setString(2, xrec.eph_node_id);
				stmt5.executeUpdate();

				stmt6 = conn.prepareStatement(sql6);
				stmt6.setString(1, xrec.rule_id);
				stmt6.setString(2, xrec.list_cat_id);
				stmt6.setString(3, xrec.rule_id);
				stmt6.executeUpdate();
			}

			conn.commit();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();
				if (stmt5 != null) stmt5.close();
				if (stmt6 != null) stmt6.close();

			} catch(SQLException ex) {}

			if (conn != null) {
				try {
					conn.rollback();
				} catch(SQLException ex) {}

				conn.setAutoCommit(true);
				conn.close();
			}
		}

		xrec = new Record();
		xrec.rule_id = "0";
		xrec.list_cat_id = rec.list_cat_id;
		xrec.sequence_num = 0;
		xsequence(xrec);

		return list;
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.eph_node_id)) {

			throw new MessageException(1000, "invalid eph_node_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;
		int index = 1;

		String sql1 = "UPDATE bbb_misc.bbb_list_rules_eph SET " +
			     	"eph_node_id = ? " +
		 		"WHERE rule_id = ? ";

		String sql2 = "UPDATE bbb_misc.bbb_list_rules SET " +
			     	"facet_rule_name = ?,  ";

			if (Config.FACET_FILTER) {

				sql2 += "facet_value_pair_list = (SELECT " + 
				"LISTAGG(facet_id || ':' || facet_value_id, ',') WITHIN GROUP (ORDER BY facet_id) facets " +
				"FROM " + 
				"(SELECT " +
				"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
				"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " + 
				"FROM xmltable(('\"' || REPLACE(?, ',', '\",\"') || '\"')) " + 
				") f " +
				"WHERE EXISTS (SELECT 1 FROM bbb_misc.bbb_list_eph_facets WHERE f.facet_id = facet_id AND f.facet_value_id = facet_value_id AND eph_id = ?) " +
				"), ";

			}
			else {
				sql2 += "facet_value_pair_list = ?,  ";
			}
			
			sql2 += "last_mod_user = ?, " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.eph_node_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.executeUpdate();

			index = 1;
			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(index++, rec.facet_rule_name);
			stmt2.setString(index++, rec.facet_value_pair_list);
			if (Config.FACET_FILTER) {
				stmt2.setString(index++, rec.eph_node_id);
			}
			stmt2.setString(index++, rec.last_mod_user);
			stmt2.setString(index++, rec.rule_id);
			stmt2.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		if (rec.include_all == 1) {

			return insertAll(record);
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs1 = null;

		String sql1 = "DELETE FROM bbb_misc.bbb_list_rules WHERE rule_id = ? ";
		String sql2 = "DELETE FROM bbb_misc.bbb_list_rules_eph WHERE rule_id = ? ";
		String sql3 = "DELETE FROM bbb_misc.bbb_list_rules_eph_cat WHERE rule_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.rule_id);
			stmt1.executeUpdate();

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.rule_id);
			stmt2.executeUpdate();

			stmt3 = conn.prepareStatement(sql3);
			stmt3.setString(1, rec.rule_id);
			stmt3.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_rules_eph_cat a " +
			"SET sequence_num = (SELECT rn-1 FROM ( " +
			"SELECT list_cat_id, rule_id, row_number() " + 
			"OVER (ORDER BY CASE WHEN list_cat_id = ? AND rule_id = ? THEN ? ELSE sequence_num END, " + 
			"CASE WHEN list_cat_id = ? AND rule_id = ? THEN (SELECT ?-sequence_num FROM bbb_misc.bbb_list_rules_eph_cat WHERE list_cat_id = ? AND rule_id = ?) ELSE 0 END, list_cat_id, rule_id) rn " + 
			"FROM bbb_misc.bbb_list_rules_eph_cat " +
			"WHERE list_cat_id = ?) " + 
			"WHERE list_cat_id = a.list_cat_id " + 
			"AND rule_id = a.rule_id) " +
			"WHERE list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.setString(4, rec.list_cat_id);
			stmt1.setString(5, rec.rule_id);
			stmt1.setLong(6, rec.sequence_num);
			stmt1.setString(7, rec.list_cat_id);
			stmt1.setString(8, rec.rule_id);
			stmt1.setString(9, rec.list_cat_id);
			stmt1.setString(10, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence2(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_rules_eph_cat_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public String eph_node_id;
		public String eph_display_name;
		public String eph_nodes;
		public ArrayList eph_nodes_array;
		public String rule_type_cd;
		public String facet_rule_name;
		public String facet_value_pair_list;
		public String facets;
		public ArrayList facets_array;
		public long sequence_num;
		public long include_all;
		public long children;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}

	public class eph_node {

		public String eph_node_id;
		public String display_name;
	}

	public class facet {

		public String facet_id;
		public String facet_name;
		public String facet_value_id;
		public String facet_value_name;
	}
}

public class BBB_LIST_CAT_JDA implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_CAT_JDA(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}
/*
		String sql = "WITH facets AS ( " +
		"SELECT DISTINCT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " +
		"facet_value_id, " +
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM ( " +
		"SELECT " + 
		"rule_id, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 1) FACET_ID, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 2) FACET_VALUE_ID " +
		"FROM bbb_misc.bbb_facet_rules " +
		"CONNECT BY REGEXP_SUBSTR(facet_value_pair_list, '[^,]+', 1, LEVEL) IS NOT NULL " +
		") f )" +
*/
		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_facet_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +

		"SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"a.jda_dept_id, " +
			"(SELECT descrip FROM bbb_switch_a.bbb_dept WHERE jda_dept_id = a.jda_dept_id) jda_dept_descrip, " +
			"a.jda_sub_dept_id, " +
			"(SELECT descrip FROM bbb_switch_a.bbb_sub_dept WHERE jda_dept_id = a.jda_dept_id AND jda_sub_dept_id = a.jda_sub_dept_id) jda_sub_dept_descrip, " +
			"(SELECT COUNT(*) FROM bbb_switch_a.bbb_sub_dept WHERE jda_dept_id = a.jda_dept_id) jda_sub_dept_count, " +
			"a.jda_class, " +
			"(SELECT descrip FROM bbb_switch_a.bbb_class WHERE jda_dept_id = a.jda_dept_id AND jda_sub_dept_id = REGEXP_REPLACE(a.jda_sub_dept_id, '[0-9]*\\_', '') AND jda_class = a.jda_class) jda_class_descrip, " +
			"(SELECT COUNT(*) FROM bbb_switch_a.bbb_class WHERE jda_dept_id = a.jda_dept_id AND jda_sub_dept_id = REGEXP_REPLACE(a.jda_sub_dept_id, '[0-9]*\\_', '')) jda_class_count, " +
			"a.facet_rule_id, " +
			"b.facet_rule_name, " +
			"b.facet_value_pair_list, " + 
			"a.sequence_num, " +
			"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = a.facet_rule_id) facets, " +
			"a.create_user, " +
			"a.create_date, " +
			"a.last_mod_user, " +
			"a.last_mod_date " +
		"FROM bbb_misc.bbb_list_cat_jda a, bbb_misc.bbb_facet_rules b " +
		"WHERE a.facet_rule_id = b.rule_id(+) AND a.list_cat_id = ? ORDER BY NVL(sequence_num, 999999) "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.facet_rule_id = rs.getString("facet_rule_id");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.jda_dept_id = rs.getString("jda_dept_id");
				xrec.jda_dept_descrip = rs.getString("jda_dept_descrip");
				xrec.jda_sub_dept_id = rs.getString("jda_sub_dept_id");
				xrec.jda_sub_dept_descrip = rs.getString("jda_sub_dept_descrip");
				xrec.jda_sub_dept_count = rs.getLong("jda_sub_dept_count");
				xrec.jda_class = rs.getString("jda_class");
				xrec.jda_class_descrip = rs.getString("jda_class_descrip");
				xrec.jda_class_count = rs.getLong("jda_class_count");
				xrec.facets = rs.getString("facets");
				xrec.facet_value_pair_list = rs.getString("facet_value_pair_list");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.jda_dept_id)) {

			throw new MessageException(1000, "invalid jda_dept_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		if (utility.isBlank(rec.facet_rule_id)) {

			// for bbb_list_cat_facet only
			if (utility.isBlank(rec.facet_rule_name)) {

			//	throw new MessageException(1000, "invalid facet_rule_name");
			}
		}

		Connection conn = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql2 = "INSERT INTO bbb_misc.bbb_facet_rules ( " +
				"rule_id,  " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		String sql3 = "INSERT INTO bbb_misc.bbb_list_cat_jda ( " +
				"rule_id,  " +
				"list_cat_id,  " +
			     	"jda_dept_id,  " +
				"jda_sub_dept_id,  " +
				"jda_class,  " +
				"facet_rule_id,  " +
				"sequence_num, " + 
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, ?, ?, (SELECT MAX(rule_id) FROM bbb_misc.bbb_facet_rules WHERE rule_id = ?), " +
				"2, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(sql1);

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			if (utility.isBlank(rec.facet_rule_id) && !utility.isBlank(rec.facet_rule_name)) {

				stmt2 = conn.createStatement();

      				rs2 = stmt2.executeQuery(sql1);

				if (rs2.next()) {

					// facet_rule_id is NULL and facet_rule_name is not NULL 
					// insert new facet_rule

					rec.facet_rule_id = rs1.getString(1);

				//	rec.facet_value_pair_list = "";

					stmt3 = conn.prepareStatement(sql2);
					stmt3.setString(1, rec.facet_rule_id);
					stmt3.setString(2, rec.facet_rule_name);
					stmt3.setString(3, rec.facet_value_pair_list);

					stmt3.executeUpdate();
				}
			}

			stmt4 = conn.prepareStatement(sql3);
			stmt4.setString(1, rec.rule_id);
			stmt4.setString(2, rec.list_cat_id);
			stmt4.setString(3, rec.jda_dept_id);
			stmt4.setString(4, rec.jda_sub_dept_id);
			stmt4.setString(5, rec.jda_class);
			stmt4.setString(6, rec.facet_rule_id);

			stmt4.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.jda_dept_id)) {

			throw new MessageException(1000, "invalid jda_dept_id");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		int index = 1;

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		String sql2 = "INSERT INTO bbb_misc.bbb_facet_rules ( " +
				"rule_id,  " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		String sql3 = "UPDATE bbb_misc.bbb_facet_rules SET " +
			     	"facet_rule_name = ?,  " +
				"facet_value_pair_list = ?,  " +
				"create_user = 'APPADMIN', " +
				"create_date = SYSDATE, " + 
				"last_mod_user = 'APPADMIN', " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? " +
				"AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_cat_jda WHERE list_cat_id = ? AND rule_id = ? AND facet_rule_id = ?) ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

			if (utility.isBlank(rec.facet_rule_id)) {

				if (!utility.isBlank(rec.facet_rule_name)) {

					// facet_rule_id is NULL and facet_rule_name is not NULL 
					// insert new facet_rule
				
      					rs1 = stmt1.executeQuery(sql1);

					if (rs1.next()) {

						rec.facet_rule_id = rs1.getString(1);
					}

				//	rec.facet_value_pair_list = "";

					stmt2 = conn.prepareStatement(sql2);
					stmt2.setString(1, rec.facet_rule_id);
					stmt2.setString(2, rec.facet_rule_name);
					stmt2.setString(3, rec.facet_value_pair_list);

					stmt2.executeUpdate();
				}
			}

			else if (!utility.isBlank(rec.facet_rule_id) && !rec.facet_rule_id.equals("0")) {

				// facet_rule_id is not NULL and not 0
				// update facet_rule only if it is already attached

				stmt3 = conn.prepareStatement(sql3);
				stmt3.setString(1, rec.facet_rule_name);
				stmt3.setString(2, rec.facet_value_pair_list);
				stmt3.setString(3, rec.facet_rule_id);
				stmt3.setString(4, rec.list_cat_id);
				stmt3.setString(5, rec.rule_id);
				stmt3.setString(6, rec.facet_rule_id);

				stmt3.executeUpdate();
			}

			// update the rule

			String sql4 = "UPDATE bbb_misc.bbb_list_cat_jda SET " + 
				"jda_dept_id = ?,  " + 
				"jda_sub_dept_id = ?,  " +
				"jda_class = ?,  ";
				if (utility.isBlank(rec.facet_rule_id) || !rec.facet_rule_id.equals("0") ) {

					sql4 += "facet_rule_id = ?,  ";
				}

			 sql4 += "create_user = 'APPADMIN', " + 
				"create_date = SYSDATE, " + 
				"last_mod_user = 'APPADMIN', " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? AND list_cat_id = ? ";

			stmt4 = conn.prepareStatement(sql4);
			stmt4.setString(index++, rec.jda_dept_id);
			stmt4.setString(index++, rec.jda_sub_dept_id);
			stmt4.setString(index++, rec.jda_class);
			if (utility.isBlank(rec.facet_rule_id) || !rec.facet_rule_id.equals("0") ) {
				stmt4.setString(index++, rec.facet_rule_id);
			}
			stmt4.setString(index++, rec.rule_id);
			stmt4.setString(index++, rec.list_cat_id);

			stmt4.executeUpdate();


		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;


		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "DELETE FROM bbb_misc.bbb_list_cat_jda WHERE rule_id = ? AND list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.rule_id);
			stmt1.setString(2, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_cat_jda_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public String jda_dept_id;
		public String jda_dept_descrip;
		public String jda_sub_dept_id;
		public String jda_sub_dept_descrip;
		public long jda_sub_dept_count;
		public String jda_class;
		public String jda_class_descrip;
		public long jda_class_count;
		public String facet_rule_id;
		public String facet_rule_name;
		public String facet_value_pair_list;
		public String facets;
		public ArrayList facets_array;
		public long sequence_num;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}

	public class facet {

		public String facet_id;
		public String facet_name;
		public String facet_value_id;
		public String facet_value_name;
	}
}

public class BBB_LIST_RULES_JDA_CAT implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_RULES_JDA_CAT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}
/*
		String sql = "WITH facets AS ( " +
		"SELECT DISTINCT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " +
		"facet_value_id, " +
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM ( " +
		"SELECT " + 
		"rule_id, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 1) FACET_ID, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 2) FACET_VALUE_ID " +
		"FROM bbb_misc.bbb_list_rules " +
		"CONNECT BY REGEXP_SUBSTR(facet_value_pair_list, '[^,]+', 1, LEVEL) IS NOT NULL " +
		") f )" +
*/
		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_list_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +

		"SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"b.jda_dept_id, " +
			"(SELECT descrip FROM bbb_switch_a.bbb_dept WHERE jda_dept_id = b.jda_dept_id) jda_dept_descrip, " +
			"b.jda_sub_dept_id, " +
			"(SELECT descrip FROM bbb_switch_a.bbb_sub_dept WHERE jda_dept_id = b.jda_dept_id AND jda_sub_dept_id = b.jda_sub_dept_id) jda_sub_dept_descrip, " +
			"(SELECT COUNT(*) FROM bbb_switch_a.bbb_sub_dept WHERE jda_dept_id = b.jda_dept_id) jda_sub_dept_count, " +
			"b.jda_class, " +
			"(SELECT descrip FROM bbb_switch_a.bbb_class WHERE jda_dept_id = b.jda_dept_id AND jda_sub_dept_id = REGEXP_REPLACE(b.jda_sub_dept_id, '[0-9]*\\_', '') AND jda_class = b.jda_class) jda_class_descrip, " +
			"(SELECT COUNT(*) FROM bbb_switch_a.bbb_class WHERE jda_dept_id = b.jda_dept_id AND jda_sub_dept_id = REGEXP_REPLACE(b.jda_sub_dept_id, '[0-9]*\\_', '')) jda_class_count, " +
			"c.rule_type_cd, " +
			"c.facet_rule_name, " +
			"c.facet_value_pair_list, " + 
			"a.sequence_num, " +
			"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = c.rule_id) facets, " +
			"c.create_user, " +
			"c.create_date, " +
			"c.last_mod_user, " +
			"c.last_mod_date " +
		"FROM bbb_misc.bbb_list_rules_jda_cat a, bbb_misc.bbb_list_rules_jda b, bbb_misc.bbb_list_rules c " +
		"WHERE a.list_cat_id = ? AND a.rule_id = b.rule_id AND a.rule_id = c.rule_id ORDER BY NVL(a.sequence_num, 999999) "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.rule_type_cd = rs.getString("rule_type_cd");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.jda_dept_id = rs.getString("jda_dept_id");
				xrec.jda_dept_descrip = rs.getString("jda_dept_descrip");
				xrec.jda_sub_dept_id = rs.getString("jda_sub_dept_id");
				xrec.jda_sub_dept_descrip = rs.getString("jda_sub_dept_descrip");
				xrec.jda_sub_dept_count = rs.getLong("jda_sub_dept_count");
				xrec.jda_class = rs.getString("jda_class");
				xrec.jda_class_descrip = rs.getString("jda_class_descrip");
				xrec.jda_class_count = rs.getLong("jda_class_count");
				xrec.facets = rs.getString("facets");
				xrec.facet_value_pair_list = rs.getString("facet_value_pair_list");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.jda_dept_id)) {

			throw new MessageException(1000, "invalid jda_dept_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		String sql2 = "INSERT INTO bbb_misc.bbb_list_rules ( " +
				"rule_id,  " +
				"rule_type_cd, " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"VALUES (?, 2, ?, ?, ?, SYSDATE, ?, SYSDATE) ";

		String sql3 = "INSERT INTO bbb_misc.bbb_list_rules_jda ( " +
				"rule_id,  " +
			     	"jda_dept_id,  " +
				"jda_sub_dept_id,  " +
				"jda_class)  " +
		 		"VALUES (?, ?, ?, ?) ";

		String sql4 = "INSERT INTO bbb_misc.bbb_list_rules_jda_cat ( " +
				"rule_id,  " +
				"list_cat_id,  " +
				"sequence_num)  " +
		 		"VALUES (?, ?, REGEXP_REPLACE(?, '[^0-9]', '')) ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(sql1);

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.rule_id);
			stmt2.setString(2, rec.facet_rule_name);
			stmt2.setString(3, rec.facet_value_pair_list);
			stmt2.setString(4, rec.create_user);
			stmt2.setString(5, rec.last_mod_user);
			stmt2.executeUpdate();

			stmt3 = conn.prepareStatement(sql3);
			stmt3.setString(1, rec.rule_id);
			stmt3.setString(2, rec.jda_dept_id);
			stmt3.setString(3, rec.jda_sub_dept_id);
			stmt3.setString(4, rec.jda_class);
			stmt3.executeUpdate();

			stmt4 = conn.prepareStatement(sql4);
			stmt4.setString(1, rec.rule_id);
			stmt4.setString(2, rec.list_cat_id);
			stmt4.setString(3, rec.rule_id);
			stmt4.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.jda_dept_id)) {

			throw new MessageException(1000, "invalid jda_dept_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;
		int index = 1;

		String sql1 = "UPDATE bbb_misc.bbb_list_rules_jda SET " +
			     	"jda_dept_id = ?,  " +
				"jda_sub_dept_id = ?, " +
				"jda_class = ?  " +
		 		"WHERE rule_id = ? ";

		String sql2 = "UPDATE bbb_misc.bbb_list_rules SET " +
			     	"facet_rule_name = ?,  ";

			if (Config.FACET_FILTER) {

				sql2 += "facet_value_pair_list = (SELECT " + 
				"LISTAGG(facet_id || ':' || facet_value_id, ',') WITHIN GROUP (ORDER BY facet_id) facets " +
				"FROM " + 
				"(SELECT " +
				"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
				"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " + 
				"FROM xmltable(('\"' || REPLACE(?, ',', '\",\"') || '\"')) " + 
				") f " +
				"WHERE EXISTS (SELECT 1 FROM bbb_misc.bbb_list_jda_facets WHERE f.facet_id = facet_id AND f.facet_value_id = facet_value_id ";
				
				if (!utility.isBlank(rec.jda_dept_id)) {

					if (!utility.isBlank(rec.jda_sub_dept_id)) {
						
						sql2 += "AND jda_sub_dept_id = ? ";

						if (!utility.isBlank(rec.jda_class)) {

							sql2 += "AND jda_class = ? ";	
						}
					}
					else {
						sql2 += "AND jda_dept_id = ? ";	
					}
				} 

				sql2 += ")), ";
			}
			else {
				sql2 += "facet_value_pair_list = ?,  ";
			}

			sql2 += "last_mod_user = ?, " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? ";
		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.jda_dept_id);
			stmt1.setString(2, rec.jda_sub_dept_id);
			stmt1.setString(3, rec.jda_class);
			stmt1.setString(4, rec.rule_id);
			stmt1.executeUpdate();

			index = 1;
			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(index++, rec.facet_rule_name);
			stmt2.setString(index++, rec.facet_value_pair_list);

			if (Config.FACET_FILTER) {

				if (!utility.isBlank(rec.jda_dept_id)) {

					if (!utility.isBlank(rec.jda_sub_dept_id)) {
						
						stmt2.setString(index++, rec.jda_sub_dept_id);

						if (!utility.isBlank(rec.jda_class)) {

							stmt2.setString(index++, rec.jda_class);	
						}
					}
					else {
						stmt2.setString(index++, rec.jda_dept_id);	
					}
				}
			}

			stmt2.setString(index++, rec.last_mod_user);
			stmt2.setString(index++, rec.rule_id);
			stmt2.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs1 = null;

		String sql1 = "DELETE FROM bbb_misc.bbb_list_rules WHERE rule_id = ? ";
		String sql2 = "DELETE FROM bbb_misc.bbb_list_rules_jda WHERE rule_id = ? ";
		String sql3 = "DELETE FROM bbb_misc.bbb_list_rules_jda_cat WHERE rule_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.rule_id);
			stmt1.executeUpdate();

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.rule_id);
			stmt2.executeUpdate();

			stmt3 = conn.prepareStatement(sql3);
			stmt3.setString(1, rec.rule_id);
			stmt3.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_rules_jda_cat a " +
			"SET sequence_num = (SELECT rn-1 FROM ( " +
			"SELECT list_cat_id, rule_id, row_number() " + 
			"OVER (ORDER BY CASE WHEN list_cat_id = ? AND rule_id = ? THEN ? ELSE sequence_num END, " + 
			"CASE WHEN list_cat_id = ? AND rule_id = ? THEN (SELECT ?-sequence_num FROM bbb_misc.bbb_list_rules_jda_cat WHERE list_cat_id = ? AND rule_id = ?) ELSE 0 END, list_cat_id, rule_id) rn " + 
			"FROM bbb_misc.bbb_list_rules_jda_cat " +
			"WHERE list_cat_id = ?) " + 
			"WHERE list_cat_id = a.list_cat_id " + 
			"AND rule_id = a.rule_id) " +
			"WHERE list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.setString(4, rec.list_cat_id);
			stmt1.setString(5, rec.rule_id);
			stmt1.setLong(6, rec.sequence_num);
			stmt1.setString(7, rec.list_cat_id);
			stmt1.setString(8, rec.rule_id);
			stmt1.setString(9, rec.list_cat_id);
			stmt1.setString(10, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence2(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_rules_jda_cat_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public String jda_dept_id;
		public String jda_dept_descrip;
		public String jda_sub_dept_id;
		public String jda_sub_dept_descrip;
		public long jda_sub_dept_count;
		public String jda_class;
		public String jda_class_descrip;
		public long jda_class_count;
		public String rule_type_cd;
		public String facet_rule_name;
		public String facet_value_pair_list;
		public String facets;
		public ArrayList facets_array;
		public long sequence_num;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}

	public class facet {

		public String facet_id;
		public String facet_name;
		public String facet_value_id;
		public String facet_value_name;
	}
}

public class BBB_LIST_CAT_FACET implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_CAT_FACET(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}
/*
		String sql = "WITH facets AS ( " +
		"SELECT DISTINCT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " +
		"facet_value_id, " +
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM ( " +
		"SELECT " + 
		"rule_id, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 1) FACET_ID, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 2) FACET_VALUE_ID " +
		"FROM bbb_misc.bbb_facet_rules " +
		"CONNECT BY REGEXP_SUBSTR(facet_value_pair_list, '[^,]+', 1, LEVEL) IS NOT NULL " +
		") f )" +
*/
		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_facet_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +

		"SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"a.facet_rule_id, " +
			"b.facet_rule_name, " +
			"b.facet_value_pair_list, " + 
			"a.sequence_num, " +
			"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = a.facet_rule_id) facets, " +
			"a.create_user, " +
			"a.create_date, " +
			"a.last_mod_user, " +
			"a.last_mod_date " +
		"FROM bbb_misc.bbb_list_cat_facet a, bbb_misc.bbb_facet_rules b " +
		"WHERE a.facet_rule_id = b.rule_id(+) AND a.list_cat_id = ? ORDER BY NVL(sequence_num, 999999) "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.facet_rule_id = rs.getString("facet_rule_id");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.facets = rs.getString("facets");
				xrec.facet_value_pair_list = rs.getString("facet_value_pair_list");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		if (utility.isBlank(rec.facet_rule_id)) {

			// for bbb_list_cat_facet only
			if (utility.isBlank(rec.facet_rule_name)) {

				throw new MessageException(1000, "invalid facet_rule_name");
			}
		}

		Connection conn = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql2 = "INSERT INTO bbb_misc.bbb_facet_rules ( " +
				"rule_id,  " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		String sql3 = "INSERT INTO bbb_misc.bbb_list_cat_facet ( " +
				"rule_id,  " +
				"list_cat_id,  " +
				"facet_rule_id,  " +
				"sequence_num, " + 
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, (SELECT MAX(rule_id) FROM bbb_misc.bbb_facet_rules WHERE rule_id = ?), " +
				"2, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(sql1);

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			if (utility.isBlank(rec.facet_rule_id) && !utility.isBlank(rec.facet_rule_name)) {

				stmt2 = conn.createStatement();

      				rs2 = stmt2.executeQuery(sql1);

				if (rs2.next()) {

					// facet_rule_id is NULL and facet_rule_name is not NULL 
					// insert new facet_rule

					rec.facet_rule_id = rs1.getString(1);

				//	rec.facet_value_pair_list = "";

					stmt3 = conn.prepareStatement(sql2);
					stmt3.setString(1, rec.facet_rule_id);
					stmt3.setString(2, rec.facet_rule_name);
					stmt3.setString(3, rec.facet_value_pair_list);

					stmt3.executeUpdate();
				}
			}

			stmt4 = conn.prepareStatement(sql3);
			stmt4.setString(1, rec.rule_id);
			stmt4.setString(2, rec.list_cat_id);
			stmt4.setString(3, rec.facet_rule_id);

			stmt4.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs1 = null;
		int index = 1;

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		String sql2 = "INSERT INTO bbb_misc.bbb_facet_rules ( " +
				"rule_id,  " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"SELECT ?, ?, ?, 'APPADMIN', SYSDATE, 'APPADMIN', SYSDATE FROM DUAL ";

		String sql3 = "UPDATE bbb_misc.bbb_facet_rules SET " +
			     	"facet_rule_name = ?,  " +
				"facet_value_pair_list = ?,  " +
				"create_user = 'APPADMIN', " +
				"create_date = SYSDATE, " + 
				"last_mod_user = 'APPADMIN', " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? " +
				"AND EXISTS (SELECT 1 FROM bbb_misc.bbb_list_cat_facet WHERE list_cat_id = ? AND rule_id = ? AND facet_rule_id = ?) ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

			if (utility.isBlank(rec.facet_rule_id)) {

				if (!utility.isBlank(rec.facet_rule_name)) {

					// facet_rule_id is NULL and facet_rule_name is not NULL 
					// insert new facet_rule
				
      					rs1 = stmt1.executeQuery(sql1);

					if (rs1.next()) {

						rec.facet_rule_id = rs1.getString(1);
					}

				//	rec.facet_value_pair_list = "";

					stmt2 = conn.prepareStatement(sql2);
					stmt2.setString(1, rec.facet_rule_id);
					stmt2.setString(2, rec.facet_rule_name);
					stmt2.setString(3, rec.facet_value_pair_list);

					stmt2.executeUpdate();
				}
			}

			else if (!utility.isBlank(rec.facet_rule_id) && !rec.facet_rule_id.equals("0")) {

				// facet_rule_id is not NULL and not 0
				// update facet_rule only if it is already attached

				stmt3 = conn.prepareStatement(sql3);
				stmt3.setString(1, rec.facet_rule_name);
				stmt3.setString(2, rec.facet_value_pair_list);
				stmt3.setString(3, rec.facet_rule_id);
				stmt3.setString(4, rec.list_cat_id);
				stmt3.setString(5, rec.rule_id);
				stmt3.setString(6, rec.facet_rule_id);

				stmt3.executeUpdate();
			}

			// update the rule

			String sql4 = "UPDATE bbb_misc.bbb_list_cat_facet SET "; 

				if (utility.isBlank(rec.facet_rule_id) || !rec.facet_rule_id.equals("0") ) {

					sql4 += "facet_rule_id = ?,  ";
				}

			 sql4 += "create_user = 'APPADMIN', " + 
				"create_date = SYSDATE, " + 
				"last_mod_user = 'APPADMIN', " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? AND list_cat_id = ? ";

			stmt4 = conn.prepareStatement(sql4);
			if (utility.isBlank(rec.facet_rule_id) || !rec.facet_rule_id.equals("0") ) {
				stmt4.setString(index++, rec.facet_rule_id);
			}
			stmt4.setString(index++, rec.rule_id);
			stmt4.setString(index++, rec.list_cat_id);

			stmt4.executeUpdate();


		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;


		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "DELETE FROM bbb_misc.bbb_list_cat_facet WHERE rule_id = ? AND list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.rule_id);
			stmt1.setString(2, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_cat_facet_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public String facet_rule_id;
		public String facet_rule_name;
		public String facet_value_pair_list;
		public String facets;
		public ArrayList facets_array;
		public long sequence_num;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}

	public class facet {

		public String facet_id;
		public String facet_name;
		public String facet_value_id;
		public String facet_value_name;
	}
}

public class BBB_LIST_RULES_FACET_CAT implements DataRepository {


	private DatabaseAdapter dba = null;
	private Utility utility = new Utility();

	public BBB_LIST_RULES_FACET_CAT(DatabaseAdapter dba) {

		
		this.dba = dba;
	}

	public ArrayList select(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}
/*
		String sql = "WITH facets AS ( " +
		"SELECT DISTINCT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " +
		"facet_value_id, " +
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM ( " +
		"SELECT " + 
		"rule_id, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 1) FACET_ID, " +
		"REGEXP_SUBSTR(REGEXP_SUBSTR(facet_value_pair_list,'[^,]+', 1, LEVEL), '[^:]+', 1, 2) FACET_VALUE_ID " +
		"FROM bbb_misc.bbb_list_rules " +
		"CONNECT BY REGEXP_SUBSTR(facet_value_pair_list, '[^,]+', 1, LEVEL) IS NOT NULL " +
		") f )" +
*/
		String sql = "WITH facets AS ( " + 
		"SELECT " + 
		"rule_id, " +
		"facet_id, " +
		"(SELECT description FROM bbb_core.bbb_facet_types WHERE facet_id = f.facet_id) description, " + 
		"facet_value_id, " + 
		"(SELECT facet_value_desc FROM bbb_misc.bbb_facet_value_pairs WHERE facet_id = f.facet_id AND f.facet_value_id = facet_value_id) facet_value_desc " +
		"FROM " +
		"(SELECT " + 
		"rule_id, " +
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 1)) facet_id, " + 
		"TRIM(REGEXP_SUBSTR(TRIM(column_value), '[^:]+', 1, 2)) facet_value_id " +
		"FROM bbb_misc.bbb_list_rules, xmltable(('\"' || REPLACE(facet_value_pair_list, ',', '\",\"') || '\"')) " +
		") f) " +
		"SELECT " + 
			"a.rule_id, " +
			"a.list_cat_id, " +
			"c.rule_type_cd, " +
			"c.facet_rule_name, " +
			"c.facet_value_pair_list, " + 
			"a.sequence_num, " +
			"(SELECT LISTAGG(facet_id || CHR(9) || description || CHR(13) || facet_value_id || CHR(9) || facet_value_desc, CHR(10)) WITHIN GROUP (ORDER BY description)  FROM facets WHERE rule_id = a.rule_id) facets, " +
			"c.create_user, " +
			"c.create_date, " +
			"c.last_mod_user, " +
			"c.last_mod_date " +
		"FROM bbb_misc.bbb_list_rules_facet_cat a, bbb_misc.bbb_list_rules c " +
		"WHERE a.list_cat_id = ? AND a.rule_id = c.rule_id ORDER BY NVL(a.sequence_num, 999999) "; 

		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList list = new ArrayList();

		try 
		{
			conn = dba.getConnection();

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, rec.list_cat_id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{

				Record xrec = new Record();

				xrec.rule_id = rs.getString("rule_id");
				xrec.list_cat_id = rs.getString("list_cat_id");
				xrec.rule_type_cd = rs.getString("rule_type_cd");
				xrec.facet_rule_name = rs.getString("facet_rule_name");
				xrec.sequence_num = rs.getLong("sequence_num");
				xrec.facets = rs.getString("facets");
				xrec.facet_value_pair_list = rs.getString("facet_value_pair_list");
				xrec.create_user = rs.getString("create_user");
				xrec.create_date = rs.getString("create_date");
				xrec.last_mod_user = rs.getString("last_mod_user");
				xrec.last_mod_date = rs.getString("last_mod_date");

				list.add(xrec);
			}

		} catch (Exception e) {

			throw e;
		}
		finally {

			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return list;
	}

	public ArrayList insert(Object record) throws Exception {


		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		String sql1 = Config.getNextSequence(Config.BBB_LIST_RULES_SEQUENCE);

		String sql2 = "INSERT INTO bbb_misc.bbb_list_rules ( " +
				"rule_id,  " +
				"rule_type_cd, " +
				"facet_rule_name,  " +
			     	"facet_value_pair_list,  " +
				"create_user, " + 
				"create_date, " + 
				"last_mod_user, " + 
				"last_mod_date) " +
		 		"VALUES (?, 3, ?, ?, ?, SYSDATE, ?, SYSDATE) ";

		String sql3 = "INSERT INTO bbb_misc.bbb_list_rules_facet_cat ( " +
				"rule_id,  " +
				"list_cat_id,  " +
				"sequence_num)  " +
		 		"VALUES (?, ?, REGEXP_REPLACE(?, '[^0-9]', '')) ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.createStatement();

      			rs1 = stmt1.executeQuery(sql1);

			if (rs1.next()) {

				rec.rule_id = rs1.getString(1);
			}

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.rule_id);
			stmt2.setString(2, rec.facet_rule_name);
			stmt2.setString(3, rec.facet_value_pair_list);
			stmt2.setString(4, rec.create_user);
			stmt2.setString(5, rec.last_mod_user);
			stmt2.executeUpdate();

			stmt3 = conn.prepareStatement(sql3);
			stmt3.setString(1, rec.rule_id);
			stmt3.setString(2, rec.list_cat_id);
			stmt3.setString(3, rec.rule_id);
			stmt3.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (rs2 != null) rs2.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		return xsequence(record);
	}

	public ArrayList update(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql1 = "UPDATE bbb_misc.bbb_list_rules SET " +
			     	"facet_rule_name = ?,  " +
				"facet_value_pair_list = ?,  " +
				"last_mod_user = ?, " + 
				"last_mod_date = SYSDATE " +
		 		"WHERE rule_id = ? ";
		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.facet_rule_name);
			stmt1.setString(2, rec.facet_value_pair_list);
			stmt1.setString(3, rec.last_mod_user);
			stmt1.setString(4, rec.rule_id);
			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList delete(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;

		String sql1 = "DELETE FROM bbb_misc.bbb_list_rules WHERE rule_id = ? ";
		String sql2 = "DELETE FROM bbb_misc.bbb_list_rules_facet_cat WHERE rule_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, rec.rule_id);
			stmt1.executeUpdate();

			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, rec.rule_id);
			stmt2.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		// need to re-order when an item is deleted
		rec.sequence_num = 0;
		xsequence(rec);

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "UPDATE bbb_misc.bbb_list_rules_facet_cat a " +
			"SET sequence_num = (SELECT rn-1 FROM ( " +
			"SELECT list_cat_id, rule_id, row_number() " + 
			"OVER (ORDER BY CASE WHEN list_cat_id = ? AND rule_id = ? THEN ? ELSE sequence_num END, " + 
			"CASE WHEN list_cat_id = ? AND rule_id = ? THEN (SELECT ?-sequence_num FROM bbb_misc.bbb_list_rules_facet_cat WHERE list_cat_id = ? AND rule_id = ?) ELSE 0 END, list_cat_id, rule_id) rn " + 
			"FROM bbb_misc.bbb_list_rules_facet_cat " +
			"WHERE list_cat_id = ?) " + 
			"WHERE list_cat_id = a.list_cat_id " + 
			"AND rule_id = a.rule_id) " +
			"WHERE list_cat_id = ? ";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareStatement(sql);
			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.setString(4, rec.list_cat_id);
			stmt1.setString(5, rec.rule_id);
			stmt1.setLong(6, rec.sequence_num);
			stmt1.setString(7, rec.list_cat_id);
			stmt1.setString(8, rec.rule_id);
			stmt1.setString(9, rec.list_cat_id);
			stmt1.setString(10, rec.list_cat_id);

			stmt1.executeUpdate();

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public ArrayList xsequence2(Object record) throws Exception {

		Record rec = (Record)record;

		if (utility.isBlank(rec.list_cat_id)) {

			throw new MessageException(1000, "invalid list_cat_id");
		}

		if (utility.isBlank(rec.rule_id)) {

			throw new MessageException(1000, "invalid rule_id");
		}

		if (rec.sequence_num == -1) {

			throw new MessageException(1000, "invalid sequence_num");
		}

		Connection conn = null;
		CallableStatement stmt1 = null;
		ResultSet rs1 = null;

		String sql = "{call bbb_misc.bbb_list_rules_facet_cat_xseq(?,?,?,?)}";

		try {
			conn = dba.getConnection();

			stmt1 = conn.prepareCall(sql);

			stmt1.setString(1, rec.list_cat_id);
			stmt1.setString(2, rec.rule_id);
			stmt1.setLong(3, rec.sequence_num);
			stmt1.registerOutParameter(4, OracleTypes.CURSOR);

			stmt1.executeUpdate();

			rs1 = (ResultSet) stmt1.getObject(4);

		} catch (Exception e) {

			throw e;
		}
		finally {

			try {
				if (rs1 != null) rs1.close();
				if (stmt1 != null) stmt1.close();

			} catch(SQLException ex) {}

			if (conn != null) {

				conn.close();
			}
		}

		ArrayList list = new ArrayList();
		list.add(record);

		return list;
	}

	public class Record {

		public String rule_id;
		public String list_cat_id;
		public ArrayList eph_nodes_array;
		public String rule_type_cd;
		public String facet_rule_name;
		public String facet_value_pair_list;
		public String facets;
		public ArrayList facets_array;
		public long sequence_num;
		public String create_user;
		public String create_date;
		public String last_mod_user;
		public String last_mod_date;
	}

	public class facet {

		public String facet_id;
		public String facet_name;
		public String facet_value_id;
		public String facet_value_name;
	}
}

public static class Config {

	public static boolean EXACT_SEARCH = true;
	public static boolean FACET_FILTER = true;
	public static String DATA_CENTER = "DC3";
	public static String DATA_SOURCE = "MiscDS";
	public static String BBB_SEQUENCE = "bbb_misc.bbb_list_sequence";
	public static String BBB_LIST_SEQUENCE = "bbb_misc.bbb_list_sequence";
	public static String BBB_LIST_TYPE_SEQUENCE = "bbb_misc.bbb_list_type_sequence";
	public static String BBB_LIST_CATEGORY_SEQUENCE = "bbb_misc.bbb_list_category_sequence";
	public static String BBB_LIST_RULES_SEQUENCE = "bbb_misc.bbb_list_rules_sequence";
	public static String BBB_LIST_RULES_SKU_SEQUENCE = "bbb_misc.bbb_sku_rules_sequence";

	public static String exactSearch(String str) {

		if (EXACT_SEARCH) return str;
		if (str == null || str.length() == 0) return str;
		if (str.indexOf("%") != -1) return str;
		return str + "%";
	}

	public static String exactSearch(String str, boolean exactSearch) {

		if (exactSearch) return str;
		if (str == null || str.length() == 0) return str;
		if (str.indexOf("%") != -1) return str;
		return str + "%";
	}

	public static String getNextSequence(String seq) {

		return "SELECT '" + Config.DATA_CENTER + "' || " + seq + ".NEXTVAL FROM DUAL";
	}
}

public class Utility {

	public String substr(String str, int start, int length) {

		int startpos = 0;
		int strlen = str.length();

		if (start < 0) {
	        	startpos = Math.max(start + strlen, 0); 
		}
		else {
			startpos = Math.min(start, strlen); 
		}
		int endpos = Math.min(startpos + length, strlen);
    		return str.substring(startpos, endpos);
	}

	public String NVL(String value, String alt) {

		if (value == null) value = alt==null?"":alt;

		return value.trim();
	}

	public int parseInt(String s, int alt) {

		try {

			return Integer.parseInt(s, 10);
		}
		catch (NumberFormatException e) {

			return alt;
		}
	}

	public long parseLong(String s, long alt) {

		try {

			return Long.parseLong(s, 10);
		}
		catch (NumberFormatException e) {

			return alt;
		}
	}

	public boolean parseBoolean(String s, boolean alt) {

		if (isBlank(s)) return alt;
		if (s.equals("0") || s.toLowerCase().equals("false")) return false;
		return true;
	}

	public boolean isNullOrEmpty(String s) {

		return (s == null || s.length() == 0);
	}

	public boolean isBlank(String s) {

		return (s == null || s.trim().length() == 0);
	}

	public String split(String s, String delim, int index) {

		String[] part = new String[0];
		
		try {
			part = s.split(delim);
			return part[index];
		} catch (Exception ex) {}

		return "";
	}

	public String ltrim(String s) {
	
		if (s == null || s.trim().length() == 0) {
			return "";
		}
		return s.replaceAll("^\\s+","");
	}

	public String rtrim(String s) {
	
		if (s == null || s.trim().length() == 0) {
			return "";
		}
		return s.replaceAll("\\s+$","");
	}

	public Cookie getCookie(Cookie[] cookies, String name, String value) {
		if (cookies != null) {
			for(int i = 0; i < cookies.length; i++) { 
		    		if (cookies[i].getName().equals(name)) {
		        		return cookies[i];
		    		}
			}
		}
		return new Cookie(name, value);
	}
}
%>

