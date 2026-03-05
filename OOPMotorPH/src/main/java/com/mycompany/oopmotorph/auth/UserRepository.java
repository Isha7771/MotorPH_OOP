/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.auth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll() throws IOException;

    Optional<User> findByUsername(String username) throws IOException;

    void append(User user) throws IOException;

    void saveAll(List<User> users) throws IOException;
}