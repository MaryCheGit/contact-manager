package com.example.contact_manager;

import com.example.contact_manager.config.AppConfig;
import com.example.contact_manager.model.Contact;
import com.example.contact_manager.service.ContactService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactServiceTests {
    
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Path tempFile;
    
    @BeforeEach
    public void setUp() throws IOException {
        jdbcTemplate.execute("TRUNCATE TABLE contacts RESTART IDENTITY CASCADE");

        tempFile = Files.createTempFile("contacts", ".csv");
    }
    
    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }
    
    @Test
    @Order(1)
    public void testLoadContactsFromCsv() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            writer.write("Иван Иванов,+71234567890,iivanov@gmail.com\n");
            writer.write("Петр Петров,+79876543221,ppetrov@mail.ru\n");
            writer.write("Сидор Сидоров,+71112223344,ssidorov@yandex.ru\n");
        }
        
     
        int count = contactService.loadContactsFromCsv(tempFile.toString());
        
     
        assertEquals(3, count, "Загружено 3 контакта");
        
        List<Contact> contacts = contactService.getAllContacts();
        assertEquals(3, contacts.size(), "В базе 3 контакта");
    }
    
    @Test
    @Order(2)
    public void testLoadContactsFromCsvWithHeader() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            writer.write("Имя Фамилия,Телефон,Email\n");
            writer.write("Иван Иванов,+71234567890,iivanov@gmail.com\n");
            writer.write("Петр Петров,+79876543221,ppetrov@mail.ru\n");
        }

        int count = contactService.loadContactsFromCsv(tempFile.toString());
        
        assertEquals(2, count, "Загружено 2 контакта");
        
        List<Contact> contacts = contactService.getAllContacts();
        assertEquals(2, contacts.size());
        assertEquals("Иван", contacts.get(0).getFirstName());
        assertEquals("Иванов", contacts.get(0).getLastName());
    }
    
    @Test
    @Order(3)
    public void testLoadContactsFromCsvWithQuotes() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            writer.write("\"Иван Иванов\",\"+71234567890\",\"iivanov@gmail.com\"\n");
            writer.write("\"Петр Петров\",\"+79876543221\",\"ppetrov@mail.ru\"\n");
        }

        int count = contactService.loadContactsFromCsv(tempFile.toString());
        
        assertEquals(2, count, "Загружено 2 контакта");
        
        List<Contact> contacts = contactService.getAllContacts();
        Contact contact = contacts.get(0);
        assertEquals("Иван", contact.getFirstName());
        assertEquals("Иванов", contact.getLastName());
        assertEquals("+71234567890", contact.getPhoneNumber());
        assertEquals("iivanov@gmail.com", contact.getEmail());
    }
    
    @Test
    @Order(4)
    public void testLoadLargeBatch() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            for (int i = 0; i < 2500; i++) {
                writer.write(String.format("Иван%d Иванов,+7123456%04d,iivanov%d@gmail.com\n", i, i, i));
            }
        }

        int count = contactService.loadContactsFromCsv(tempFile.toString());
        
        assertEquals(2500, count, "Загружено 2500 контактов");
        
        List<Contact> contacts = contactService.getAllContacts();
        assertEquals(2500, contacts.size(), "В базе 2500 контактов");
    }
    
    @Test
    @Order(5)
    public void testGetAllContacts() throws IOException {
 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            writer.write("Иван Иванов,+71234567890,iivanov@gmail.com\n");
            writer.write("Петр Петров,+79876543221,ppetrov@mail.ru\n");
        }
        
        contactService.loadContactsFromCsv(tempFile.toString());
 
        List<Contact> contacts = contactService.getAllContacts();
        assertEquals(2, contacts.size());

        assertTrue(contacts.get(0).getId() < contacts.get(1).getId());
    }
}
