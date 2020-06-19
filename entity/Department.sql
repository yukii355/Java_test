-- 昇順で部署一覧を取得する

FIND_ALL =
SELECT
    DEPT_ID,
    DEPT_NAME
FROM
    DEPARTMENT
ORDER BY
    DEPT_ID

--指定した部署IDを基に情報を取得するSQL（存在確認用）
FIND_BY_DEPT_ID =
SELECT
    DEPT_ID
FROM
    DEPARTMENT
WHERE
    DEPT_ID = :deptId
