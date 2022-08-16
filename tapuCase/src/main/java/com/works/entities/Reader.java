package com.works.entities;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Data
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name can't be blank")
    @Length(message = "Name must be between 3 and 50 characters", min = 3, max = 50)
    private String name;
    @NotBlank(message = "Surname can't be blank")
    @Length(message = "Surname must be between 3 and 50 characters", min = 3, max = 50)
    private String surname;
    @NotBlank(message = "Phone can't be blank")

    // @Pattern(message = "Please enter a valid phone number",regexp = "/(\\+\\d{1,3}\\s?)?((\\(\\d{3}\\)\\s?)|(\\d{3})(\\s|-?))(\\d{3}(\\s|-?))(\\d{4})(\\s?(([E|e]xt[:|.|]?)|x|X)(\\s?\\d+))?/g")
    private String phone;

    @NotNull(message = "Email not null")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "password can not be blank")
    @Pattern(message = "Password must contain min one upper,lower letter and 0-9 digit number ",
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9\\s]).{6,}")
    private String password;

    private boolean enabled;

    private boolean tokenExpired;

    @ManyToOne
    //@JsonIgnore
    @JoinColumn(name = "role_Id",referencedColumnName = "id")
    private Role role;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

}
