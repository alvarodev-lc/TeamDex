package es.upm.miw.virgolini.models;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Pokemon {

    @SerializedName("types")
    @Expose
    private List<TypeList> types = null;

    public List<TypeList> getTypes() {
        return types;
    }

    public void setTypes(List<TypeList> types) {
        this.types = types;
    }

}