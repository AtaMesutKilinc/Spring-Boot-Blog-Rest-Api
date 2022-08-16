package com.works.services;


import com.works.configs.Configs;
import com.works.entities.Reader;
import com.works.entities.Role;
import com.works.repositories.ReaderRepository;
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
public class ReaderService {

    final ReaderRepository readerRepository;
    final Configs configs;
    final CacheManager cacheManager;
    final HttpSession httpSession;

    public ReaderService(ReaderRepository readerRepository, Configs configs, CacheManager cacheManager, HttpSession httpSession) {
        this.readerRepository = readerRepository;
        this.configs = configs;
        this.cacheManager = cacheManager;
        this.httpSession = httpSession;
    }

    public ResponseEntity registerReader(Reader reader){
        Optional<Reader> optionalReader=readerRepository.findByEmailEqualsIgnoreCase(reader.getEmail());
        Map<REnum,Object> hm= new LinkedHashMap<>();
        if (!optionalReader.isPresent()){
            reader.setPassword(configs.encoder().encode(reader.getPassword()));
            Role role = new Role();
            role.setId(2L);
            role.setName("reader");
            reader.setRole(role);
            reader.setTokenExpired(true);
            reader.setEnabled(true);
            Reader read=readerRepository.save(reader);
            hm.put(REnum.status,true);
            hm.put(REnum.message,"Register Success");
            hm.put(REnum.result,read);
            return new ResponseEntity(hm, HttpStatus.OK);
        }else{
            hm.put(REnum.status,false);
            hm.put(REnum.message,"This email "+reader.getEmail() +" has been received");
            hm.put(REnum.result,reader);
            return new ResponseEntity(hm, HttpStatus.OK);
        }
    }

    public ResponseEntity<Map<REnum,Object>> save(Reader reader){
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        reader.setPassword(configs.encoder().encode(reader.getPassword()));
        Reader cus= readerRepository.save(reader);
        cacheManager.getCache("readerList").clear();
        hashMap.put(REnum.status,true);
        hashMap.put(REnum.result,reader);
        return new ResponseEntity<>(hashMap, HttpStatus.OK);

    }

    public ResponseEntity<Map<REnum,Object>> update(Reader reader){
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        try {
            Optional<Reader> optionalReader= readerRepository.findById(reader.getId());
            if (optionalReader.isPresent()){
                readerRepository.saveAndFlush(reader);
                cacheManager.getCache("readerList").clear();
                hashMap.put(REnum.status,true);
                hashMap.put(REnum.result, reader);
                return new  ResponseEntity(hashMap, HttpStatus.OK);
            }else {
                hashMap.put(REnum.status,false);
                hashMap.put(REnum.message,"Customer is null! try again");
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
            readerRepository.deleteById(id);
            cacheManager.getCache("readerList").clear();
            hashMap.put(REnum.status,true);
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
        hashMap.put(REnum.result,readerRepository.findAll());
        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    public ResponseEntity<Map<REnum,Object>> changePasswordReader(String oldPwd,String pwd, String confirmPwd){
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        try {
            Reader reader= (Reader) httpSession.getAttribute("reader");
            boolean result = configs.encoder().matches(oldPwd, reader.getPassword());
            if (result){
                if (pwd.equals(confirmPwd)){
                    System.out.println("/********************");
                    String newPassword=configs.encoder().encode(pwd);
                    reader.setPassword(newPassword);
                    readerRepository.saveAndFlush(reader);
                    hashMap.put(REnum.status,true);
                    hashMap.put(REnum.message,"Change password success");
                    hashMap.put(REnum.result, reader);
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
