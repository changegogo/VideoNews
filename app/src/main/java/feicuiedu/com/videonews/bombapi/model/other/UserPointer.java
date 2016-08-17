package feicuiedu.com.videonews.bombapi.model.other;


import feicuiedu.com.videonews.bombapi.BombConst;

@SuppressWarnings("unused")
public class UserPointer extends Pointer{

    private String username;

    public UserPointer(String objectId) {
        super(BombConst.TABLE_USER, objectId);
    }

    public UserPointer() {
    }

    public String getUsername() {
        return username;
    }
}
