package com.works.services;

import com.works.entities.Author;
import com.works.entities.Writing;
import com.works.repositories.AuthorRepository;
import com.works.repositories.WritingRepository;
import com.works.utils.REnum;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WritingService {

    final WritingRepository writingRepository;
    final AuthorRepository authorRepository;
    final CacheManager cacheManager;
    final HttpSession httpSession;

    public WritingService(WritingRepository writingRepository, AuthorRepository authorRepository, CacheManager cacheManager, HttpSession httpSession) {
        this.writingRepository = writingRepository;
        this.authorRepository = authorRepository;
        this.cacheManager = cacheManager;
        this.httpSession = httpSession;
    }

    public ResponseEntity<Map<REnum,Object>> save(Writing writing){
        Map<REnum,Object> hashMap= new LinkedHashMap<>();
        Author author = (Author) httpSession.getAttribute("author");
        try {
            Optional<Author> optionalAuthor= authorRepository.findById(writing.getAuthor().getId());
            if (optionalAuthor.isPresent()){
                writing.setAuthor(author);
                writingRepository.save(writing);
                cacheManager.getCache("writingList").clear();
                cacheManager.getCache("mylist").clear();
                hashMap.put(REnum.status,true);
                hashMap.put(REnum.result,writing);
                return new ResponseEntity<>(hashMap, HttpStatus.OK);
            }else{
                hashMap.put(REnum.status, false);
                return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex){
            hashMap.put(REnum.status, false);
            hashMap.put(REnum.message, ex.getMessage());
            return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
        }


    }

    public ResponseEntity<Map<REnum ,Object>> update(Writing writing){
        Map<REnum,Object> hashMap = new LinkedHashMap<>();
        try{
            Optional<Writing> optionalWriting = writingRepository.findById(writing.getId());
            Author author = (Author) httpSession.getAttribute("author");

            if(optionalWriting.isPresent()){
                Writing write= optionalWriting.get();
                if (author.getId()==write.getAuthor().getId()){
                    writing.setAuthor(author);
                    writingRepository.saveAndFlush(writing);
                    cacheManager.getCache("writingList").clear();
                    cacheManager.getCache("mylist").clear();
                    hashMap.put(REnum.status, true);
                    hashMap.put(REnum.result, writing);
                    return new  ResponseEntity(hashMap, HttpStatus.OK);
                }else{
                    hashMap.put(REnum.status,false);
                    hashMap.put(REnum.message,"There is no such writing by the author.");
                    return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
                }

            }else{
                hashMap.put(REnum.status, false);
                hashMap.put(REnum.message, "No such writing found");
                return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            hashMap.put(REnum.status, false);
            hashMap.put(REnum.message, ex.getMessage());
            return new  ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<Map<REnum,Object>> delete(Long id){
        Map<REnum,Object> hashMap =new LinkedHashMap<>();
        try {
            Optional<Writing> optionalWriting= writingRepository.findById(id);
            Author author = (Author) httpSession.getAttribute("author");
            if (optionalWriting.isPresent()){
                Writing writing=optionalWriting.get();
                if (author.getId()==writing.getAuthor().getId()){
                    writingRepository.deleteById(id);
                    cacheManager.getCache("writingList").clear();
                    cacheManager.getCache("mylist").clear();
                    hashMap.put(REnum.status,true);
                    hashMap.put(REnum.message,"Writing delete is success");
                    return new ResponseEntity<>(hashMap, HttpStatus.OK);
                }else{
                    hashMap.put(REnum.status,false);
                    hashMap.put(REnum.message,"There is no such writing by the author.");
                    return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
                }



            }else{
                hashMap.put(REnum.status,false);
                hashMap.put(REnum.message,"There is no such writing");
                return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
            }


        }catch (Exception ex){
            hashMap.put(REnum.status,false);
            hashMap.put(REnum.message,ex.getMessage());
            return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Map<REnum,Object>> list(){
        Map<REnum,Object> hashMap =new LinkedHashMap<>();
        hashMap.put(REnum.status,true);
        hashMap.put(REnum.result,writingRepository.findAll());
        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    public ResponseEntity<Map<REnum,Object>> search(String q){
        Map<REnum,Object> hashMap =new LinkedHashMap<>();
        List<Writing> writingList=writingRepository.findByTitleContainsIgnoreCaseOrTextContainsIgnoreCase(q,q);
        hashMap.put(REnum.status,true);
        hashMap.put(REnum.result,writingList);
        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    public ResponseEntity<Map<REnum,Object>> myList(){
        Author author = (Author) httpSession.getAttribute("author");
        Map<REnum,Object> hashMap =new LinkedHashMap<>();
        List<Writing> writingList=writingRepository.findByAuthor_IdEquals(author.getId());
        if (writingList.size()!=0){
            hashMap.put(REnum.status,true);
            hashMap.put(REnum.result,writingList);
            return new ResponseEntity<>(hashMap, HttpStatus.OK);
        }
        hashMap.put(REnum.status,true);
        hashMap.put(REnum.message,"There are no writings in this author");
        return new ResponseEntity<>(hashMap, HttpStatus.OK);

    }

}
