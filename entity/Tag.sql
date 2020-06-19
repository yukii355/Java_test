--------------------------------------------------------------------------------
-- 全タグを取得するSQL
--------------------------------------------------------------------------------
FIND_ALL=
SELECT
    *
FROM
    TAG
ORDER BY
    TAG_ID

--------------------------------------------------------------------------------
-- 全タグを取得するSQL
--------------------------------------------------------------------------------
FIND_BY_TAG_IDS=
SELECT
    *
FROM
    TAG
WHERE
    TAG_ID IN (:tagIds[])
ORDER BY
    TAG_ID

--------------------------------------------------------------------------------
-- 投稿idに紐づくタグ名をすべて取得するSQL
--------------------------------------------------------------------------------
FIND_NAME_BY_ITEM_ID=
SELECT
    TAG.TAG_NAME
FROM ITEM_TAG
    INNER JOIN TAG ON ITEM_TAG.TAG_ID = TAG.TAG_ID
WHERE
    ITEM_ID = :itemId
ORDER BY
    TAG.TAG_ID

--------------------------------------------------------------------------------
-- 投稿idに紐づくタグ情報をすべて取得するSQL
--------------------------------------------------------------------------------
FIND_BY_ITEM_ID=
SELECT
    TAG.TAG_ID,
    TAG.TAG_NAME
FROM TAG
    INNER JOIN ITEM_TAG ON ITEM_TAG.TAG_ID = TAG.TAG_ID
WHERE
    ITEM_ID = :itemId
ORDER BY
    TAG.TAG_ID