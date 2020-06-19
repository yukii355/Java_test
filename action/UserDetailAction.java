package jp.co.tis.rookies.action;

import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.dto.UserDetailDto;
import jp.co.tis.rookies.form.UserIdForm;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.core.message.ApplicationException;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

import java.util.HashMap;
import java.util.Map;

/**
 * ユーザ情報詳細。
 *
 * @author Moriyama Hiroyuki
 * @since 1.0
 */
public class UserDetailAction {

     /**
     * ヘッダからユーザ情報詳細画面を表示する。
     * @param req リクエスト
     * @param ctx HTTPリクエストの処理に関するサーバの情報
     * @return HttpResponse
     */
    @InjectForm(form = UserIdForm.class) // ユーザIDの単項目精査
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse headerProfile(HttpRequest req, ExecutionContext ctx) {


        UserIdForm userIdForm = ctx.getRequestScopedVar("form");


        LoginUserContext userContext = SessionUtil.get(ctx, "userContext");

        // 実行ユーザ権限精査
        if (!userContext.isAdmin()) {

            if (!userContext.getUserId().equals(userIdForm.getUserId())) {
                throw new ApplicationException();
            }
        }

        Map<String, String> param = new HashMap<>();
        param.put("userId", userIdForm.getUserId());
        UserDetailDto dto = null;
        try {
            dto = UniversalDao.findBySqlFile(UserDetailDto.class, "FIND_USER_INFO_BY_USER_ID", param);
        } catch (NoDataException e) {
            throw new ApplicationException();
        }
        ctx.setRequestScopedVar("searchResult", dto);

        return new HttpResponse("/WEB-INF/view/user/detail.jsp");

    }
}
