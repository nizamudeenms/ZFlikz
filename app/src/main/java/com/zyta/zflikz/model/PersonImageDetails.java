
package com.zyta.zflikz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PersonImageDetails implements Serializable
{

    @SerializedName("profiles")
    @Expose
    private List<PersonImage> personImage = null;
    @SerializedName("id")
    @Expose
    private Integer id;
    private final static long serialVersionUID = 1470486385854690043L;

    public List<PersonImage> getPersonImage() {
        return personImage;
    }

    public void setPersonImage(List<PersonImage> personImage) {
        this.personImage = personImage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
