SET ECHO ON;
SET DEFINE OFF;

--Added for TBS Store coupon data
create table BBB_PUB.bbb_coupon_info (
  asset_version integer not null,
  coupon_id varchar2(40) not null,
  store_only number(1) null,
  eligible_stores varchar2(1024) null
);
