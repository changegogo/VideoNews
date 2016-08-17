package feicuiedu.com.videonews.bombapi.model.other;


import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class RelationOperation {

    public enum Operation{
        AddRelation,
        RemoveRelation
    }

    @SerializedName("__op")
    private Operation operation;

    private List<Pointer> objects;

    public RelationOperation() {
    }

    public RelationOperation(Operation operation, Pointer... pointers) {
        this.operation = operation;
        this.objects = Arrays.asList(pointers);
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setObjects(List<Pointer> objects) {
        this.objects = objects;
    }
}
