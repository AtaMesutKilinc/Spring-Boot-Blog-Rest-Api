package com.works.restcontrollers;

import com.works.entities.Author;
import com.works.entities.Login;
import com.works.entities.Reader;
import com.works.services.AuthorService;
import com.works.services.ReaderService;
import com.works.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class LoginRestController {

    final LoginService loginService;
    final ReaderService readerService;
    final AuthorService authorService;

    public LoginRestController(LoginService loginService, ReaderService readerService, AuthorService authorService) {
        this.loginService = loginService;
        this.readerService = readerService;
        this.authorService = authorService;
    }


    @PostMapping("/login")
    public ResponseEntity login (@Valid @RequestBody Login login){
        return  loginService.login(login);
    }


    @PostMapping("/register") //save
    public ResponseEntity register(@Valid @RequestBody Reader reader){
        return readerService.registerReader(reader);
    }
    @PostMapping("/registerAuthor")
    public ResponseEntity register(@Valid @RequestBody Author author){
        return authorService.registerAuthor(author);
    }




}
