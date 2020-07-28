package com.overops.azure.boards;

/**
 * Azure Boards Exception
 *
 * Generic exception to be thrown in the scope of this UDF
 */
public class AzureBoardsException extends RuntimeException {

    public AzureBoardsException(String msg){
        super(msg);
    }

    public AzureBoardsException(String msg, Throwable throwable){
        super(msg, throwable);
    }

}
