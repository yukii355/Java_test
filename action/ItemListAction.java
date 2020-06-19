package jp.co.tis.rookies.action;

import jp.co.tis.rookies.component.ItemCompo;
import jp.co.tis.rookies.dto.ItemListDto;
import jp.co.tis.rookies.entity.Tag;
import jp.co.tis.rookies.form.ItemListForm;
import jp.co.tis.rookies.form.ItemListPagingForm;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.core.message.ApplicationException;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投稿一覧Action。
 *
 * @author Yutaka Kanayama
 * @since 1.0
 *
 */
public class ItemListAction {

    /** 検索条件格納用セッションキー。 */
    private static final String SEARCH_CONDITION_SESSION_KEY = "searchConditionForItem";

    /** ページ番号格納用セッションキー。 */
    private static final String PAGING_NUM_SESSION_KEY = "pagingNumForItem";

    /** 初期ページの番号。 */
    private static final long FIRST_PAGE = 1L;

    /**
     * 投稿一覧画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse index(HttpRequest req, ExecutionContext ctx) {
        // 初期状態で検索条件なしをセッションに保存する。
        ItemListForm form = new ItemListForm();
        SessionUtil.put(ctx, SEARCH_CONDITION_SESSION_KEY, form);
        SessionUtil.put(ctx, PAGING_NUM_SESSION_KEY, FIRST_PAGE);

        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("tags", compo.findAllTagList());
        ctx.setRequestScopedVar("searchResult", findItemList(form, FIRST_PAGE));

        return new HttpResponse("/WEB-INF/view/item/list.jsp");
    }

    /**
     * 投稿検索でエラーが発生した際、投稿一覧を表示しないで画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse searchOnError(HttpRequest req, ExecutionContext ctx) {
        SessionUtil.delete(ctx, SEARCH_CONDITION_SESSION_KEY);
        SessionUtil.delete(ctx, PAGING_NUM_SESSION_KEY);

        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("tags", compo.findAllTagList());
        ctx.setRequestScopedVar("showList", false);

        return new HttpResponse("/WEB-INF/view/item/list.jsp");
    }

    /**
     * 投稿検索を行う。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemListForm.class, prefix = "form")
    @OnError(type = ApplicationException.class, path = "forward://searchOnError")
    public HttpResponse search(HttpRequest req, ExecutionContext ctx) {
        ItemListForm form = ctx.getRequestScopedVar("form");

        SessionUtil.put(ctx, SEARCH_CONDITION_SESSION_KEY, form);
        SessionUtil.put(ctx, PAGING_NUM_SESSION_KEY, FIRST_PAGE);

        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("tags", compo.findAllTagList());
        ctx.setRequestScopedVar("searchResult", findItemList(form, FIRST_PAGE));

        return new HttpResponse("/WEB-INF/view/item/list.jsp");
    }

    /**
     * ページングを行う。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = ItemListPagingForm.class, name = "pagingForm")
    @OnError(type = ApplicationException.class, path = "/WEB-INF/view/common/errorPages/USER_ERROR.jsp")
    public HttpResponse paging(HttpRequest req, ExecutionContext ctx) {
        ItemListPagingForm pagingForm = ctx.getRequestScopedVar("pagingForm");
        ItemListForm form = SessionUtil.get(ctx, SEARCH_CONDITION_SESSION_KEY);

        SessionUtil.put(ctx, PAGING_NUM_SESSION_KEY, Long.parseLong(pagingForm.getPageNum()));

        ItemCompo compo = new ItemCompo();
        ctx.setRequestScopedVar("form", form);
        ctx.setRequestScopedVar("tags", compo.findAllTagList());
        ctx.setRequestScopedVar("searchResult", findItemList(form, Long.parseLong(pagingForm.getPageNum())));

        return new HttpResponse("/WEB-INF/view/item/list.jsp");
    }

    /**
     * 検索条件をもとに投稿情報を取得する。
     *
     * @param searchCondition 投稿情報の検索条件
     * @param pageNum         ページ番号
     * @return 投稿情報のリスト
     */
    private EntityList<ItemListDto> findItemList(ItemListForm searchCondition, long pageNum) {
        EntityList<ItemListDto> result = UniversalDao
                .per(Long.parseLong(SystemRepository.getString("rookies.listSearch.count.per"))).page(pageNum)
                .findAllBySqlFile(ItemListDto.class, "FIND_ITEM_LIST_BY_SEARCH_CONDITION", searchCondition);
        result.stream().forEach(itemListDto -> itemListDto.setTags(findTagList(itemListDto.getItemId())));
        return result;
    }

    /**
     * 投稿IDに紐づけられたタグを取得する。
     * 
     * @param itemId 投稿ID
     * @return 投稿IDに紐づけられたタグのリスト
     */
    private List<Tag> findTagList(Integer itemId) {
        Map<String, Integer> param = new HashMap<>();
        param.put("itemId", itemId);
        return UniversalDao.findAllBySqlFile(Tag.class, "FIND_NAME_BY_ITEM_ID", param);
    }

}
