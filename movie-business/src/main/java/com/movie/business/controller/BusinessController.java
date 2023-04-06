package com.movie.business.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movie.business.dto.MessageResult;
import com.movie.business.dto.VendorData;
import com.movie.business.model.Dates;
import com.movie.business.model.Shows;
import com.movie.business.model.Theatres;
import com.movie.business.model.Times;
import com.movie.business.model.Towns;
import com.movie.business.repository.BusinessRepository;
import com.movie.business.service.ProducerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/business")
public class BusinessController {

	@Autowired
	ProducerService<VendorData> producer;
	
	@Autowired
	MongoOperations mongoOps;
	
	
	@Autowired
	BusinessRepository repo;
	
	@GetMapping("/test")
	public String test() {
		log.info("test business controller");
		return "test data";
	}
	
	@PostMapping("/theatre/add")
	public MessageResult addTheatre() throws FileNotFoundException, IOException {
		Workbook workbook = new XSSFWorkbook(new FileInputStream("D:\\Work\\data.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> itr = sheet.iterator(); // iterating over excel file
		itr.next();
		while (itr.hasNext()) {
			Row row = itr.next();
			if (row.getCell(0) != null) {
				String vendorId = row.getCell(0).getStringCellValue();
				String vendorName = row.getCell(1).getStringCellValue();
				String movieName = row.getCell(2).getStringCellValue();
				String movieSeat = row.getCell(3).getStringCellValue();
				String movieDate = row.getCell(4).getStringCellValue();
				String movieTime = row.getCell(5).getStringCellValue();
				String moviePrice = row.getCell(6).getStringCellValue();
				String moviePlace = row.getCell(7).getStringCellValue();
				
				VendorData data = VendorData.builder()
									.vendorId(vendorId)
									.vendorName(vendorName)
									.movieSeat(movieSeat)
									.movieName(movieName)
									.moviePrice(moviePrice)
									.movieTime(movieTime)
									.movieDate(movieDate)
									.moviePlace(moviePlace)
									.build();
				
				log.info("data is:::"+data);
				producer.sendDataMessage(data);
			}
		}
		return new MessageResult(200, "success");
	}
	
	@GetMapping("/theatre/list")
	public List<Theatres> getMovie(@RequestParam String movie, @RequestParam String city, @RequestParam String time, @RequestParam String date) throws JsonProcessingException{
		
		List<Theatres> theatresList = repo.findAll();
		List<Theatres> newTheatresList = new ArrayList<>();
		theatresList.forEach(eachtheatre -> {
			if(eachtheatre.getShows() != null) {
				eachtheatre.getShows().forEach(eachshow -> {
					if(!eachshow.getShow().isEmpty() && eachshow.getShow().equalsIgnoreCase(movie)) {
						log.info("inside movie...");
						eachshow.getTowns().forEach(eachtown->{
							if(!eachtown.getTown().isEmpty() && eachtown.getTown().equalsIgnoreCase(city)) {
								log.info("inside city...");
								eachtown.getDates().forEach(eachdate->{
									if(!eachdate.getDate().isEmpty() && eachdate.getDate().equalsIgnoreCase(date)) {
										log.info("inside date...");
										eachdate.getTimes().forEach(eachtime -> {
											if(!eachtime.getTime().isEmpty() && eachtime.getTime().equalsIgnoreCase(time)) {
												log.info("inside date...");
												eachtime.getSeatprice();
												Times times = Times.builder().seatprice(eachtime.getSeatprice()).time(time).build();
												Dates dates = Dates.builder().times(Arrays.asList(times)).date(date).build();
												Towns towns = Towns.builder().dates(Arrays.asList(dates)).town(city).build();
												Shows shows = Shows.builder().towns(Arrays.asList(towns)).show(movie).build();
												Theatres theatres = Theatres.builder().shows(Arrays.asList(shows)).id(eachtheatre.getId()).build();
												log.info("theatre is::"+theatres);
												/*
												Dates dates = Dates.builder().seatprice(eachdate.getSeatprice()).date(date).build();
												Times times = Times.builder().dates(Arrays.asList(dates)).time(time).build();
									    		Towns towns = Towns.builder().times(Arrays.asList(times)).town(city).build();
									    		Shows shows = Shows.builder().towns(Arrays.asList(towns)).show(movie).build();
									    		Theatres theatres = Theatres.builder().shows(Arrays.asList(shows)).id(eachtheatre.getId()).build();
									    		*/
												newTheatresList.add(theatres);
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
		
		return newTheatresList;
	}


	@DeleteMapping("/theatre/delete/{movie}/{date}")
	public String deleteTheatre(@PathVariable String movie, @PathVariable String date) {
		log.info("delete theatre called...");
		StringBuilder data = new StringBuilder();
		List<Theatres> theatresList = repo.findAll();
		theatresList.forEach(eachtheatre -> {
			if(eachtheatre.getShows() != null) {
				eachtheatre.getShows().forEach(eachshow -> {
					if(eachshow.getShow().equalsIgnoreCase(movie)) {
						eachshow.getTowns().forEach(eachtown -> {
							if(eachtown.getDates() != null) {
								for(Iterator<Dates> iterator = eachtown.getDates().iterator(); iterator.hasNext(); ) {
									Dates value = iterator.next();
									if(value.getDate()!=null && value.getDate().equalsIgnoreCase(date)) {
										iterator.remove();
										repo.save(eachtheatre);
										data.append("data deleted...");
									}
								}
							}
						});
					}
				});
			}
		});
		return data.toString();
	}
}
