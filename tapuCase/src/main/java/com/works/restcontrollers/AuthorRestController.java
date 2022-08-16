package com.works.restcontrollers;

import com.works.entities.Author;
import com.works.services.AuthorService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/author")
public class AuthorRestController {

    final AuthorService authorService;

    public AuthorRestController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody Author author){
        return authorService.save(author);
    }

    @Cacheable("authorList")
    @GetMapping("/list")
    public ResponseEntity list(){
        return authorService.list();
    }

    @PutMapping("/update")
    public ResponseEntity update(@Valid @RequestBody Author author){
        return authorService.update(author);
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestParam Long id){
        return  authorService.delete(id);
    }

    @PostMapping("/changePasswordAuthor")
    public ResponseEntity changePassword(@RequestParam String oldPwd,@RequestParam  String newPwd,
                                         @RequestParam String newPwdConf){
        return authorService.changePassword(oldPwd,newPwd,newPwdConf);
    }
}
