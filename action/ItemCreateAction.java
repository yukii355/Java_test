package jp.co.tis.rookies.action;

import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.component.ItemCompo;
import jp.co.tis.rookies.entity.Item;
import jp.co.tis.rookies.entity.ItemTag;
import jp.co.tis.rookies.form.ItemCreateForm;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.core.beans.BeanUtil;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

import java.util.Objects;

/**
 * 投稿Action。
 *
 * @author Yutaka Kanayama
 * @since 1.0
 *
 */
public class ItemCreateAction {
    /** 入力値を保持するFormのセッションキー */
    private static final String FORM_SESSION_KEY = "itemCreateForm";

    /**
     * 投稿画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {
        SessionUtil.delete(ctx, FORM_SESSION_KEY);
        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("tags", compo.findAllTagList());

        return new HttpResponse("/WEB-INF/view/item/create.jsp");
    }

    /**
     * 投稿確認画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemCreateForm.class, prefix = "form")
    @OnError(type = ApplicationException.class, path = "forward://index")
    public HttpResponse confirm(HttpRequest req, ExecutionContext ctx) {
        ItemCreateForm form = ctx.getRequestScopedVar("form");
        String[] tagIdArray = form.getTagIds().stream().filter(tagIdForm -> Objects.nonNull(tagIdForm))
                .map(tagIdForm -> tagIdForm.getTagId()).toArray(String[]::new);
        validateExistsTagIds(tagIdArray);
        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("tags", compo.findAllTagList());
        SessionUtil.put(ctx, FORM_SESSION_KEY, form);
        return new HttpResponse("/WEB-INF/view/item/createConfirm.jsp");
    }

    /**
     * 戻るボタンを押下での投稿画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse back(HttpRequest req, ExecutionContext ctx) {
        ctx.setRequestScopedVar("form", SessionUtil.delete(ctx, FORM_SESSION_KEY));
        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("tags", compo.findAllTagList());
        return new HttpResponse("/WEB-INF/view/item/create.jsp");
    }

    /**
     * 投稿を登録する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse create(HttpRequest req, ExecutionContext ctx) {
        ItemCreateForm form = SessionUtil.delete(ctx, FORM_SESSION_KEY);
        LoginUserContext userContext = SessionUtil.get(ctx, "userContext");

        Item item = BeanUtil.createAndCopy(Item.class, form);
        item.setUserId(Integer.parseInt(userContext.getUserId()));
        item.setCreateTime(SystemTimeUtil.getDate());
        item.setUpdateTime(SystemTimeUtil.getDate());
        item.setVersionNo(0L);
        UniversalDao.insert(item);

        form.getTagIds().stream().filter(tagIdForm -> Objects.nonNull(tagIdForm)).map(tagIdForm -> tagIdForm.getTagId())
                .forEach(tagId -> {
                    ItemTag itemTag = new ItemTag();
                    itemTag.setItemId(item.getItemId());
                    itemTag.setTagId(Integer.parseInt(tagId));
                    UniversalDao.insert(itemTag);
                });

        return new HttpResponse("redirect://itemList/index");
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
                    MessageUtil.createMessage(MessageLevel.ERROR, "rookies.item.create.error.tag.nothing"));
        }
    }
}
