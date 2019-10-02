--Bang ton kho theo ngay de phuc vu chuc nang xuat the kho

  CREATE TABLE MJR_STOCK_DAILY_REMAIN 
   (	ID NUMBER, 
	CUST_ID NUMBER, 
	GOODS_ID NUMBER, 
	GOODS_CODE VARCHAR2(50 CHAR), 
	GOODS_NAME VARCHAR2(100 CHAR), 
	GOODS_STATE VARCHAR2(1 CHAR), 
	STOCK_ID NUMBER, 
	AMOUNT NUMBER(38,4), 
	GOODS_UNIT_NAME VARCHAR2(50 CHAR), 
	REMAIN_DATE DATE
   );
 
 --Bang tong hop xuat kho theo ngay de phuc vu chuc nang xuat the kho
  CREATE TABLE MJR_STOCK_DAILY_IMPORT_EXPORT 
   (	ID NUMBER, 
	CUST_ID NUMBER, 
	GOODS_ID NUMBER, 
	GOODS_CODE VARCHAR2(50 CHAR), 
	GOODS_NAME VARCHAR2(100 CHAR), 
	GOODS_STATE VARCHAR2(1 CHAR), 
	STOCK_ID NUMBER, 
	AMOUNT NUMBER(38,4), 
	GOODS_UNIT_NAME VARCHAR2(50 CHAR), 
	TRANS_DATE DATE, 
	TYPE VARCHAR2(1 CHAR)
   );
 
 
--Han dung, ghi chu, ngay san xuat, so chung tu cua hang trong kho
alter table mjr_stock_goods add description varchar2(2000);
alter table mjr_stock_goods add produce_date date;
alter table mjr_stock_goods add expire_date date;
alter table mjr_stock_goods add invoice_number varchar2(2000);;

comment on column mjr_stock_goods.description is 'Ghi chu';
comment on column mjr_stock_goods.produce_date is 'Ngay san xuat';
comment on column mjr_stock_goods.expire_date is 'Ngay het han';
comment on column mjr_stock_goods.invoice_number is 'So chung tu';

alter table mjr_stock_goods_serial add  description varchar2(2000);
alter table mjr_stock_goods_serial add  produce_date date;
alter table mjr_stock_goods_serial add  expire_date date;
alter table mjr_stock_goods_serial add  invoice_number varchar2(2000);

comment on column mjr_stock_goods_serial.description is 'Ghi chu';
comment on column mjr_stock_goods_serial.produce_date is 'Ngay san xuat';
comment on column mjr_stock_goods_serial.expire_date is 'Ngay het han';
comment on column mjr_stock_goods_serial.invoice_number is 'So chung tu';

---Patched at 2/10/2019---

