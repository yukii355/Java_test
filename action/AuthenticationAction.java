package jp.co.tis.rookies.action;

import jp.co.tis.rookies.common.authentication.AuthenticationUtil;
import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.common.authentication.exception.AuthenticationException;
import jp.co.tis.rookies.entity.SystemAccount;
import jp.co.tis.rookies.entity.UserProfile;
import jp.co.tis.rookies.form.LoginForm;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

/**
 * 認証機能アクション。
 *
 * @author Hiroyuki Hirano
 * @since 1.0
 */
public class AuthenticationAction {

    /** フラグがtrueであることを表す定数 */
    private static final String FLAG_TRUE = "1";

    /**
     * ログイン画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {
        return new HttpResponse("/WEB-INF/view/login/index.jsp");
    }

    /**
     * ログイン処理を実行する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = LoginForm.class)
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/login/index.jsp")
    public HttpResponse login(HttpRequest req, ExecutionContext ctx) {

        LoginForm form = ctx.getRequestScopedVar("form");

        try {
            AuthenticationUtil.authenticate(form.getLoginId(), form.getLoginPassword());
        } catch (AuthenticationException e) {
            // パスワード不一致、その他認証エラー（ユーザーが存在しない等）
            throw new ApplicationException(
                    MessageUtil.createMessage(MessageLevel.ERROR, "rookies.login.error.authentication"));
        }

        // 認証OKの場合、ログイン前のセッションを破棄
        SessionUtil.invalidate(ctx);
        ctx.invalidateSession();

        // 認証情報をセッション（新規）に格納後、トップ画面にリダイレクトする。
        SystemAccount account = UniversalDao.findBySqlFile(SystemAccount.class,
                "FIND_ENROLLED_SYSTEM_ACCOUNT_BY_LOGIN_ID", new Object[] { form.getLoginId() });
        UserProfile userProfile = UniversalDao.findById(UserProfile.class, account.getUserId());

        LoginUserContext userContext = new LoginUserContext();
        userContext.setUserId(account.getUserId().toString());
        userContext.setAdmin(FLAG_TRUE.equals(account.getAdminFlag()));
        userContext.setKanjiName(userProfile.getLastName() + " " + userProfile.getFirstName());

        SessionUtil.put(ctx, "userContext", userContext, "httpSession");

        return new HttpResponse("redirect:///action/itemList/index");
    }

    /**
     * ログアウト。
     *
     * @param request HTTPリクエスト
     * @param context 実行コンテキスト
     * @return HTTPレスポンス
     */
    public HttpResponse logout(HttpRequest request, ExecutionContext context) {
        SessionUtil.invalidate(context);
        context.invalidateSession();

        return new HttpResponse("redirect:///");
    }
}
