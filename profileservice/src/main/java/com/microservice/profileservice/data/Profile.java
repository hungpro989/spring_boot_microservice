package com.microservice.profileservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Profile {
    @Id
    private long id;
    private String email;
    private String name;
    private String status;
    private String role;
}
