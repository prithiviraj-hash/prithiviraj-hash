package com.divum.hiring_platform.exception;

public class DataNotFoundException extends RuntimeException{

    public DataNotFoundException(String error)
    {
        super(error);
    }

}
