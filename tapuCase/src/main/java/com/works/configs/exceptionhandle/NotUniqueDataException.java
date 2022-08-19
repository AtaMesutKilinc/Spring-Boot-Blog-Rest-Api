package com.works.configs.exceptionhandle;

public class NotUniqueDataException extends RuntimeException {
    public NotUniqueDataException(){
        super();
    }

    public NotUniqueDataException(String message){
        super(message);
    }
}
