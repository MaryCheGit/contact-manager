package com.example.contact_manager.dao;

import com.example.contact_manager.model.Contact;
import java.util.List;

public interface ContactDao {
    List<Contact> findAll();
    Contact findById(Long id);
    Contact save(Contact contact);
    boolean updatePhoneNumber(Long id, String phoneNumber);
    boolean updateEmail(Long id, String email);
    boolean deleteById(Long id);
}
