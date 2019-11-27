package com.ita.if103java.ims;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImsApplication implements CommandLineRunner {
@Autowired
    ItemDao itemDao;
    public static void main(String[] args) {
        SpringApplication.run(ImsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println(itemDao.findSavedItemByItemId(itemDao.getItems().get(1).getId()).getId());
//        Account account = new Account();
//        account.setId(1L);
//        itemDao.addItem("juice","block","delisious juice", 6, account, true);
//
    }
}
