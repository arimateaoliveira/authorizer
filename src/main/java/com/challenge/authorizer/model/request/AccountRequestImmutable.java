package com.challenge.authorizer.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountRequestImmutable {

    @JsonProperty("active-card")
    private final Boolean activecard;

    @JsonProperty("available-limit")
    private final Integer availablelimit;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AccountRequestImmutable(@JsonProperty("active-card") Boolean activecard,
                                   @JsonProperty("available-limit") Integer availablelimit) {
        this.activecard = activecard;
        this.availablelimit = availablelimit;
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