package com.challenge.authorizer.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "account")
@Immutable
public final class AccountEntityImmutable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonProperty("active-card")
    private Boolean activecard;

    @JsonProperty("available-limit")
    private Integer availablelimit;

    public AccountEntityImmutable() {

    }

    public AccountEntityImmutable(long id, Boolean activecard, Integer availablelimit) {
        this.id = id;
        this.activecard = activecard;
        this.availablelimit =availablelimit;
    }

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("active-card")
    public Boolean getActivecard() {
        return activecard;
    }

    @JsonProperty("available-limit")
    public Integer getAvailablelimit() {
        return availablelimit;
    }


}
