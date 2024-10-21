package com.rmhy.userservice.dto.response;

import com.rmhy.userservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private ZonedDateTime createdDate;
    private ZonedDateTime updatedDate;
}
