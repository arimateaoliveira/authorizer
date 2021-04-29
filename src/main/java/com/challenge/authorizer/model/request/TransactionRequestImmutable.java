package com.challenge.authorizer.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


public final class TransactionRequestImmutable {

    @JsonProperty("merchant")
    private final String merchant;

    @JsonProperty("amount")
    private final Integer amount;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("time")
    private final Date time;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public  TransactionRequestImmutable(@JsonProperty("merchant") String merchant,
                                        @JsonProperty("amount") Integer amount,
                                        @JsonProperty("time") Date time) {
        this.merchant = merchant;
        this.amount = amount;
        this.time = time;

    }

    public String getMerchant() {
        return merchant;
    }

    public Integer getAmount() {
        return amount;
    }

    public Date getTime() {
        return time;
    }
}
