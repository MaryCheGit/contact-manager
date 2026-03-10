package com.example.contact_manager.service;

import com.example.contact_manager.dao.ContactDao;
import com.example.contact_manager.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {
    
    private final ContactDao contactDao;
    
    @Autowired
    public ContactServiceImpl(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Override
    public int loadContactsFromCsv(String filePath) throws IOException {
        List<Contact> batch = new ArrayList<>();
        int batchSize = 1000; 
        int totalCount = 0;
        int lineNumber = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (lineNumber == 1 && line.toLowerCase().contains("имя") && 
                    line.toLowerCase().contains("телефон")) {
                    continue;
                }

                Contact contact = parseCsvLine(line);
                if (contact != null) {
                    batch.add(contact);
                    totalCount++;
                    
                    if (batch.size() >= batchSize) {
                        contactDao.saveAll(batch);
                        batch.clear();
                        System.out.println("Загружено " + totalCount + " контактов...");
                    }
                } else {
                    System.err.println("Ошибка парсинга строки " + lineNumber + ": " + line);
                }
            }
            
            if (!batch.isEmpty()) {
                contactDao.saveAll(batch);
                System.out.println("Загружено " + totalCount + " контактов (финал)");
            }
        }
        
        return totalCount;
    }
    
    @Override
    public List<Contact> getAllContacts() {
        return contactDao.findAll();
    }

    private Contact parseCsvLine(String line) {
        try {
            String[] parts = line.split(",");
            
            if (parts.length < 3) {
                return null;
            }
            
            String fullName = parts[0].trim().replace("\"", "");
            String phone = parts[1].trim().replace("\"", "");
            String email = parts[2].trim().replace("\"", "");
            
            String firstName;
            String lastName;
            
            String[] nameParts = fullName.split(" ");
            if (nameParts.length >= 2) {
                firstName = nameParts[0];
                lastName = String.join(" ", java.util.Arrays.copyOfRange(nameParts, 1, nameParts.length));
            } else {
                firstName = fullName;
                lastName = "";
            }
            
            return new Contact(firstName, lastName, phone, email);
            
        } catch (Exception e) {
            System.err.println("Ошибка парсинга строки: " + line);
            e.printStackTrace();
            return null;
        }
    }
}
