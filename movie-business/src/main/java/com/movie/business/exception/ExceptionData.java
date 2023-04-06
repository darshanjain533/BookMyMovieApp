package com.movie.business.exception;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionData {
	private HttpStatus status;
	private String message;
	private List<String> errors;
	private String requestpath;
	private Date date;
}
