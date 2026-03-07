package com.example.contact_manager;

import com.example.contact_manager.config.AppConfig;
import com.example.contact_manager.dao.ContactDao;
import com.example.contact_manager.model.Contact;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.List;

public class App {
    public static void main(String[] args) {
      
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
           
            ContactDao contactDao = context.getBean(ContactDao.class);
            
           
            List<Contact> contacts = contactDao.findAll();
            
            System.out.println("\n=== Все контакты в базе данных ===");
            if (contacts.isEmpty()) {
                System.out.println("Контактов пока нет. Запустите тесты, чтобы добавить данные!");
            } else {
                for (Contact contact : contacts) {
                    System.out.println("ID: " + contact.getId());
                    System.out.println("Имя: " + contact.getFirstName() + " " + contact.getLastName());
                    System.out.println("Телефон: " + contact.getPhoneNumber());
                    System.out.println("Email: " + contact.getEmail());
                    System.out.println("------------------------");
                }
            }
        }
    }
}