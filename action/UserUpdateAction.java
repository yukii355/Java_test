package jp.co.tis.rookies.action;


import jp.co.tis.rookies.common.authentication.AuthenticationUtil;
import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.dto.UserUpdateDto;
import jp.co.tis.rookies.entity.Department;
import jp.co.tis.rookies.entity.SystemAccount;
import jp.co.tis.rookies.form.UserIdForm;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.WebUtil;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.core.beans.BeanUtil;
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

public class UserUpdateAction {

    /** ユーザ情報保持用セッションキー */
    private static final String USER_UPDATE_SESSION_KEY = "updateUser";

    /** ユーザ情報入力内容更新用セッションキー */
    private static final String USER_UPDATE_NEWINFO_SESSION_KEY = "newUpdateUser";

    /**
     * ユーザ更新画面を初期表示する。
     *
     * @param req リクエスト
     * @param ctx HTTPリクエストの処理に関するサーバの情報
     * @return HttpResponse
     */
    @InjectForm(form = UserIdForm.class) // ユーザIDの単項目精査
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {

        // セッションストアを初期化
        SessionUtil.delete(ctx, USER_UPDATE_SESSION_KEY);
        SessionUtil.delete(ctx, USER_UPDATE_NEWINFO_SESSION_KEY);


        UserIdForm userIdForm = ctx.getRequestScopedVar("form");

        Map<String, String> param = new HashMap<>();
        param.put("userId", userIdForm.getUserId());
        UserUpdateDto userUpdateDto;

        // ユーザIDでユーザの情報を取得する。ユーザ更新可能精査。
        try {
            userUpdateDto = UniversalDao.findBySqlFile(UserUpdateDto.class, "FIND_EXISTS_USER_INFO_BY_USER_ID", param);
        }catch (NoDataException e){
            Message message = MessageUtil.createMessage(MessageLevel.ERROR, "rookies.user.detail.error.noData");
            WebUtil.notifyMessages(ctx, message);
            throw new HttpErrorResponse("forward:///action/userDetail/headerProfile", e);
        }

        ctx.setRequestScopedVar("updateAccount", userUpdateDto);
        ctx.setRequestScopedVar("deptList", UniversalDao.findAllBySqlFile(Department.class, "FIND_ALL"));
        ctx.setRequestScopedVar("userContext", SessionUtil.get(ctx, "userContext"));

        // ログインIDを格納するため
        SessionUtil.put(ctx, USER_UPDATE_SESSION_KEY, userUpdateDto);

        // ユーザ更新画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/update.jsp");

    }


    /**
     * ユーザ更新確認画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = UserUpdateDto.class, prefix = "updateAccount")
    @OnError(type = ApplicationException.class, path = "forward://back")
    public HttpResponse confirm(HttpRequest req, ExecutionContext ctx) {

        // セッションストアから、格納してあるログインIDを取得
        UserUpdateDto account = SessionUtil.get(ctx, USER_UPDATE_SESSION_KEY);

        // 更新画面で入力されているユーザ情報をリクエストスコープから取得
        UserUpdateDto userUpdateDto = ctx.getRequestScopedVar("form");


        // 部署ID存在精査のためのHashマップ作成
        Map<String, Integer> param = new HashMap<>();
        param.put("deptId", Integer.parseInt(userUpdateDto.getDeptId()));



        // 部署ID存在精査
        if (!UniversalDao.exists(Department.class, "FIND_BY_DEPT_ID", param)) {
            Message message = MessageUtil.createMessage(MessageLevel.ERROR, "rookies.user.edit.error.department.nothing");
            throw new ApplicationException(message);
        }


        // 権限変更精査
        LoginUserContext loginUserContext = SessionUtil.get(ctx, "userContext");
        if (!(loginUserContext.isAdmin() && !(userUpdateDto.getAdminFlag() == null))) {
            throw new HttpErrorResponse("/WEB-INF/view/common/errorPages/USER_ERROR.jsp");
        }


        // 入力値をセッションストアに格納
        SessionUtil.put(ctx, USER_UPDATE_NEWINFO_SESSION_KEY, userUpdateDto);

        // ログインIDを含むオブジェクトを格納
        ctx.setRequestScopedVar("account", account);

        // ログインID以外の更新画面で入力された情報を格納
        ctx.setRequestScopedVar("form", userUpdateDto);

        // 部署一覧を取得してリクエストスコープに格納
        ctx.setRequestScopedVar("deptList", UniversalDao.findAllBySqlFile(Department.class, "FIND_ALL"));

        // ユーザ権限をリクエストスコープに格納
        ctx.setRequestScopedVar("userContext", SessionUtil.get(ctx, "userContext"));

        // ユーザ更新確認画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/updateConfirm.jsp");
    }


    /**
     * 戻るボタンを押下後、ユーザ更新画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse back(HttpRequest req, ExecutionContext ctx) {

        // セッションに格納された入力値を取得
        UserUpdateDto userUpdateDto = SessionUtil.get(ctx, USER_UPDATE_SESSION_KEY);

        // リクエストスコープに格納
        ctx.setRequestScopedVar("form", userUpdateDto);

        // 部署一覧を取得してリクエストスコープに格納
        ctx.setRequestScopedVar("deptList", UniversalDao.findAllBySqlFile(Department.class, "FIND_ALL"));

        // ユーザ権限をリクエストスコープに格納
        ctx.setRequestScopedVar("userContext", SessionUtil.get(ctx, "userContext"));

        // ユーザ更新画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/update.jsp");
    }


    /**
     * ユーザ更新処理を行う。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    @OnError(type = ApplicationException.class, path = "forward://index")
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse update(HttpRequest req, ExecutionContext ctx) {

        // セッションストアを初期化(かつ、データを持ってくる)
        UserUpdateDto userUpdateDto = SessionUtil.delete(ctx, USER_UPDATE_SESSION_KEY);
        UserUpdateDto newUserUpdateDto = SessionUtil.delete(ctx, USER_UPDATE_NEWINFO_SESSION_KEY);

        // newUserUpdateDtoをuserUpdateDtoの中に代入する

        UserIdForm userIdForm = ctx.getRequestScopedVar("form");


        // 実行ユーザ権限精査
        LoginUserContext loginUserContext = SessionUtil.get(ctx, "userContext");
        if (!loginUserContext.isAdmin()) {
            if (!loginUserContext.getUserId().equals(userIdForm.getUserId())) {
                throw new ApplicationException();
            }
        }



        // DtoからEntityへ変換(DtoをSystemAccount.classにコピーしている。同じ変数があればコピーし、無ければ空のまま。BeanUtilでこれが出来る)
        SystemAccount systemAccount = BeanUtil.createAndCopy(SystemAccount.class, userUpdateDto);

        // パスワードの暗号化
        String encryptedPassword = AuthenticationUtil.encryptPassword(systemAccount.getLoginId(), systemAccount.getLoginPassword());

        systemAccount.setLoginPassword(encryptedPassword);

        // 削除フラグを立てる(初期値設定)
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


        // ユーザ更新完了画面表示メソッドにリダイレクトする際に、ユーザ更新完了画面から「更新したユーザ」のユーザ情報画面に遷移するため、
        // 「更新したユーザのユーザID」をクエリパラメータに設定する。
        return new HttpResponse("redirect://complete?userId" + Integer.toString(systemAccount.getUserId()));

    }


    /**
     * ユーザ更新完了画面表示を行う。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse complete(HttpRequest req, ExecutionContext ctx) {

        // ユーザ更新完了画面を表示する。
        return new HttpResponse("/WEB-INF/view/user/updateComplete.jsp");

    }

}
