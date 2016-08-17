package feicuiedu.com.videonews.bombapi.model.other;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Pointer {

    @SerializedName("__type")
    private final String type = "Pointer";

    private String className;

    private String objectId;

    public Pointer(String className, String objectId) {
        this.className = className;
        this.objectId = objectId;
    }

    public Pointer() {
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
