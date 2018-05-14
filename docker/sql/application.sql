CREATE TABLE ITEMS(

  ITEM_I SERIAL,
  SKU TEXT NOT NULL,
  ITEM_DESCRIPTION TEXT DEFAULT  NULL,
  PRICE NUMERIC (5,2),
  CREATE_TS TIMESTAMPTZ NULL DEFAULT current_timestamp
);
