--------------------------------------------------------------------------------
-- 投稿IDをもとに投稿の情報を取得するSQL
--------------------------------------------------------------------------------
FIND_ITEM_BY_ITEM_ID=
SELECT
    ITEM.ITEM_ID,
    ITEM.TITLE,
    ITEM.CONTENT,
    USER_PROFILE.USER_ID,
    USER_PROFILE.LAST_NAME,
    USER_PROFILE.FIRST_NAME,
    ITEM.UPDATE_TIME
FROM ITEM
    INNER JOIN USER_PROFILE ON ITEM.USER_ID = USER_PROFILE.USER_ID
WHERE
    ITEM.ITEM_ID = :itemId