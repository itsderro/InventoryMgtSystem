package com.tesfayedev.InventoryMgtSystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tesfayedev.InventoryMgtSystem.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryDTO {

    private Long id;

    private String name;

    @JsonIgnore
    private String password;

    private String email;

    private String phoneNumber;

    private UserRole role;

    private LocalDateTime createdAt;

}
