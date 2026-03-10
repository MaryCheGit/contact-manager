package com.example.contact_manager.service;

import com.example.contact_manager.model.Contact;
import java.io.IOException;
import java.util.List;

public interface ContactService {

    int loadContactsFromCsv(String filePath) throws IOException;
    
    List<Contact> getAllContacts();
}