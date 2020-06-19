package jp.co.tis.rookies.dto;

import java.util.Date;
import java.util.List;

import jp.co.tis.rookies.entity.Tag;

/**
 * 投稿一覧表示のためのDto
 * 
 * @author Yutaka Kanayama
 * @since 1.0
 */
public class ItemListDto {

    /** 投稿ID */
    private Integer itemId;

    /** 投稿タイトル */
    private String title;

    /** 投稿内容 */
    private String content;

    /** タグ */
    private List<Tag> tags;

    /** 投稿ユーザ名 */
    private String authorName;

    /** 作成日時 */
    private Date createTime;

    /** コメント数 */
    private Long commentNum;

    /**
     * 投稿IDを取得する。
     * @return 投稿ID
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 投稿IDを設定する。
     * @param itemId 投稿ID
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 投稿タイトルを取得する。
     * 
     * @return 投稿タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * 投稿タイトルを設定する。
     * 
     * @param title 投稿タイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 投稿内容を取得する。
     * 
     * @return 投稿内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 投稿内容を設定する。
     * 
     * @param content 投稿内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * タグを取得する。
     * 
     * @return タグ
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * タグを設定する。
     * 
     * @param tags タグ
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * 投稿ユーザ名を取得する。
     * 
     * @return 投稿ユーザ名
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * 投稿ユーザ名を設定する。
     * 
     * @param authorName 投稿ユーザ名
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * 作成日時を取得する。
     * 
     * @return 作成日時
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 作成日時を設定する。
     * 
     * @param createTime 作成日時
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * コメント数を取得する。
     * 
     * @return コメント数
     */
    public Long getCommentNum() {
        return commentNum;
    }

    /**
     * コメント数を設定する。
     * 
     * @param commentNum コメント数
     */
    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

}