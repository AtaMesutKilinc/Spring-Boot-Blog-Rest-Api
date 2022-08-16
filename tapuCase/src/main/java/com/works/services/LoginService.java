package com.works.services;

import com.works.configs.JwtUtil;

import com.works.entities.Author;
import com.works.entities.Login;
import com.works.entities.Reader;
import com.works.entities.Role;

import com.works.repositories.AuthorRepository;
import com.works.repositories.ReaderRepository;
import com.works.utils.REnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
@Transactional
public class LoginService implements UserDetailsService {

    final AuthorRepository authorRepository;
    final ReaderRepository readerRepository;
    final AuthenticationManager authenticationManager; //spring securitye haber vermek için ara sınıf kullanolacak
    final JwtUtil jwtUtil;
    final HttpSession httpSession;

    //@Lazy yorgun yükleme manasındadır. Bu ifadeye göre içiçe çağrılmış injecte nesnelerinin circle a girmesini engeller.Sonsuz döngüye girmesini engeller.
    public LoginService(AuthorRepository authorRepository, ReaderRepository readerRepository, @Lazy AuthenticationManager authenticationManager, JwtUtil jwtUtil, HttpSession httpSession) {
        this.authorRepository = authorRepository;
        this.readerRepository = readerRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.httpSession = httpSession;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Author> optionalAuthor=authorRepository.findByEmailEqualsIgnoreCase(username);
        Optional<Reader> optionalReader=readerRepository.findByEmailEqualsIgnoreCase(username);
        if (optionalAuthor.isPresent() && !optionalReader.isPresent()){
            Author author= optionalAuthor.get();
            UserDetails userDetails=new org.springframework.security.core.userdetails.User(
                    author.getEmail(),
                    author.getPassword(),
                    author.isEnabled(),
                    author.isTokenExpired(),
                    true,
                    true,
                    roles(author.getRole())
            );
            httpSession.setAttribute("author",author);
            return userDetails;
        }else if(optionalReader.isPresent()&& !optionalAuthor.isPresent()){
            Reader reader= optionalReader.get();
            UserDetails userDetails=new org.springframework.security.core.userdetails.User(
                    reader.getEmail(),
                    reader.getPassword(),
                    reader.isEnabled(),
                    reader.isTokenExpired(),
                    true,
                    true,
                    roles(reader.getRole())
            );
            httpSession.setAttribute("reader",reader);
            return userDetails;
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public Collection roles(Role role ) {
        List<GrantedAuthority> ls = new ArrayList<>();
        ls.add( new SimpleGrantedAuthority( role.getName()));
        return ls;
    }

    public  ResponseEntity login (Login login){
        Map<REnum,Object> hashMap = new LinkedHashMap<>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    login.getUsername(),login.getPassword()));
            UserDetails userDetails=loadUserByUsername(login.getUsername());
            String jwt= jwtUtil.generateToken(userDetails);
            //add session
            Reader customer = (Reader) httpSession.getAttribute("reader");
            Author admin = (Author) httpSession.getAttribute("author");

            if (admin==null && customer !=null){
                hashMap.put(REnum.status,true);
                hashMap.put(REnum.message,"Login successful");
                hashMap.put(REnum.jwt,jwt);
                hashMap.put(REnum.result,customer);
                return new ResponseEntity(hashMap,HttpStatus.OK);
            }else{
                hashMap.put(REnum.status,true);
                hashMap.put(REnum.message,"Login successful");
                hashMap.put(REnum.jwt,jwt);
                hashMap.put(REnum.result,admin);
                return new ResponseEntity(hashMap,HttpStatus.OK);
            }

        }catch (Exception ex){
            hashMap.put(REnum.status,false);
            hashMap.put(REnum.message,"Login failed");
            hashMap.put(REnum.error,ex.getMessage());
            return new ResponseEntity(hashMap,HttpStatus.OK);
        }

    }


}
