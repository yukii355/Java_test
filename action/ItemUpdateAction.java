package jp.co.tis.rookies.action;

import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.component.ItemCompo;
import jp.co.tis.rookies.entity.Item;
import jp.co.tis.rookies.entity.ItemTag;
import jp.co.tis.rookies.form.ItemIdForm;
import jp.co.tis.rookies.form.ItemUpdateForm;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.WebUtil;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.core.beans.BeanUtil;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 投稿編集Action。
 *
 * @author yamada sayo
 * @since 1.0
 *
 */
public class ItemUpdateAction {

    /** 投稿情報を保持するためのセッションキー */
    private static final String ITEM_SESSION_KEY = "itemUpdate";

    /** 投稿更新フォームを保持するためのセッションキー */
    private static final String FORM_SESSION_KEY = "itemUpdateForm";

    /**
     * 投稿編集画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemIdForm.class, prefix = "form")
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {
        ItemIdForm form = ctx.getRequestScopedVar("form");
        return initIndexPage(ctx, Integer.parseInt(form.getItemId()));
    }

    /**
     * エラーがあった場合に投稿更新画面を初期表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse indexOnError(HttpRequest req, ExecutionContext ctx) {
        Item item = SessionUtil.get(ctx, ITEM_SESSION_KEY);
        return initIndexPage(ctx, item.getItemId());
    }

    /**
     * 投稿更新画面を初期表示する際の共通処理。
     *
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @param itemId 表示する投稿のId
     * @return HTTPレスポンス
     */
    private HttpResponse initIndexPage(ExecutionContext ctx, Integer itemId) {
        SessionUtil.delete(ctx, ITEM_SESSION_KEY);
        SessionUtil.delete(ctx, FORM_SESSION_KEY);

        Item item;
        try {
            item = UniversalDao.findById(Item.class, itemId);
        } catch (NoDataException e) {
            // 一覧画面で問題が出る(要素が入っていると展開しようとしてしまう)ため、formという名前のフォームの要素にはnullを設定
            ctx.setRequestScopedVar("form", null);
            WebUtil.notifyMessages(ctx, MessageUtil.createMessage(MessageLevel.ERROR, "rookies.item.detail.error.noData"));
            throw new HttpErrorResponse("forward:///action/itemList/index");
        }

        String[] itemTagArr = UniversalDao.findAllBySqlFile(ItemTag.class, "FIND_BY_ITEM_ID", item)
                .stream().map(itemTag -> itemTag.getTagId().toString()).toArray(String[]::new);
        ItemUpdateForm form = BeanUtil.createAndCopy(ItemUpdateForm.class, item);
        form.setItemTagArr(itemTagArr);

        SessionUtil.put(ctx, ITEM_SESSION_KEY, item);
        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("form", form);
        ctx.setRequestScopedVar("tags", compo.findAllTagList());

        return new HttpResponse("/WEB-INF/view/item/update.jsp");
    }

    /**
     * 投稿確認画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemUpdateForm.class, prefix = "form")
    @OnError(type = ApplicationException.class, path = "forward://indexOnError")
    public HttpResponse confirm(HttpRequest req, ExecutionContext ctx) {
        ItemUpdateForm form = ctx.getRequestScopedVar("form");

        String[] tagIdArray = form.getItemTagArr();
        validateExistsTagIds(tagIdArray);

        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("tags", compo.findAllTagList());
        // sessionに保持しているFormに入力内容を反映
        SessionUtil.put(ctx, FORM_SESSION_KEY, form);

        return new HttpResponse("/WEB-INF/view/item/updateConfirm.jsp");
    }

    /**
     * 戻るボタンを押下での投稿画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse back(HttpRequest req, ExecutionContext ctx) {
        ItemUpdateForm form = SessionUtil.get(ctx, FORM_SESSION_KEY);
        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("form", form);
        ctx.setRequestScopedVar("tags", compo.findAllTagList());

        return new HttpResponse("/WEB-INF/view/item/update.jsp");
    }

    /**
     * 投稿を更新する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse update(HttpRequest req, ExecutionContext ctx) {
        Item item = SessionUtil.get(ctx, ITEM_SESSION_KEY);
        ItemUpdateForm form = SessionUtil.delete(ctx, FORM_SESSION_KEY);
        LoginUserContext loginUserContext = SessionUtil.get(ctx, "userContext");

        // 編集権限精査
        if (!loginUserContext.getUserId().equals(item.getUserId().toString())) {
            SessionUtil.delete(ctx, ITEM_SESSION_KEY);
            throw new ApplicationException();
        }

        BeanUtil.copy(form, item);
        item.setUpdateTime(SystemTimeUtil.getDate());
        // 更新処理
        try {
            UniversalDao.update(item);
        } catch (OptimisticLockException e) {
            // 排他エラー
            WebUtil.notifyMessages(ctx, MessageUtil.createMessage(MessageLevel.ERROR, "rookies.common.optimistic.message"));
            // エラーメッセージを表示したいのでforwardで遷移する
            throw new HttpErrorResponse("forward://indexOnError");
        }

        UniversalDao.batchDelete(UniversalDao.findAllBySqlFile(ItemTag.class, "FIND_BY_ITEM_ID", item));
        if (Objects.nonNull(form.getItemTagArr())) {
            List<ItemTag> itemTagList = new ArrayList<>();
            Arrays.stream(form.getItemTagArr()).forEach(tagId -> {
                ItemTag itemTag = new ItemTag();
                itemTag.setItemId(item.getItemId());
                itemTag.setTagId(Integer.parseInt(tagId));
                itemTagList.add(itemTag);
            });
            UniversalDao.batchInsert(itemTagList);
        }

        SessionUtil.delete(ctx, ITEM_SESSION_KEY);

        return new HttpResponse("redirect:///action/itemDetail/index?itemId=" + item.getItemId().toString());
    }

    /**
     * タグIDが存在するか精査する。 <br>
     *
     * @throws ApplicationException 存在しないタグIDが含まれていた場合
     * @param tagIds 精査対象のタグID
     */
    private void validateExistsTagIds(String[] tagIds) {
        ItemCompo compo = new ItemCompo();
        if (!compo.isExistsTags(tagIds)) {
            throw new ApplicationException(
                    MessageUtil.createMessage(MessageLevel.ERROR, "rookies.item.edit.error.tag.nothing"));
        }
    }
}
