package ua.ubki.cassmon.exception;

import java.util.StringJoiner;

public class UbkiException extends RuntimeException {

    private String inn;
    private Integer httpStatus = 500;
    private Integer outerHttpStatus;
    private String outerMessage;

    public UbkiException(String inn, Integer httpStatus, String message) {
        super(message);
        this.inn = inn;
        this.httpStatus = httpStatus;
    }

    public UbkiException(String inn, Integer httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.inn = inn;
        this.httpStatus = httpStatus;
    }

    public UbkiException(Integer httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public UbkiException(Integer httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public UbkiException(String inn, Integer httpStatus, String message, Integer outerHttpStatus, String outerMessage) {
        super(message);
        this.inn = inn;
        this.httpStatus = httpStatus;
        this.outerHttpStatus = outerHttpStatus;
        this.outerMessage = outerMessage;
    }

    public UbkiException(Integer httpStatus, String message, Integer outerHttpStatus, String outerMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.outerHttpStatus = outerHttpStatus;
        this.outerMessage = outerMessage;
    }

    public String getInn() {
        return inn;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public Integer getOuterHttpStatus() {
        return outerHttpStatus;
    }

    public String getOuterMessage() {
        return outerMessage;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UbkiException.class.getSimpleName() + "[", "]")
                .add("inn='" + inn + "'")
                .add("httpStatus=" + httpStatus)
                .add("outerHttpStatus=" + outerHttpStatus)
                .add("outerMessage='" + outerMessage + "'")
                .toString();
    }
}
