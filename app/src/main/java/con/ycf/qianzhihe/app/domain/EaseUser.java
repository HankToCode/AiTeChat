package con.ycf.qianzhihe.app.domain;

public class EaseUser extends com.hyphenate.easeui.domain.EaseUser {

    public EaseUser() {

    }

    public EaseUser(String username) {
        super(username);
    }


    /**
     * 千纸鹤号
     */
    private String friendUserCode;

    private String friendUserId;

    private String friendNickName;

    private String nickName;
    private String userCode;
    private String account;
    private String password;
    private String line;

    private String type;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendUserCode() {
        return friendUserCode;
    }

    public void setFriendUserCode(String friendUserCode) {
        this.friendUserCode = friendUserCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


}
