package com.challenge.authorizer.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountResponseImmutable {

    @JsonProperty("active-card")
    private final boolean activecard;

    @JsonProperty("available-limit")
    private final Integer availablelimit;


    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AccountResponseImmutable(@JsonProperty("active-card") Boolean activecard,
                                    @JsonProperty("available-limit") Integer availablelimit) {
        this.activecard = activecard;
        this.availablelimit = availablelimit;
    }

    public boolean getActivecard() {
        return activecard;
    }
    public Integer getAvailablelimit() {
        return availablelimit;
    }

}
