package jp.co.tis.rookies.dto;

import java.util.Date;

/**
 * 投稿の検索結果格納用Dto。
 *
 * @author yamada sayo
 * @since 1.0
 */
public class ItemCommentDto {

    /**
     * 投稿ID
     */
    private String itemId;

    /**
     * コメントID
     */
    private String commentId;

    /**
     * コメントユーザID
     */
    private String userId;

    /**
     * コメント内容
     */
    private String content;

    /**
     * 投稿ユーザ姓
     */
    private String lastName;

    /**
     * 投稿ユーザ名
     */
    private String firstName;

    /**
     * 更新日時
     */
    private Date updateTime;

    /**
     * 投稿IDを取得する。
     * @return 投稿ID
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * 投稿IDを設定する。
     * @param itemId 投稿ID
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * コメントIDを取得する。
     * @return コメントID
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * コメントIDを設定する。
     * @param commentId コメントID
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    /**
     * ユーザIDを取得する。
     * @return ユーザID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザIDを設定する。
     * @param userId ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 投稿内容を取得する。
     * @return 投稿内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 投稿内容を設定する。
     * @param content 投稿内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * コメントユーザ姓名を取得する。
     * @return コメントユーザ姓名
     */
    public String getName() {
        return getLastName() + " " + getFirstName();
    }

    /**
     * コメントユーザ姓を取得する。
     * @return コメントユーザ姓
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * コメントユーザ姓を設定する。
     * @param lastName コメントユーザ姓
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * コメントユーザ名を取得する。
     * @return コメントユーザ名
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * コメントユーザ名を設定する。
     * @param firstName コメントユーザ名
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 更新日時を取得する。
     * @return 更新日時
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新日時を設定する。
     * @param updateTime 更新日時
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
