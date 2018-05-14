package com.learncamel.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.math.BigDecimal;

@ToString
@Setter
@Getter
@CsvRecord(separator = ",", skipFirstLine = true)
public class Item {

    @DataField(pos = 1)
    private String TransactionType;

    @DataField(pos = 2)
    private String sku;

    @DataField(pos = 3)
    private String itemDescription;

    @DataField(pos = 4, precision = 2)
    private BigDecimal price;
}
