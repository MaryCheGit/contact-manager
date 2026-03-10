package com.example.contact_manager;

import com.example.contact_manager.config.AppConfig;
import com.example.contact_manager.dao.ContactDao;
import com.example.contact_manager.model.Contact;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactDaoTests {
    
    @Autowired
    private ContactDao contactDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @BeforeEach
    public void cleanDatabase() {
      
        jdbcTemplate.execute("TRUNCATE TABLE contacts RESTART IDENTITY CASCADE");
    }
    
    @Test
    @Order(1)
    public void testSaveContact() {
        Contact contact = new Contact("Иван", "Иванов", "+7-999-123-45-67", "ivan@email.com");
        Contact saved = contactDao.save(contact);
        
        assertNotNull(saved.getId(), "ID не должен быть null");
        assertEquals("Иван", saved.getFirstName());
        assertEquals("Иванов", saved.getLastName());
        assertEquals("+7-999-123-45-67", saved.getPhoneNumber());
        assertEquals("ivan@email.com", saved.getEmail());
        
        System.out.println("Контакт сохранен с ID: " + saved.getId());
    }
    
    @Test
    @Order(2)
    public void testFindById() {
      
        Contact contact = new Contact("Петр", "Петров", "+7-999-765-43-21", "petr@email.com");
        Contact saved = contactDao.save(contact);
        
     
        Contact found = contactDao.findById(saved.getId());
        
        assertNotNull(found, "Найденный контакт не должен быть null");
        assertEquals(saved.getId(), found.getId());
        assertEquals("Петр", found.getFirstName());
        assertEquals("Петров", found.getLastName());
        
        System.out.println("Контакт найден: " + found.getFirstName() + " " + found.getLastName());
    }
    
    @Test
    @Order(3)
    public void testFindAll() {
       
        contactDao.save(new Contact("Иван", "Иванов", "+7-999-111-11-11", "ivan1@email.com"));
        contactDao.save(new Contact("Петр", "Петров", "+7-999-222-22-22", "petr@email.com"));
        contactDao.save(new Contact("Сергей", "Сергеев", "+7-999-333-33-33", "sergey@email.com"));
        
        List<Contact> contacts = contactDao.findAll();
        assertNotNull(contacts);
        assertEquals(3, contacts.size(), "Должно быть 3 контакта");
        
        System.out.println("Найдено контактов: " + contacts.size());
    }
    
    @Test
    @Order(4)
    public void testUpdatePhoneNumber() {
      
        Contact contact = new Contact("Сергей", "Сергеев", "+7-999-111-22-33", "sergey@email.com");
        Contact saved = contactDao.save(contact);
        
   
        String newPhone = "+7-999-999-99-99";
        boolean updated = contactDao.updatePhoneNumber(saved.getId(), newPhone);
        
        assertTrue(updated, "Обновление должно быть успешным");
        
     
        Contact updatedContact = contactDao.findById(saved.getId());
        assertNotNull(updatedContact);
        assertEquals(newPhone, updatedContact.getPhoneNumber());
        
        System.out.println("Телефон обновлен для контакта ID: " + saved.getId());
    }
    
    @Test
    @Order(5)
    public void testUpdateEmail() {
      
        Contact contact = new Contact("Анна", "Иванова", "+7-999-222-33-44", "anna@email.com");
        Contact saved = contactDao.save(contact);
        
     
        String newEmail = "anna.new@email.com";
        boolean updated = contactDao.updateEmail(saved.getId(), newEmail);
        
        assertTrue(updated, "Обновление должно быть успешным");
        
      
        Contact updatedContact = contactDao.findById(saved.getId());
        assertNotNull(updatedContact);
        assertEquals(newEmail, updatedContact.getEmail());
        
        System.out.println("Email обновлен для контакта ID: " + saved.getId());
    }
    
    @Test
    @Order(6)
    public void testDeleteById() {
       
        Contact contact = new Contact("Мария", "Петрова", "+7-999-333-44-55", "maria@email.com");
        Contact saved = contactDao.save(contact);
        
      
        boolean deleted = contactDao.deleteById(saved.getId());
        assertTrue(deleted, "Удаление должно быть успешным");
        
      
        Contact shouldBeNull = contactDao.findById(saved.getId());
        assertNull(shouldBeNull, "Контакт должен быть null после удаления");
        
        System.out.println("Контакт с ID " + saved.getId() + " удален");
    }
    
    @Test
    @Order(7)
    public void testFindByIdNotFound() {
        Contact notFound = contactDao.findById(999L);
        assertNull(notFound, "Поиск несуществующего ID должен вернуть null");
        System.out.println("Поиск несуществующего контакта работает корректно");
    }
}