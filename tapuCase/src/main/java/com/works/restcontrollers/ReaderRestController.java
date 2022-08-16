package com.works.restcontrollers;


import com.works.configs.Configs;
import com.works.entities.Author;
import com.works.entities.Reader;
import com.works.services.ReaderService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/reader")
public class ReaderRestController {
    final ReaderService readerService;

    public ReaderRestController(ReaderService readerService) {
        this.readerService = readerService;
    }
    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody Reader reader){
        return readerService.save(reader);
    }

    @Cacheable("readerList")
    @GetMapping("/list")
    public ResponseEntity list(){
        return readerService.list();
    }

    @PutMapping("/update")
    public ResponseEntity update(@Valid @RequestBody Reader reader){
        return readerService.update(reader);
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestParam Long id){
        return  readerService.delete(id);
    }

    @PostMapping("/changePasswordReader")
    public ResponseEntity changePassword(@RequestParam String oldPwd,@RequestParam  String newPwd,
                                         @RequestParam String newPwdConf){
        return readerService.changePasswordReader(oldPwd,newPwd,newPwdConf);
    }

}
