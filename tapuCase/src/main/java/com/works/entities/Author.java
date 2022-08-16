package com.works.entities;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name can not be blank")
    @Length(message = "name must contain min 2 max  5O character.", min = 2, max = 50)
    private String name;

    @NotBlank(message = "surname can not be blank")
    @Length(message = "surname must contain min 2 max  5O character.", min = 2, max = 50)
    private String surname;

    // @Pattern(message = "Please enter a valid phone number",regexp = "/(\\+\\d{1,3}\\s?)?((\\(\\d{3}\\)\\s?)|(\\d{3})(\\s|-?))(\\d{3}(\\s|-?))(\\d{4})(\\s?(([E|e]xt[:|.|]?)|x|X)(\\s?\\d+))?/g")
    private String phone;

    @NotBlank(message = "publisher name can not be blank")
    @Length(message = "publisher name must contain min 2 max  50 character.", min = 2, max = 50)
    private String publisher;

    //    @Column(unique = true)
    @Length(message = "Maximum 60", max = 60)
    @NotBlank(message = "Email can not be blank")
    @Email(message = "Email Format Error")
    private String email;

    //    @Length(message = "Maximum 5 min 10",min = 5, max = 10)
    @NotBlank(message = "password can not be blank")
  //  @Pattern(message = "Password must contain min one upper,lower letter and 0-9 digit number ",
         //   regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9\\s]).{6,}")
    private String password;

    private boolean enabled;
    private boolean tokenExpired;


    @ManyToOne
//    @JsonIgnore
    @JoinColumn(name = "role_Id", referencedColumnName = "id")
    private Role role;

//    @ManyToOne
////    @JsonIgnore
//    @JoinColumn(name = "role_Id", referencedColumnName = "id")
//    private Writing writing;

//    @Column(name = "reset_password_token")
//    private String resetPasswordToken;
}
