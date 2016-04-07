package id.pantauharga.android.modelgson;

/**
 * Created by widodo on 4/6/16.
 */
public class VerifyUser {
    String serverAuthCode;
    String email;
    String idToken;
    String androidId;

    public VerifyUser() {
    }

    public void setServerAuthCode(String serverAuthCode) {
        this.serverAuthCode = serverAuthCode;
    }

    public String getServerAuthCode() {
        return serverAuthCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getAndroidId() {
        return androidId;
    }
}
