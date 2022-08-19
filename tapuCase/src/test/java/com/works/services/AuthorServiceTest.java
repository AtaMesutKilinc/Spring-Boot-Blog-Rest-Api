package com.works.services;

import com.works.configs.Configs;
import com.works.configs.exceptionhandle.NotUniqueDataException;
import com.works.entities.Author;
import com.works.entities.Role;
import com.works.repositories.AuthorRepository;

import com.works.utils.REnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @InjectMocks
    private  AuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Configs configs;
    @Mock
    private HttpSession httpSession;

    @Spy
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Author exAuthor;

    List<Role> basicRoles;

    final static Long ROLE_author = 0L;
    final static Long ROLE_reader = 1L;


    @BeforeEach
    void setUp() {
        basicRoles = new ArrayList<>();
        basicRoles.add(new Role(0L,"ROLE_author"));
        basicRoles.add(new Role(1L,"ROLE_reader"));
        exAuthor = new Author(0L,
                "Test-name",
                "Test-surname",
                "+90 555 666 77 88",
                "Test",
                "test@test.com",
                "Test.001",
                true,
                true,
                basicRoles.get(0)
                );
        exAuthor.setId(1L);




        basicRoles.get(0).setId(1L);
        basicRoles.get(1).setId(2L);

    }

    private Author getAuthorWithChanges(Author author, UnaryOperator<Author> operator) {
        return operator.apply(author);
    }


    @Test
    void testIfEmailIsAlreadyTaken() {
        when(authorRepository.findByEmailEqualsIgnoreCase(exAuthor.getEmail())).thenReturn(Optional.of(exAuthor));

        Author author = new Author();
        author.setEmail("test@test.com");

        Assertions.assertThrows(Exception.class, () -> authorRepository.save(author));
    }


    @Test
    void testIfNickNameIsAlreadyTaken() {
        when(authorRepository.findByEmailEqualsIgnoreCase(exAuthor.getEmail())).thenReturn(Optional.of(exAuthor));
//        when(authorRepository.existsByNickName(Mockito.anyString())).thenReturn(true);

        Author author = new Author();
        author.setEmail("email@em.ail");
        author.setName("test0");

        Assertions.assertThrows(NotUniqueDataException.class, () -> authorRepository.save(author));
    }
    @Test
    void testIfUserDoesNotHaveRoles() {
        when(authorRepository.findByEmailEqualsIgnoreCase(exAuthor.getEmail())).thenReturn(Optional.of(exAuthor));

        exAuthor.setId(null);

//        when(roleRepository.findByRoleNameIn(List.of("ROLE_USER"))).thenReturn(List.of(basicRoles.get(ROLE_USER_INDEX)));
        when(authorRepository.save(exAuthor)).thenReturn(getAuthorWithChanges(exAuthor, u -> {
            u.setId(1L);
            return u;
        }));

        Author actualAuthor = authorRepository.save(exAuthor);
        assertEquals(1, actualAuthor.getRole());
        assertEquals(basicRoles.get(Math.toIntExact(ROLE_author)).getName(), actualAuthor.getRole().getName());
    }
    @Test
    void testWhenUserHasRole() {
        when(authorRepository.findByEmailEqualsIgnoreCase(exAuthor.getEmail())).thenReturn(Optional.of(exAuthor));

        List<Role> roles = new ArrayList<>(basicRoles);
        roles.remove(ROLE_reader);
        exAuthor.setRole((Role) roles);


        exAuthor.setId(null);

        when(authorRepository.save(exAuthor)).thenReturn(getAuthorWithChanges(exAuthor, u -> {
            u.setId(1L);
            return u;
        }));

        ResponseEntity<Map<REnum, Object>> actualAuthor = authorService.save(exAuthor);
        assertEquals(2, actualAuthor.getBody().keySet().size());
    }

    @Test
    void testWhenUserDoesNotHaveStatus() {
        when(authorRepository.findByEmailEqualsIgnoreCase(exAuthor.getEmail())).thenReturn(Optional.of(exAuthor));

        exAuthor.setId(null);



        when(authorRepository.save(exAuthor)).thenReturn(getAuthorWithChanges(exAuthor, u -> {
            u.setId(1L);
            return u;
        }));

        ResponseEntity<Map<REnum, Object>> actualAuthor = authorService.save(exAuthor);
        assertEquals(exAuthor.isEnabled(), actualAuthor.getBody().get("enabled"));

    }
