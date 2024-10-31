package ru.t1.java.demo.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.AccountType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class AccountDto {

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("account_type")
    private AccountType accountType;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("blocked")
    private Boolean blocked;
}
