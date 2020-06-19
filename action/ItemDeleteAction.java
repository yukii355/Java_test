package jp.co.tis.rookies.action;

import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.component.ItemCompo;
import jp.co.tis.rookies.entity.Item;
import jp.co.tis.rookies.entity.ItemComment;
import jp.co.tis.rookies.entity.ItemTag;
import jp.co.tis.rookies.entity.Tag;
import jp.co.tis.rookies.form.ItemIdForm;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.WebUtil;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

/**
 * 投稿削除アクション。
 *
 * @author yamada sayo
 * @since 1.0
 */
public class ItemDeleteAction {

    /** 投稿情報を保持するためのセッションキー */
    private static final String ITEM_SESSION_KEY = "deleteItem";

    /**
     * 投稿削除画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemIdForm.class)
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {
        SessionUtil.delete(ctx, ITEM_SESSION_KEY);

        ItemIdForm form = ctx.getRequestScopedVar("form");

        Item item;
        try {
            item = UniversalDao.findById(Item.class, Integer.parseInt(form.getItemId()));
        } catch (NoDataException e) {
            // 一覧画面で問題が出る(要素が入っていると展開しようとしてしまう)ため、formという名前のフォームの要素にはnullを設定
            ctx.setRequestScopedVar("form", null);
            WebUtil.notifyMessages(ctx, MessageUtil.createMessage(MessageLevel.ERROR, "rookies.item.detail.error.noData"));
            throw new HttpErrorResponse("forward:///action/itemList/index", e);
        }

        ctx.setRequestScopedVar("form", item);
        ctx.setRequestScopedVar("tagList", UniversalDao.findAllBySqlFile(Tag.class, "FIND_NAME_BY_ITEM_ID", form));

        SessionUtil.put(ctx, ITEM_SESSION_KEY, item);

        return new HttpResponse("/WEB-INF/view/item/delete.jsp");
    }

    /**
     * 投稿削除を行う。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse delete(HttpRequest req, ExecutionContext ctx) {
        Item item = SessionUtil.delete(ctx, ITEM_SESSION_KEY);
        LoginUserContext loginUserContext = SessionUtil.get(ctx, "userContext");
        // 削除権限精査
        ItemCompo compo = new ItemCompo();
        if (!compo.hasDeletePermission(item.getUserId(), loginUserContext)) {
            throw new ApplicationException();
        }

        UniversalDao.batchDelete(UniversalDao.findAllBySqlFile(ItemTag.class, "FIND_BY_ITEM_ID", item));
        UniversalDao.batchDelete(UniversalDao.findAllBySqlFile(ItemComment.class, "FIND_COMMENT_ID_BY_ITEM_ID", item));
        // レコードの制約上、ITEMテーブルのデータを最後に削除
        UniversalDao.delete(item);

        // 投稿一覧初期表示にリダイレクト
        return new HttpResponse("redirect:///action/itemList/index");
    }
}
