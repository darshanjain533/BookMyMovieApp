package com.movie.business.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class CustomException extends ResponseEntityExceptionHandler {


	// 400
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.info(ex.getClass().getName());
		log.info("path is::::" + request.getContextPath());
		final List<String> errors = new ArrayList<>();
		for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		ExceptionData excep = ExceptionData.builder().errors(errors).status(HttpStatus.BAD_REQUEST)
				.message(ex.getLocalizedMessage())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return handleExceptionInternal(ex, excep, headers, excep.getStatus(), request);
	}

	protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers,final HttpStatus status, final WebRequest request) {
		log.info(ex.getClass().getName());
		//
		final List<String> errors = new ArrayList<>();
		for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		ExceptionData excep = ExceptionData.builder().errors(errors).status(HttpStatus.BAD_REQUEST)
				.message(ex.getLocalizedMessage())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return handleExceptionInternal(ex, excep, headers, excep.getStatus(), request);
	}

	protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers,final HttpStatus status, final WebRequest request) {
		log.info(ex.getClass().getName());
		final List<String> errors = new ArrayList<>();
		final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type "
				+ ex.getRequiredType();
		errors.add(error);
		ExceptionData excep = ExceptionData.builder().errors(errors).status(HttpStatus.BAD_REQUEST)
				.message(ex.getLocalizedMessage())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.info(ex.getClass().getName());
		final List<String> errors = new ArrayList<>();
		final String error = ex.getRequestPartName() + " part is missing";
		errors.add(error);
		ExceptionData excep = ExceptionData.builder().errors(errors).status(HttpStatus.BAD_REQUEST)
				.message(ex.getLocalizedMessage())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,final WebRequest request) {
		log.info(ex.getClass().getName());
		final List<String> errors = new ArrayList<>();
		final String error = ex.getParameterName() + " parameter is missing";
		errors.add(error);
		ExceptionData excep = ExceptionData.builder().errors(errors).status(HttpStatus.BAD_REQUEST)
				.message(ex.getLocalizedMessage())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex,final WebRequest request) {
		log.info(ex.getClass().getName());
		final List<String> errors = new ArrayList<>();
		final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
		errors.add(error);
		ExceptionData excep = ExceptionData.builder().errors(errors).status(HttpStatus.BAD_REQUEST)
				.message(ex.getLocalizedMessage())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	// 404
	protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.info(ex.getClass().getName());
		final List<String> errors = new ArrayList<>();
		final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
		errors.add(error);
		ExceptionData excep = ExceptionData.builder().errors(errors).status(HttpStatus.BAD_REQUEST)
				.message(ex.getLocalizedMessage())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	// 405
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status,final WebRequest request) {
		log.info(ex.getClass().getName());
		final StringBuilder builder = new StringBuilder();
		final List<String> errors = new ArrayList<>();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are ");
		ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
		errors.add(builder.toString());
		ExceptionData excep = ExceptionData.builder().status(HttpStatus.METHOD_NOT_ALLOWED)
				.message(ex.getLocalizedMessage()).errors(errors)
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	// 415
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.info(ex.getClass().getName());
		final List<String> errors = new ArrayList<>();
		final StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
		errors.add(builder.substring(0, builder.length() - 2).toString());
		ExceptionData excep = ExceptionData.builder().status(HttpStatus.METHOD_NOT_ALLOWED)
				.message(ex.getLocalizedMessage()).errors(errors)
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	// 500
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
		logger.info(ex.getClass().getName());
		log.info("path is::::" + ((ServletWebRequest) request).getRequest().getRequestURI());
		final List<String> errors = new ArrayList<>();
		logger.error("error", ex);
		errors.add("ERROR occurred....");
		ExceptionData excep = ExceptionData.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(ex.getLocalizedMessage()).errors(errors)
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.info(ex.getClass().getName());
		log.info("path is::::" + ((ServletWebRequest) request).getRequest().getRequestURI());
		final List<String> errors = new ArrayList<>();
		logger.error("error", ex);
		errors.add("ERROR occurred....");
		ExceptionData excep = ExceptionData.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(ex.getLocalizedMessage()).errors(errors)
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI())
				.requestpath(((ServletWebRequest) request).getRequest().getRequestURI()).date(new Date()).build();
		return new ResponseEntity<Object>(excep, new HttpHeaders(), excep.getStatus());
	}

}
