package com.challenge.authorizer.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "account"
})
@Data
public class TransactionResponseEntity {

    @JsonProperty("transaction")
    private TransactionResponseFields transactionResponseFields;

}