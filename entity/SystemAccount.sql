--------------------------------------------------------------------------------
-- ログインIDを元に在籍中のシステムアカウント情報を取得する。(ログイン用)
--------------------------------------------------------------------------------
FIND_ENROLLED_SYSTEM_ACCOUNT_BY_LOGIN_ID =
SELECT
    *
FROM
    SYSTEM_ACCOUNT
WHERE
    LOGIN_ID = ?
    AND DELETED_FLAG = '0'

--------------------------------------------------------------------------------
-- ログインIDを元に在籍中のシステムアカウント情報を取得する。(削除用)
--------------------------------------------------------------------------------
FIND_ENROLLED_SYSTEM_ACCOUNT_BY_USER_ID =
SELECT
    *
FROM
    SYSTEM_ACCOUNT
WHERE
    USER_ID = ?
    AND DELETED_FLAG = '0'

--------------------------------------------------------------------------------
-- ログインIDを元に在籍中のシステムアカウント情報を取得する。(削除用)
--------------------------------------------------------------------------------
FIND_SYSTEM_ACCOUNT_BY_LOGIN_ID =
SELECT
    *
FROM
    SYSTEM_ACCOUNT
WHERE
    LOGIN_ID = ?

