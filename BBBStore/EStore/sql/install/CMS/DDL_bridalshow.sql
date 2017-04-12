 
drop table bbb_bridalshow_site;

drop table bbb_bridalshow_state;

drop table bbb_bridalshow_template;

drop table state;

create table bbb_bridalshow_template (
	bridalshow_template_id 	varchar2(254)	not null,
	show_date 		TIMESTAMP(6),
	show_name 		varchar2(254)	null,
	show_address 		varchar2(254)	null,
	show_phone 		varchar2(254)	null,
	show_view_directions 	varchar2(254)	null,
	primary key(bridalshow_template_id)
);

create table bbb_bridalshow_state (
	bridalshow_template_id 	varchar2(254)	not null references bbb_bridalshow_template(bridalshow_template_id),
	state_id 		varchar2(254)	not null references bbb_states(state_id),
	primary key(bridalshow_template_id, state_id)
);

 

create table bbb_bridalshow_site (
	bridalshow_template_id 	varchar2(254)	not null references bbb_bridalshow_template(bridalshow_template_id),
	site_id 		varchar2(254)	not null references site_configuration(id),
	primary key(bridalshow_template_id, site_id)
);
