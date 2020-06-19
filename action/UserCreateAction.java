package jp.co.tis.rookies.action;


import jp.co.tis.rookies.common.authentication.AuthenticationUtil;
import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.entity.Department;
import jp.co.tis.rookies.entity.SystemAccount;
import jp.co.tis.rookies.entity.UserProfile;
import jp.co.tis.rookies.form.UserCreateForm;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.core.beans.BeanUtil;
import nablarch.core.db.statement.exception.DuplicateStatementException;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

import java.util.HashMap;
import java.util.Map;


/**
 * ユーザ登録Action。
 *
 * @author Moriyama Hiroyuki
 * @since 1.0
 */
public class UserCreateAction {

    /**
     * 新規ユーザ作成用セッションキー
     */
    private static final String NEW_USER_INFO_SESSION_KEY = "newUserInfo";

     /**
     * ユーザ登録画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {

        SessionUtil.delete(ctx, NEW_USER_INFO_SESSION_KEY);

        ctx.setRequestScopedVar("deptList", UniversalDao.findAllBySqlFile(Department.class, "FIND_ALL"));

        // ユーザ登録画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/create.jsp");

    }

    /**
     * ユーザ登録確認画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = UserCreateForm.class, prefix = "form") // 単項目精査、項目間精査
    @OnError(type = ApplicationException.class, path = "forward://index")
    public HttpResponse confirm(HttpRequest req, ExecutionContext ctx) {

        // リクエストスコープから入力情報を取得
        UserCreateForm form = ctx.getRequestScopedVar("form");

        Map<String, Integer> param = new HashMap<>();
        param.put("deptId", Integer.parseInt(form.getDeptId()));


        // 部署ID存在精査
        if (!UniversalDao.exists(Department.class, "FIND_BY_DEPT_ID", param)) {
            Message message = MessageUtil.createMessage(MessageLevel.ERROR, "rookies.user.create.error.department.nothing");
            throw new ApplicationException(message);
        }

        // ログインID重複精査
        if (UniversalDao.exists(SystemAccount.class, "FIND_SYSTEM_ACCOUNT_BY_LOGIN_ID", new Object[] {form.getLoginId() })) {
            Message message = MessageUtil.createMessage(MessageLevel.ERROR, "rookies.user.create.error.duplicate.loginId", form.getLoginId());
            throw new ApplicationException(message);
        }

        // 入力値をセッションストアに格納
        SessionUtil.put(ctx, NEW_USER_INFO_SESSION_KEY, form);



        // 部署一覧を取得してリクエストスコープに格納
        ctx.setRequestScopedVar("deptList", UniversalDao.findAllBySqlFile(Department.class, "FIND_ALL"));

        // ユーザ登録確認画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/createConfirm.jsp");

    }


    /**
     * 戻るボタンを押下後、ユーザ登録画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse back(HttpRequest req, ExecutionContext ctx) {

        // セッションに格納された入力値を取得
        UserCreateForm form = SessionUtil.get(ctx, NEW_USER_INFO_SESSION_KEY);

        // リクエストスコープに格納
        ctx.setRequestScopedVar("form", form);

        // 部署一覧を取得してリクエストスコープに格納
        ctx.setRequestScopedVar("deptList", UniversalDao.findAllBySqlFile(Department.class, "FIND_ALL"));

        // ユーザ登録画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/create.jsp");
    }

    /**
     * ユーザを登録し、ユーザ登録完了画面表示メソッドにリダイレクトする。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    @OnError(type = ApplicationException.class, path = "forward://index")
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse create(HttpRequest req, ExecutionContext ctx) {
        LoginUserContext userContext = SessionUtil.get(ctx, "userContext");

        // ユーザ登録権限精査
        if (!userContext.isAdmin()) {
            throw new HttpErrorResponse("/WEB-INF/view/common/errorPages/USER_ERROR.jsp");
        }

        // ブラウザを直接閉じた場合などにセッションが残っている場合があるので削除
        UserCreateForm form = SessionUtil.delete(ctx, NEW_USER_INFO_SESSION_KEY);

        // FormからEntityへ変換(formをSystemAccount.classにコピーしている。同じ変数があればコピーし、無ければ空のまま。BeanUtilでこれが出来る)
        SystemAccount account = BeanUtil.createAndCopy(SystemAccount.class, form);

        // パスワードの暗号化
        String encryptedPassword = AuthenticationUtil.encryptPassword(account.getLoginId(), account.getLoginPassword());


        account.setLoginPassword(encryptedPassword);

        // 初期値設定
        account.setDeletedFlag("0");

        // DBにaccountをデータとして登録している。登録出来なければ重複エラー(DuplicateStatementException)
        try {
            UniversalDao.insert(account);
        } catch (DuplicateStatementException e) {
            ctx.setRequestScopedVar("form", form);
            Message message = MessageUtil.createMessage(MessageLevel.ERROR, "rookies.user.create.error.duplicate.loginId", account.getLoginId());
            throw new ApplicationException(message);
        }



        // formは精査済みのデータ。(UserCreateForm)
        UserProfile profile = BeanUtil.createAndCopy(UserProfile.class, form);

        // ユーザIDは自動採番なのでそのまま突っ込んでいる
        profile.setUserId(account.getUserId());

        UniversalDao.insert(profile);


        // ユーザ登録完了画面表示メソッドにリダイレクトする。
        return new HttpResponse("redirect://complete");
    }


    /**
     * ユーザ登録完了画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse complete(HttpRequest req, ExecutionContext ctx) {
        // ユーザ登録完了画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/createComplete.jsp");
    }

}
