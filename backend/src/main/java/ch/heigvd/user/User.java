package ch.heigvd.user;

import java.time.LocalDate;

public record User(String username, String lname, String fname, LocalDate birthdate, String email) {
};
