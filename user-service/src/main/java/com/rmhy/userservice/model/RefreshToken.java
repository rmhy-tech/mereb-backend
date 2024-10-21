package com.rmhy.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch to optimize performance
    @JoinColumn(name = "user_id", nullable = false) // Foreign key constraint
    private User user;

    @Column(nullable = false)
    private Date expiryDate;

    private boolean revoked = false;

    public RefreshToken(String token, Date expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.revoked = false;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                ", revoked=" + revoked +
                '}';
    }
}
