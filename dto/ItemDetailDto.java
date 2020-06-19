package jp.co.tis.rookies.dto;

import jp.co.tis.rookies.entity.Tag;

import java.util.Date;
import java.util.List;

/**
 * 投稿の検索結果格納用Dto。
 *
 * @author yamada sayo
 * @since 1.0
 */
public class ItemDetailDto {

    /**
     * 投稿ID
     */
    private String itemId;

    /**
     * タイトル
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 投稿ユーザID
     */
    private String userId;

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
     * タグリスト
     */
    private List<Tag> tagList;

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
     * タイトルを取得する。
     * @return タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * タイトルを設定する。
     * @param title タイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 内容を取得する。
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 内容を設定する。
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
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
     * 名前を取得する。
     * @return 名前
     */
    public String getName() {
        return getLastName() + " " + getFirstName();
    }

    /**
     * 姓を取得する。
     * @return 姓
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 姓を設定する。
     * @param lastName 姓
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 名を取得する。
     * @return 名
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 名を設定する。
     * @param firstName 名
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

    /**
     * タグリストを取得する。
     * @return タグリスト
     */
    public List<Tag> getTagList() {
        return tagList;
    }

    /**
     * タグリストを設定する。
     * @param tagList タグリスト
     */
    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }
}
