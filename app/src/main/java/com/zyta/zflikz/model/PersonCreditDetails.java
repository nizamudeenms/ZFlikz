
package com.zyta.zflikz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PersonCreditDetails implements Serializable
{

    @SerializedName("cast")
    @Expose
    private List<PersonCast> personCast = null;
    @SerializedName("crew")
    @Expose
    private List<PersonCrew> personCrew = null;
    @SerializedName("id")
    @Expose
    private Integer id;
    private final static long serialVersionUID = -8783940607490574947L;

    public List<PersonCast> getPersonCast() {
        return personCast;
    }

    public void setPersonCast(List<PersonCast> personCast) {
        this.personCast = personCast;
    }

    public List<PersonCrew> getPersonCrew() {
        return personCrew;
    }

    public void setPersonCrew(List<PersonCrew> personCrew) {
        this.personCrew = personCrew;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
