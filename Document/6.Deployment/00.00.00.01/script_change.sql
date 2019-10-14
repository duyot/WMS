--Khach hang nay co can phan quyen theo doi tac gui hang hay khong
alter table CAT_CUSTOMER add PARTNER_PERMISSION NUMBER(1,0) DEFAULT 0;

alter table MJR_STOCK_TRANS_DETAIL drop column goods_size;


 CREATE TABLE "MAP_USER_PARTNER" 
   (	"ID" NUMBER, 
	"USER_ID" NUMBER, 
	"PARTNER_ID" NUMBER
   );
 


--Thay doi cac cot varchar(byte) -> varchar(char) do loi khi luu Tieng Viet
alter table APP_PARAMS modify NAME VARCHAR2(2000 CHAR);
alter table APP_PARAMS modify CODE VARCHAR2(50 CHAR);
alter table APP_PARAMS modify TYPE VARCHAR2(100 CHAR);
alter table CAT_CUSTOMER modify EMAIL VARCHAR2(50 CHAR);
alter table CAT_CUSTOMER modify TEL_NUMBER VARCHAR2(15 CHAR);
alter table CAT_CUSTOMER modify ADDRESS VARCHAR2(100 CHAR);
alter table CAT_CUSTOMER modify BANK_NAME VARCHAR2(100 CHAR);
alter table CAT_CUSTOMER modify BANK_ACCOUNT_CODE VARCHAR2(20 CHAR);
alter table CAT_CUSTOMER modify CODE VARCHAR2(50 CHAR);
alter table CAT_DEPARTMENT modify CODE VARCHAR2(50 CHAR);
alter table CAT_GOODS modify UNIT_TYPE VARCHAR2(50 CHAR);
alter table CAT_PARTNER modify CODE VARCHAR2(50 CHAR);
alter table CAT_PARTNER modify TEL_NUMBER VARCHAR2(200 CHAR);
alter table CAT_STOCK modify CODE VARCHAR2(50 CHAR);
alter table CAT_STOCK modify MANAGER_INFO VARCHAR2(200 CHAR);
alter table CAT_STOCK_CELL modify CODE VARCHAR2(50 CHAR);
alter table CAT_USER modify NAME VARCHAR2(100 CHAR);
alter table CAT_USER modify PASSWORD VARCHAR2(100 CHAR);
alter table CAT_USER modify EMAIL VARCHAR2(50 CHAR);
alter table CAT_USER modify TEL_NUMBER VARCHAR2(15 CHAR);
alter table CAT_USER modify IMG_URL VARCHAR2(50 CHAR);
alter table CAT_USER modify CODE VARCHAR2(50 CHAR);
alter table CAT_USER modify LOG_REASON VARCHAR2(50 CHAR);
alter table CAT_USER modify ROLE_NAME VARCHAR2(50 CHAR);
alter table CUSTOMERS modify MOBILE VARCHAR2(30 CHAR);
alter table CUSTOMERS modify EMAIL VARCHAR2(100 CHAR);
alter table CUSTOMERS modify NAME VARCHAR2(100 CHAR);
alter table DEPARTMENT modify DEPARTMENT_NAME VARCHAR2(50 CHAR);
alter table DEPARTMENT modify DEPARTMENT_CODE VARCHAR2(50 CHAR);
alter table DEPARTMENT modify IS_DELETED VARCHAR2(1 CHAR);
alter table DEPARTMENT_TEST modify DEPARTMENT_CODE VARCHAR2(50 CHAR);
alter table DEPARTMENT_TEST modify DEPARTMENT_NAME VARCHAR2(50 CHAR);
alter table DEPARTMENT_TEST modify IS_DELETED VARCHAR2(1 CHAR);
alter table DUYOT_IMG modify DESCRIPTION VARCHAR2(2000 CHAR);
alter table DUYOT_IMG modify HREF VARCHAR2(4000 CHAR);
alter table DUYOT_IMG modify TITLE VARCHAR2(1000 CHAR);
alter table DUYOT_IMG modify CATEGORY VARCHAR2(100 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify SERIAL VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify ORA_ERR_OPTYP$ VARCHAR2(2 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify ORA_ERR_TAG$ VARCHAR2(2000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify ID VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify OUTPUT_PRICE VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify INPUT_PRICE VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify IMPORT_STOCK_TRANS_ID VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify PARTNER_ID VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify STATUS VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify ORA_ERR_MESG$ VARCHAR2(2000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify AMOUNT VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify CELL_CODE VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify GOODS_STATE VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify GOODS_ID VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify STOCK_ID VARCHAR2(4000 CHAR);
alter table ERR$_MJR_STOCK_GOODS_SERIAL modify CUST_ID VARCHAR2(4000 CHAR);
alter table ERROR_LOG modify FUNCTION VARCHAR2(50 CHAR);
alter table ERROR_LOG modify CLASS_NAME VARCHAR2(50 CHAR);
alter table ERROR_LOG modify PARAMETER VARCHAR2(4000 CHAR);
alter table ERROR_LOG modify ERROR_INFO VARCHAR2(1000 CHAR);
alter table MJR_STOCK_GOODS modify CELL_CODE VARCHAR2(50 CHAR);
alter table MJR_STOCK_GOODS modify GOODS_SIZE VARCHAR2(10 CHAR);
alter table MJR_STOCK_GOODS modify GOODS_STATE VARCHAR2(1 CHAR);
alter table MJR_STOCK_GOODS_SERIAL modify GOODS_STATE VARCHAR2(1 CHAR);
alter table MJR_STOCK_GOODS_SERIAL modify CELL_CODE VARCHAR2(50 CHAR);
alter table MJR_STOCK_GOODS_SERIAL modify SERIAL VARCHAR2(20 CHAR);
alter table MJR_STOCK_GOODS_TOTAL modify GOODS_STATE VARCHAR2(1 CHAR);
alter table MJR_STOCK_GOODS_TOTAL modify GOODS_CODE VARCHAR2(50 CHAR);
alter table MJR_STOCK_GOODS_TOTAL modify GOODS_NAME VARCHAR2(100 CHAR);
alter table MJR_STOCK_TRANS modify INVOICE_NUMBER VARCHAR2(50 CHAR);
alter table MJR_STOCK_TRANS modify CREATED_USER VARCHAR2(50 CHAR);
alter table MJR_STOCK_TRANS_DETAIL modify CELL_CODE VARCHAR2(50 CHAR);
alter table MJR_STOCK_TRANS_DETAIL modify GOODS_SIZE VARCHAR2(10 CHAR);
alter table MJR_STOCK_TRANS_DETAIL modify GOODS_STATE VARCHAR2(1 CHAR);
alter table MJR_STOCK_TRANS_DETAIL modify IS_SERIAL VARCHAR2(1 CHAR);
alter table MJR_STOCK_TRANS_DETAIL modify SERIAL VARCHAR2(20 CHAR);
alter table MJR_STOCK_TRANS_DETAIL modify GOODS_CODE VARCHAR2(50 CHAR);
alter table PLSQL_PROFILER_RUNS modify SPARE1 VARCHAR2(256 CHAR);
alter table PLSQL_PROFILER_RUNS modify RUN_COMMENT VARCHAR2(2047 CHAR);
alter table PLSQL_PROFILER_RUNS modify RUN_SYSTEM_INFO VARCHAR2(2047 CHAR);
alter table PLSQL_PROFILER_RUNS modify RUN_OWNER VARCHAR2(32 CHAR);
alter table PLSQL_PROFILER_RUNS modify RUN_COMMENT1 VARCHAR2(2047 CHAR);
alter table PLSQL_PROFILER_UNITS modify UNIT_TYPE VARCHAR2(32 CHAR);
alter table PLSQL_PROFILER_UNITS modify UNIT_OWNER VARCHAR2(32 CHAR);
alter table PLSQL_PROFILER_UNITS modify UNIT_NAME VARCHAR2(32 CHAR);
alter table REPORT_STOCK_DAILY_IE modify GOODS_UNIT_ID VARCHAR2(2 CHAR);
alter table REPORT_STOCK_DAILY_IE modify GOODS_STATE VARCHAR2(2 CHAR);
alter table REPORT_STOCK_DAILY_IE modify GOODS_CODE VARCHAR2(50 CHAR);
alter table REPORT_STOCK_DAILY_IE modify GOODS_NAME VARCHAR2(100 CHAR);
alter table REPORT_STOCK_DAILY_IE modify GOODS_IS_SERIAL VARCHAR2(2 CHAR);
alter table SONGS modify NAME VARCHAR2(500 CHAR);
alter table SONGS modify URL VARCHAR2(500 CHAR);
alter table SQLN_PROF_ANB modify TEXT VARCHAR2(2048 CHAR);
alter table SQLN_PROF_PROFILES modify PROJ_COMMENT VARCHAR2(2047 CHAR);
alter table SQLN_PROF_PROFILES modify PROJ_NAME VARCHAR2(2047 CHAR);
alter table SQLN_PROF_UNITS modify UNIT_NAME VARCHAR2(30 CHAR);
alter table SQLN_PROF_UNIT_HASH modify HASH VARCHAR2(32 CHAR);
alter table SYS_DATA_CHANGE modify TABLE_NAME VARCHAR2(50 CHAR);
alter table SYS_DATA_CHANGE modify STATUS VARCHAR2(1 CHAR);
alter table SYS_MAIL_PENDING modify ATTACH_PATH VARCHAR2(256 CHAR);
alter table SYS_MAIL_PENDING modify CONTENT VARCHAR2(2000 CHAR);
alter table SYS_MAIL_PENDING modify SUBJECT VARCHAR2(256 CHAR);
alter table SYS_MAIL_PENDING modify TO_ADDR VARCHAR2(1024 CHAR);
alter table SYS_MAIL_PENDING modify FROM_ADDR VARCHAR2(128 CHAR);
alter table SYS_MENU modify TYPE VARCHAR2(1 CHAR);
alter table SYS_MENU modify URL VARCHAR2(100 CHAR);
alter table SYS_MENU modify LEVELS VARCHAR2(1 CHAR);
alter table SYS_MENU modify ORDERS VARCHAR2(1 CHAR);
alter table SYS_MENU modify IMG_CLASS VARCHAR2(20 CHAR);
alter table SYS_MENU modify NAME VARCHAR2(100 CHAR);
alter table SYS_MENU modify CODE VARCHAR2(50 CHAR);
alter table SYS_ROLE modify NAME VARCHAR2(100 CHAR);
alter table SYS_ROLE modify CODE VARCHAR2(20 CHAR);
alter table SYS_STATISTIC_TOP_GOODS modify STATISTIC_INFO VARCHAR2(200 CHAR);
alter table SYS_STATISTIC_TOP_GOODS modify TYPE VARCHAR2(2 CHAR);
alter table TRACE modify DESCRIPTION VARCHAR2(4000 CHAR);
alter table TRACE modify FUNCTIONNAME VARCHAR2(50 CHAR);
alter table TRANSACTIONS modify CODE VARCHAR2(50 CHAR);
alter table TRANSACTIONS modify CUST_NAME VARCHAR2(500 CHAR);
alter table USER_TEST modify FIRST_NAME VARCHAR2(50 CHAR);
alter table USER_TEST modify LAST_NAME VARCHAR2(50 CHAR);
alter table USER_TEST modify EMAIL VARCHAR2(50 CHAR);
alter table USER_TEST modify GENDER VARCHAR2(50 CHAR);
alter table USER_TEST modify IS_DELETED VARCHAR2(1 CHAR);


--User nay co can phan quyen theo doi tac gui hang hay khong, khong can phan quyen theo khach hang
alter table CAT_CUSTOMER DROP COLUMN PARTNER_PERMISSION;
alter table CAT_USER add PARTNER_PERMISSION NUMBER(1,0) DEFAULT 0;
comment on column "CAT_USER"."PARTNER_PERMISSION" is '1. Can phan quyen theo doi tac, 0. Khong can phan quyen theo doi tac';

CREATE SEQUENCE  "SEQ_MAP_USER_PARTNER"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;


alter table CAT_USER add STOCK_PERMISSION NUMBER(1,0) DEFAULT 0;
comment on column "CAT_USER"."STOCK_PERMISSION" is '1. Can phan quyen theo kho, 0. Khong can phan quyen theo kho';

--Update bang stock_trans_detail cho cac giao dich cu khong co unit_type
update mjr_stock_trans_detail d
set d.unit_name =(
  select distinct gr.name from (
    select a.GOODS_ID, c.name
    from mjr_stock_trans_detail a, cat_goods b, app_params c
    where 1=1
    AND a.goods_id = b.id
    and b.unit_type = c.code
    and c.type ='UNIT_TYPE'
  ) gr  
  where d.goods_id = gr.goods_id
)
where 1=1;


--25/6/2019
alter table MJR_STOCK_GOODS drop column goods_size;

alter table CAT_GOODS drop column VOLUME;
alter table CAT_GOODS drop column WEIGHT;
alter table MJR_STOCK_GOODS drop column VOLUME;
alter table MJR_STOCK_GOODS drop column WEIGHT;
alter table MJR_STOCK_GOODS_SERIAL drop column VOLUME;
alter table MJR_STOCK_GOODS_SERIAL drop column WEIGHT;
alter table MJR_STOCK_TRANS_DETAIL drop column VOLUME;
alter table MJR_STOCK_TRANS_DETAIL drop column WEIGHT;

alter table CAT_GOODS add VOLUME NUMBER(19,6);
comment on column "CAT_GOODS"."VOLUME" is 'The tich cua 1 don vi hang hoa';

alter table CAT_GOODS add WEIGHT NUMBER(19,6);
comment on column "CAT_GOODS"."WEIGHT" is 'Trong luong cua 1 don vi hang hoa';

alter table MJR_STOCK_GOODS add VOLUME NUMBER(38,6);
alter table MJR_STOCK_GOODS add WEIGHT NUMBER(38,6);
comment on column "MJR_STOCK_GOODS"."VOLUME" is 'The tich hang hoa';
comment on column "MJR_STOCK_GOODS"."WEIGHT" is 'Trong luong hang hoa';

alter table MJR_STOCK_GOODS_SERIAL add VOLUME NUMBER(38,6);
alter table MJR_STOCK_GOODS_SERIAL add WEIGHT NUMBER(38,6);
comment on column "MJR_STOCK_GOODS_SERIAL"."VOLUME" is 'The tich hang hoa';
comment on column "MJR_STOCK_GOODS_SERIAL"."WEIGHT" is 'Trong luong hang hoa';


alter table MJR_STOCK_TRANS_DETAIL add VOLUME NUMBER(38,6);
comment on column "MJR_STOCK_TRANS_DETAIL"."VOLUME" is 'The tich hang hoa';

alter table MJR_STOCK_TRANS_DETAIL add WEIGHT NUMBER(38,6);
comment on column "MJR_STOCK_TRANS_DETAIL"."WEIGHT" is 'Trong luong hang hoa';
---Patched at 10/8/2019---
