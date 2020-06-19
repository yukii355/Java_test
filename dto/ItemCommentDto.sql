--------------------------------------------------------------------------------
-- 投稿IDをもとにコメントの情報を取得するSQL
--------------------------------------------------------------------------------
FIND_COMMENT_BY_ITEM_ID=
SELECT
    ITEM_COMMENT.ITEM_ID,
    ITEM_COMMENT.COMMENT_ID,
    ITEM_COMMENT.USER_ID,
    ITEM_COMMENT.CONTENT,
    USER_PROFILE.LAST_NAME,
    USER_PROFILE.FIRST_NAME,
    ITEM_COMMENT.UPDATE_TIME
FROM ITEM_COMMENT
    INNER JOIN USER_PROFILE ON USER_PROFILE.USER_ID = ITEM_COMMENT.USER_ID
WHERE
    ITEM_COMMENT.ITEM_ID = :itemId
ORDER BY
    ITEM_COMMENT.UPDATE_TIME