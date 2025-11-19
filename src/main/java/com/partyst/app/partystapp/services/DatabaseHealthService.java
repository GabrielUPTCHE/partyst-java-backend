package com.partyst.app.partystapp.services;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseHealthService {

    @Autowired
    private DataSource dataSource;

    public boolean isDatabaseUp() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2); 
        } catch (Exception e) {
            return false;
        }
    }
}