//
//    @Test
//    void testGetByIdWhenUserDoesNotExist() {
//        when(authorRepository.findById(Mockito.anyLong())).thenReturn(null);
//
//        Assertions.assertThrows(CustomEntityNotFoundException.class, () -> authorService.getById(1L));
//    }
//
//
//
//    @Test
//    void testUpdateWhenIdIsNotFound() {
//        when(authorRepository.findById(Mockito.anyLong())).thenReturn(null);
//
//        Assertions.assertThrows(CustomEntityNotFoundException.class, () -> userServiceImpl.update(exAuthor));
//    }
//
//    @Test
//    void testUpdateWhenEmailIsNotUnique() {
//        when(authorRepository.findById(Mockito.anyLong())).thenReturn(null);
//
//        User oldUser = new User(basicUser);
//        oldUser.setEmail("oldEmail@em.ail");
//
//        when(userRepository.getById(Mockito.anyLong())).thenReturn(oldUser);
//        when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
//
//        Assertions.assertThrows(NotUniqueDataException.class, () -> userServiceImpl.update(exAuthor));
//    }
//
//    @Test
//    void testUpdateWhenNickNameIsNotUnique() {
//        when(authorRepository.findById(Mockito.anyLong())).thenReturn(null);
//
//        User oldUser = new User(basicUser);
//        oldUser.setNickName("oldNick");
//
//        when(userRepository.getById(Mockito.anyLong())).thenReturn(oldUser);
//        when(userRepository.existsByNickName(Mockito.anyString())).thenReturn(true);
//
//        Assertions.assertThrows(NotUniqueDataException.class, () -> userServiceImpl.update(basicUser));
//    }
//
//    @Test
//    void testDeleteWhenUserDoesNotExist(){
//        when(authorRepository.findById(Mockito.anyLong())).thenReturn(null);
//
//        Assertions.assertThrows(CustomEntityNotFoundException.class, () -> userServiceImpl.update(basicUser));
//    }


//    @Test
//    public void testRegister(){
//        Author author= new Author();
//        author.setName("Test-name");
//        author.setSurname("Test-surname");
//        author.setPhone("+90 555 666 77 88");
//        author.setPublisher("Test");
//        author.setEmail("test@test.com");
//        author.setPassword("Test.001");
//
//        Author authorMock= Mockito.mock(Author.class);
//
//
//        when(authorRepository.save(ArgumentMatchers.any(Author.class))).thenReturn(authorMock);
//
//        Author result= authorRepository.save(author);
//
//        assertEquals(result.getName(),author.getName());
//
//
//    }
//    void setUp() {
//        authorRepository= Mockito.mock(AuthorRepository.class);
//        cacheManager= Mockito.mock(CacheManager.class);
//        configs= Mockito.mock(Configs.class);
//        httpSession= Mockito.mock(HttpSession.class);
//
//        authorService=new AuthorService(authorRepository,cacheManager,configs,httpSession);
//    }
//
//    @Test
//    public void testRegisterAuthor(){
//        Author author= new Author();
//        author.setName("Test-name");
//        author.setSurname("Test-surname");
//        author.setPhone("+90 555 666 77 88");
//        author.setPublisher("Test");
//        author.setEmail("test@test.com");
//        author.setPassword("Test.001");
//
//        Mockito.when(authorRepository.findByEmailEqualsIgnoreCase("test@test.com")).thenReturn(Optional.of(author));
//        Mockito.when( authorRepository.save(author)).thenReturn(author);
//
//    }



}