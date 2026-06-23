package com.tesfayedev.InventoryMgtSystem.services;

import com.tesfayedev.InventoryMgtSystem.dtos.LoginRequest;
import com.tesfayedev.InventoryMgtSystem.dtos.RegisterRequest;
import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.dtos.UserDTO;
import com.tesfayedev.InventoryMgtSystem.models.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getCurrentLoggedInUser();
    Response getUserById(Long id);
    Response updateUser(Long id, UserDTO userDTO);
    Response deleteUser(Long id);
    Response getUserTransactions(Long id);
}
