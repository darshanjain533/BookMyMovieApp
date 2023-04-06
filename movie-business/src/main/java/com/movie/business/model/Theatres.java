package com.movie.business.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Theatres {

	private String id; //theatre name
	private List<Shows> shows;
}


//Theatre --> List<Shows> --> List<Town> --> List<Date> --> List<Time> --> List<SeatPrice>