create table bbb_component_scheme (
    component_id varchar2(256) primary key, 
    component_expression varchar2(256)    
);                      
            
create table bbb_group_info (
    group_id  varchar2(256) primary key,
    group_name varchar2(256),           
    description varchar2(256)           
);           
       
create table bbb_component_group (
   id varchar2(256),
  group_id  varchar2(256),
  component_id varchar2(256)
);
            
create table bbb_client_info (
  id varchar2(256) primary key,            
  client_id varchar2(256) unique,                 
  client_token varchar2(256),              
  authrozied_group_id varchar2(256),
  secure_role varchar2(256),
  not_authrozied_group_id varchar2(256)
);
			
create table bbb_client_role (
  id varchar2(256),
  seq_number varchar2(256),
  role_id  varchar2(256)
);
  
create table bbb_role_persona (
  id varchar2(256),
  role_name  varchar2(256),
  persona varchar2(256)
);