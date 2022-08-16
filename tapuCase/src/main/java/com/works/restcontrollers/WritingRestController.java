package com.works.restcontrollers;

import com.works.entities.Reader;
import com.works.entities.Writing;
import com.works.services.WritingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/writing")
public class WritingRestController {

    final WritingService writingService;

    public WritingRestController(WritingService writingService) {
        this.writingService = writingService;
    }
    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody Writing writing){
        return writingService.save(writing);
    }

    @Cacheable("writingList")
    @GetMapping("/list")
    public ResponseEntity list(){
        return writingService.list();
    }

    @Cacheable("mylist")
    @GetMapping("/myList")
    public ResponseEntity mylist(){
        return writingService.myList();
    }

    @PutMapping("/update")
    public ResponseEntity update(@Valid @RequestBody Writing writing){
        return writingService.update(writing);
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestParam Long id){
        return  writingService.delete(id);
    }

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String q){
        return writingService.search(q);
    }

}
