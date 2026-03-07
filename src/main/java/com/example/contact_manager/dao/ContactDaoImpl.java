package com.example.contact_manager.dao;

import com.example.contact_manager.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ContactDaoImpl implements ContactDao {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public ContactDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private static final class ContactRowMapper implements RowMapper<Contact> {
        @Override
        public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
            Contact contact = new Contact();
            contact.setId(rs.getLong("id"));
            contact.setFirstName(rs.getString("first_name"));
            contact.setLastName(rs.getString("last_name"));
            contact.setPhoneNumber(rs.getString("phone_number"));
            contact.setEmail(rs.getString("email"));
            return contact;
        }
    }
    
    @Override
    public List<Contact> findAll() {
        String sql = "SELECT * FROM contacts ORDER BY id";
        return jdbcTemplate.query(sql, new ContactRowMapper());
    }
    
    @Override
    public Contact findById(Long id) {
        String sql = "SELECT * FROM contacts WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ContactRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Contact save(Contact contact) {
        String sql = "INSERT INTO contacts (first_name, last_name, phone_number, email) VALUES (?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getPhoneNumber());
            ps.setString(4, contact.getEmail());
            return ps;
        }, keyHolder);
        
        Number key = keyHolder.getKey();
        if (key != null) {
            contact.setId(key.longValue());
        }
        
        return contact;
    }
    
    @Override
    public boolean updatePhoneNumber(Long id, String phoneNumber) {
        String sql = "UPDATE contacts SET phone_number = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, phoneNumber, id);
        return rowsAffected > 0;
    }
    
    @Override
    public boolean updateEmail(Long id, String email) {
        String sql = "UPDATE contacts SET email = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, email, id);
        return rowsAffected > 0;
    }
    
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM contacts WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }
}