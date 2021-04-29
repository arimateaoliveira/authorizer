package com.challenge.authorizer.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseFields {

    @JsonProperty("active-card")
    private boolean activecard;

    @JsonProperty("available-limit")
    private Integer availablelimit;

    @JsonProperty("violations")
    private List<String> violations;

}
