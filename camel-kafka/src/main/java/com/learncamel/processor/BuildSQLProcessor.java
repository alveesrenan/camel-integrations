package com.learncamel.processor;

import com.learncamel.domain.Item;
import com.learncamel.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class BuildSQLProcessor implements org.apache.camel.Processor {

    private final String TABLE_ITEM = "ITEMS";

    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = (Item) exchange.getIn().getBody();
        log.info("Item processor is : {}", item);

        StringBuilder query = new StringBuilder();

        if(item.getTransactionType().equalsIgnoreCase("ADD")){
            query.append("INSERT INTO "+ TABLE_ITEM +" (SKU,ITEM_DESCRIPTION,PRICE) VALUES ('");
            query.append(item.getSku() + "','");
            query.append(item.getItemDescription() + "',");
            query.append(item.getPrice() + ")");
        }else if(item.getTransactionType().equalsIgnoreCase( "UPDATE")){

            query.append("UPDATE "+ TABLE_ITEM +" SET PRICE =");
            query.append(item.getPrice() + " WHERE SKU = '" + item.getSku() + "'");

        }else if(item.getTransactionType().equalsIgnoreCase("DELETE")){
            query.append("DELETE FROM "+ TABLE_ITEM +" WHERE SKU ='");
            query.append(item.getSku() + "'");
        }

        log.info("Final query is: {}", query.toString());

        exchange.getIn().setBody(query.toString());
    }
}
