package com.simonesestito.shopsqueue.model.dto.input;

import androidx.annotation.Nullable;

import com.simonesestito.shopsqueue.model.UserRole;

public class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String role;
    @Nullable private Integer shopId;

    public User(int id, String name, String surname, String email, String role, @Nullable Integer shopId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.shopId = shopId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return UserRole.valueOf(role);
    }

    @Nullable
    public Integer getShopId() {
        return shopId;
    }
}
