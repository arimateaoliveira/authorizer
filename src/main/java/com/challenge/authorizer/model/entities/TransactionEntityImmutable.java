package com.challenge.authorizer.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntityImmutable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonProperty("merchant")
    private String merchant;

    @JsonProperty("amount")
    private Integer amount;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("time")
    private Date time;

}
