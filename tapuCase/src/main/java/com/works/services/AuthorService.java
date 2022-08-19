package com.works.services;


import com.works.configs.Configs;
import com.works.entities.Author;
import com.works.entities.Reader;
import com.works.entities.Role;
import com.works.repositories.AuthorRepository;
import com.works.utils.REnum;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthorService {

    final AuthorRepository authorRepository;
    final CacheManager cacheManager;
    final Configs configs;
    final HttpSession httpSession;

    public AuthorService(AuthorRepository authorRepository, CacheManager cacheManager, Configs configs, HttpSession httpSession) {
        this.authorRepository = authorRepository;
        this.cacheManager = cacheManager;
        this.configs = configs;
        this.httpSession = httpSession;
    }
    public ResponseEntity registerAuthor(Author author){
        Optional<Author> optionalAuthor=authorRepository.findByEmailEqualsIgnoreCase(author.getEmail());
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        if (!optionalAuthor.isPresent()){
            author.setPassword(configs.encoder().encode(author.getPassword()));
            Role role = new Role();
            role.setId(1L);
            role.setName("author");
            author.setRole(role);
            author.setTokenExpired(true);
            author.setEnabled(true);
            Author adm=authorRepository.save(author);
            hashMap.put(REnum.status,true);
            hashMap.put(REnum.result,adm);
            return new ResponseEntity(hashMap, HttpStatus.OK);
        }else{
            hashMap.put(REnum.status,false);
            hashMap.put(REnum.message,"This email "+author.getEmail() +" has been received");
            hashMap.put(REnum.result,author);
            return new ResponseEntity(hashMap, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public ResponseEntity<Map<REnum,Object>> save(Author author){
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        Author adm= authorRepository.save(author);
        cacheManager.getCache("authorList").clear();
        hashMap.put(REnum.status,true);
        hashMap.put(REnum.result,adm);
        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    public ResponseEntity<Map<REnum,Object>> update(Author author){
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        try {
            Optional<Author> optionalAdmin= authorRepository.findById(author.getId());
            if (optionalAdmin.isPresent()){
                authorRepository.saveAndFlush(author);
                cacheManager.getCache("authorList").clear();
                hashMap.put(REnum.status,true);
                hashMap.put(REnum.result, author);
                return new  ResponseEntity(hashMap, HttpStatus.OK);
            }else {
                hashMap.put(REnum.status,false);
                hashMap.put(REnum.message,"Author is null! try again");
                return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex){
            hashMap.put(REnum.status,false);
            hashMap.put(REnum.message,ex.getMessage());
            return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Map<REnum,Object>> delete(Long id){
        Map<REnum,Object> hashMap =new LinkedHashMap<>();
        try {
            authorRepository.deleteById(id);
            cacheManager.getCache("authorList").clear();
            hashMap.put(REnum.status,true);
            hashMap.put(REnum.message,"Author delete is success. ");
            return new ResponseEntity<>(hashMap, HttpStatus.OK);
        }catch (Exception ex){
            hashMap.put(REnum.status,false);
            hashMap.put(REnum.message,ex.getMessage());
            return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Map<REnum,Object>> list(){
        Map<REnum,Object> hashMap =new LinkedHashMap<>();
        hashMap.put(REnum.status,true);
        hashMap.put(REnum.result,authorRepository.findAll());
        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    public ResponseEntity<Map<REnum,Object>> changePassword(String oldPwd,String pwd, String confirmPwd){
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        try {
            Author author= (Author) httpSession.getAttribute("author");
            boolean result = configs.encoder().matches(oldPwd, author.getPassword());
            if (result){
                if (pwd.equals(confirmPwd)){
                    System.out.println("/********************");
                    String newPassword=configs.encoder().encode(pwd);
                    author.setPassword(newPassword);
                    authorRepository.saveAndFlush(author);
                    hashMap.put(REnum.status,true);
                    hashMap.put(REnum.message,"Change password success");
                    hashMap.put(REnum.result, author);
                    return new  ResponseEntity(hashMap, HttpStatus.OK);
                }
                hashMap.put(REnum.status,false);
                hashMap.put(REnum.message,"new password is not equals new password confirm");
                return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
            }else {
                hashMap.put(REnum.status,false);
                hashMap.put(REnum.message,"Old password is false");
                return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex){
            hashMap.put(REnum.status,false);
            hashMap.put(REnum.message,ex.getMessage());
            return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
        }

    }


}
