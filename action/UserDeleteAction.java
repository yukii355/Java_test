package jp.co.tis.rookies.action;

import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.dto.UserDetailDto;
import jp.co.tis.rookies.entity.SystemAccount;
import jp.co.tis.rookies.form.UserIdForm;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.WebUtil;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

import javax.persistence.OptimisticLockException;
import java.util.HashMap;
import java.util.Map;

/**
 * ユーザ削除詳細。
 *
 * @author Moriyama Hiroyuki
 * @since 1.0
 */
public class UserDeleteAction {

    /** ユーザ情報を保持するためのセッションキー */
    private static final String USER_DELETE_SESSION_KEY = "deleteUser";

    /**
     * ユーザ削除確認画面を表示する。
     * @param req リクエスト
     * @param ctx HTTPリクエストの処理に関するサーバの情報
     * @return HttpResponse
     */
    @InjectForm(form = UserIdForm.class) // ユーザIDの単項目精査
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {

        // 初期化としてセッションを削除する。
        SessionUtil.delete(ctx, USER_DELETE_SESSION_KEY);

        UserIdForm userIdForm = ctx.getRequestScopedVar("form");


        Map<String, String> param = new HashMap<>();
        param.put("userId", userIdForm.getUserId());
        UserDetailDto dto;
        SystemAccount systemAccount;


        // ユーザ削除可能精査
        try {
            dto = UniversalDao.findBySqlFile(UserDetailDto.class, "FIND_EXISTS_USER_INFO_BY_USER_ID", param);
            systemAccount = UniversalDao.findBySqlFile(SystemAccount.class, "FIND_ENROLLED_SYSTEM_ACCOUNT_BY_USER_ID",
                new Object[] {Integer.parseInt(userIdForm.getUserId())});
        } catch (NoDataException e) {
            Message message = MessageUtil.createMessage(MessageLevel.ERROR, "rookies.user.detail.error.noData");
            WebUtil.notifyMessages(ctx, message);
            throw new HttpErrorResponse("forward:///action/userDetail/profile", e);
        }

        // JSPに遷移する前のリクエストスコープに格納
        ctx.setRequestScopedVar("deleteAccount", dto);

        SessionUtil.put(ctx, USER_DELETE_SESSION_KEY, systemAccount);

        return new HttpResponse("/WEB-INF/view/user/delete.jsp");

    }


    /**
     * ユーザ削除を行う。
     * @param req リクエスト
     * @param ctx HTTPリクエストの処理に関するサーバの情報
     * @return HttpResponse
     */
    @OnError(type = ApplicationException.class, path = "forward://index")
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse delete(HttpRequest req, ExecutionContext ctx) {

        // ログイン時のユーザアカウントを取得
        LoginUserContext loginUserContext = SessionUtil.get(ctx, "userContext");

        // 実行ユーザ権限精査
        if (!loginUserContext.isAdmin()) {
            throw new HttpErrorResponse("/WEB-INF/view/common/errorPages/USER_ERROR.jsp");
        }

        SystemAccount systemAccount = SessionUtil.delete(ctx, USER_DELETE_SESSION_KEY);

        // 削除フラグを立てる
        systemAccount.setDeletedFlag("1");

        // 排他制御エラー
        try {
            // DBを更新
            UniversalDao.update(systemAccount);
        } catch (OptimisticLockException e) {
            // エラー時の画面遷移に向け、リクエストスコープにユーザIDを格納
            req.setParam("userId", Integer.toString(systemAccount.getUserId()));
            throw new ApplicationException(MessageUtil.createMessage(MessageLevel.ERROR, "rookies.common.optimistic.message"));
        }

        return new HttpResponse("redirect://complete?userId=" + Integer.toString(systemAccount.getUserId()));
    }

    /**
     * ユーザ削除完了画面を表示する。
     *
     * @param req HTTPリクエスト
     * @param ctx 実行コンテキスト
     * @return HTTPレスポンス
     */
    @InjectForm(form = UserIdForm.class)
    public HttpResponse complete(HttpRequest req, ExecutionContext ctx) {

        UserIdForm userIdForm = ctx.getRequestScopedVar("form");

        // ユーザIDをリクエストスコープに格納
        ctx.setRequestScopedVar("userId", userIdForm.getUserId());

        // ユーザ削除完了画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/deleteComplete.jsp");
    }
}
