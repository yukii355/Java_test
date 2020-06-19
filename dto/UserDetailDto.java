package jp.co.tis.rookies.dto;

import java.io.Serializable;

/**
 * ユーザ情報用DTO。
 *
 * @author Moriyama Hiroyuki
 * @since 1.0
 */
public class UserDetailDto implements Serializable {

    /**
     * シリアルバージョンUID。
     */
    public static final long serialVersionUID = 1L;

    /**  ユーザID  */
    private String userId;
    /**  ログインID  */
    private String loginId;
    /**  名前（姓）  */
    private String lastName;
    /**  名前（名）  */
    private String firstName;
    /**  部署名  */
    private String deptName;
    /**  メールアドレス  */
    private String mailAddress;
    /**  管理者フラグ  */
    private String adminFlag;
    /**  削除フラグ  */
    private String deletedFlag;


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
     * ログインIDを取得する。
     * @return ログインID
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * ログインIDを設定する。
     * @param loginId ログインID
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * 名前（姓）を取得する。
     * @return 名前（姓）
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 名前（姓）を設定する。
     * @param lastName 名前（姓）
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 名前（名）を取得する。
     * @return 名前（名）
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 名前（名）を設定する。
     * @param firstName 名前（名）
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 部署名を取得する。
     * @return 部署名
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * 部署名を設定する。
     * @param deptName 部署名
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * メールアドレスを取得する。
     * @return メールアドレス
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * メールアドレスを設定する。
     * @param mailAddress メールアドレス
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * 管理者フラグを取得する。
     * @return 管理者フラグ
     */
    public String getAdminFlag() {
        return adminFlag;
    }

    /**
     * 管理者フラグを設定する。
     * @param adminFlag 管理者フラグ
     */
    public void setAdminFlag(String adminFlag) {
        this.adminFlag = adminFlag;
    }

    /**
     * 削除フラグを取得する。
     * @return 削除フラグ
     */
    public String getDeletedFlag() {
        return deletedFlag;
    }

    /**
     * 削除フラグを設定する。
     * @param deletedFlag 削除フラグ
     */
    public void setDeletedFlag(String deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}
