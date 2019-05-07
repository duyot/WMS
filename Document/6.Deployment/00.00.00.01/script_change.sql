--Khach hang nay co can phan quyen theo doi tac gui hang hay khong
alter table CAT_CUSTOMER add PARTNER_PERMISSION NUMBER(1,0) DEFAULT 0;


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