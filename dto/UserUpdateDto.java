package jp.co.tis.rookies.dto;


import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

import javax.validation.constraints.AssertTrue;
import java.io.Serializable;
import java.util.Objects;

/**
 * ユーザ更新Dto。
 *
 * @author Moriyama Hiroyuki
 * @since 1.0
 */
public class UserUpdateDto implements Serializable {

    /**
     * シリアルバージョンUID
     */
    public static final long serialVersionUID = 1L;



    /**  ユーザID  */
    private String userId;

    /**
     * ログインID
     */
    private String loginId;


    /**
     * 名前（姓）
     */
    @Required
    @Domain("kanjiName")
    private String lastName;


    /**
     * 名前（名）
     */
    @Required
    @Domain("kanjiName")
    private String firstName;


    /**
     * 部署
     */
    @Required
    @Domain("id")
    private String deptId;


    /**
     * メールアドレス
     */
    @Required
    @Domain("mailAddress")
    private String mailAddress;


    /**
     * パスワード
     */
    @Domain("password")
    private String loginPassword;


    /**
     * パスワード(確認)
     */
    @Domain("password")
    private String confirmPassword;


    /**
     * ユーザ権限
     */
    @Domain("userClass")
    private String adminFlag;


    /**
     * ユーザIDの取得。
     * @return userId ユーザID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザIDの設定
     * @param userId ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * ログインIDの取得。
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
     * 名前（姓）の取得。
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
     * 名前（名）の取得。
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
     * 部署の取得。
     * @return 部署
     */
    public String getDeptId() {
        return deptId;
    }

    /**
     * 部署を設定する。
     * @param deptId 部署
     */
    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /**
     * メールアドレスの取得。
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
     * パスワードの取得。
     * @return パスワード
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * パスワードを設定する。
     * @param loginPassword パスワード
     */
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    /**
     * パスワード（確認）の取得。
     * @return パスワード（確認）
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * パスワード（確認）を設定する。
     * @param confirmPassword パスワード（確認）
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * ユーザ権限の取得。
     * @return ユーザ権限
     */
    public String getAdminFlag() {
        return adminFlag;
    }

    /**
     * ユーザ権限を設定する。
     * @param adminFlag ユーザ権限
     */
    public void setAdminFlag(String adminFlag) {
        this.adminFlag = adminFlag;
    }

    /**
     * 入力されたパスワードが一致しているかの項目間精査（相関バリデーション）
     * @return パスワードが一致しているか
     */
    @AssertTrue(message = "{rookies.user.edit.error.password}")
    public boolean isEqualsPassword() {
        if (StringUtil.isNullOrEmpty(loginPassword) || StringUtil.isNullOrEmpty(confirmPassword)) {
            // どちらかが未入力の場合は、相関バリデーションは実施しない（バリデーションOKとする）
            return true;
        }
        return Objects.equals(loginPassword, confirmPassword);
    }



}

