package com.example.Projekat.model;

public enum Role
{
    ADMIN,
    MANAGER,
    GUEST;

    public static Role fromString(final String role)
    {
        if (role == null)
        {
            return null;
        }

        if (role.startsWith("ROLE_"))
        {
            return Role.valueOf(role.substring(5));
        }

        return Role.valueOf(role.toUpperCase());
    }
}
