package com.qisstpay.lendingservice.exception;

import com.qisstpay.commons.error.errortype.ApplicationErrorType;
import com.qisstpay.commons.exception.CustomException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.commons.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // This will be used in logging, to get the error code.
    public static final String ERROR_CODE = "_ERROR_CODE";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public CustomResponse<Object> customException(HttpServletRequest request, CustomException ex) {
        log.error("Custom-Exception", ex);
        return sendResponse(request, ex.getErrorCode(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CustomResponse<Object> unHandledException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled-Exception", ex);
        CustomException customException = new CustomException(ex);
        return sendResponse(request, customException.getErrorCode(), customException.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CustomResponse<Object> missingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.error("Missing-Parameter", ex);
        return sendResponse(request, ApplicationErrorType.PARAMETER_MISSING.getErrorCode(), String.format("The request parameter %s is missing.", ex.getParameterName()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CustomResponse<Object> methodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.error("Method-Not-Supported", ex);
        return sendResponse(request, ApplicationErrorType.INVALID_REQUEST.getErrorCode(), "Invalid HTTP method.");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletRequestBindingException.class)
    public CustomResponse<Object> headerMissing(ServletRequestBindingException ex, HttpServletRequest request) {
        log.error("Header-Missing", ex);
        return sendResponse(request, ApplicationErrorType.INVALID_REQUEST.getErrorCode(), "Invalid HTTP request, either body or headers are missing.");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CustomResponse<Object> bodyMissing(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("Body-Missing", ex);
        return sendResponse(request, ApplicationErrorType.INVALID_REQUEST.getErrorCode(), "HTTP body payload is missing.");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CustomResponse<Object> mediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.error("MediaType-NotSupported", ex);
        return sendResponse(request, ApplicationErrorType.INVALID_REQUEST.getErrorCode(), "Invalid/Missing Content type, only application/json is allowed.");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CustomResponse<Object> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("Method-Argument-Type-Mismatch", ex);
        return sendResponse(request, ApplicationErrorType.PARAMETER_TYPE_MISMATCH.getErrorCode(), "Parameter Type mismatched ");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public CustomResponse<Object> methodEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("Entity-Not-Found", ex);
        return sendResponse(request, ApplicationErrorType.INVALID_REQUEST.getErrorCode(), "Invalid Request ");
    }

    private CustomResponse<Object> sendResponse(HttpServletRequest request, String errorCode, String message) {
        request.setAttribute(ERROR_CODE, errorCode); // This is used for logging.
        ErrorResponse errorResponse = ErrorResponse.builder().errorCode(errorCode).errorMessage(message).build();
        return CustomResponse.builder().errors(Collections.singletonList(errorResponse)).build();
    }

}