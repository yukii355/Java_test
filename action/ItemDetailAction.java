package jp.co.tis.rookies.action;

import jp.co.tis.rookies.common.authentication.context.LoginUserContext;
import jp.co.tis.rookies.component.ItemCompo;
import jp.co.tis.rookies.dto.ItemCommentDto;
import jp.co.tis.rookies.dto.ItemDetailDto;
import jp.co.tis.rookies.entity.ItemComment;
import jp.co.tis.rookies.entity.Tag;
import jp.co.tis.rookies.form.ItemCommentCreateForm;
import jp.co.tis.rookies.form.ItemCommentDeleteForm;
import jp.co.tis.rookies.form.ItemIdForm;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.WebUtil;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.core.beans.BeanUtil;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

import java.util.Date;
import java.util.List;

/**
 * 投稿詳細アクション。
 *
 * @author yamada sayo
 * @since 1.0
 */
public class ItemDetailAction {

    /**
     * 投稿詳細画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemIdForm.class)
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {
        ItemIdForm form = ctx.getRequestScopedVar("form");

        ItemDetailDto dto;
        try {
            dto = UniversalDao.findBySqlFile(ItemDetailDto.class, "FIND_ITEM_BY_ITEM_ID", form);
        } catch (NoDataException e) {
            // 投稿存在エラー
            Message message = MessageUtil
                    .createMessage(MessageLevel.ERROR, "rookies.user.detail.error.noData");
            ctx.setRequestScopedVar("form", null);
            WebUtil.notifyMessages(ctx, message);
            throw new HttpErrorResponse("forward:///action/itemList/index", e);
        }

        List<Tag> tagList = UniversalDao.findAllBySqlFile(Tag.class, "FIND_NAME_BY_ITEM_ID", form);
        dto.setTagList(tagList);

        List<ItemCommentDto> commentDtoList = UniversalDao.findAllBySqlFile(ItemCommentDto.class, "FIND_COMMENT_BY_ITEM_ID", form);

        ctx.setRequestScopedVar("item", dto);
        ctx.setRequestScopedVar("comments", commentDtoList);

        return new HttpResponse("/WEB-INF/view/item/detail.jsp");
    }

    /**
     * コメント登録を行う。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemCommentCreateForm.class)
    @OnError(type = ApplicationException.class, path = "forward://index")
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse createComment(HttpRequest req, ExecutionContext ctx) {
        ItemCommentCreateForm form = ctx.getRequestScopedVar("form");

        LoginUserContext userContext = SessionUtil.get(ctx, "userContext");
        Date currentTime = SystemTimeUtil.getDate();

        ItemComment itemComment = BeanUtil.createAndCopy(ItemComment.class, form);
        itemComment.setUserId(Integer.parseInt(userContext.getUserId()));
        itemComment.setCreateTime(currentTime);
        itemComment.setUpdateTime(currentTime);

        UniversalDao.insert(itemComment);

        return new HttpResponse("redirect://index?itemId=" + form.getItemId());
    }

    /**
     * コメント削除を行う。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemCommentDeleteForm.class, prefix = "form")
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    @OnDoubleSubmission(path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse deleteComment(HttpRequest req, ExecutionContext ctx) {
        ItemCommentDeleteForm form = ctx.getRequestScopedVar("form");

        ItemComment itemComment = UniversalDao.findById(ItemComment.class, form.getCommentId());
        LoginUserContext userContext = SessionUtil.get(ctx, "userContext");
        // 削除権限精査
        ItemCompo compo = new ItemCompo();
        if (!compo.hasDeletePermission(itemComment.getUserId(), userContext)) {
            throw new ApplicationException();
        }

        UniversalDao.delete(itemComment);

        return new HttpResponse("redirect://index?itemId=" + form.getItemId());
    }
}
