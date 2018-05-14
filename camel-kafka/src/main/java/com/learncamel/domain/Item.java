package com.learncamel.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Item {

    private String transactionType;
    private String sku;
    private String itemDescription;
    private BigDecimal price;

}

