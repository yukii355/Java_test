--------------------------------------------------------------------------------
-- 指定した投稿IDをもとにItemを取得するSQL
--------------------------------------------------------------------------------
FIND_BY_ITEM_ID=
SELECT
    *
FROM ITEM_TAG
WHERE
    ITEM_ID = :itemId