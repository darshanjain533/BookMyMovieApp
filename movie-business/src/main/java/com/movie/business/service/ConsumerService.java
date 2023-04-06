package com.movie.business.service;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.business.dto.VendorData;
import com.movie.business.model.Dates;
import com.movie.business.model.SeatPrice;
import com.movie.business.model.Shows;
import com.movie.business.model.Theatres;
import com.movie.business.model.Times;
import com.movie.business.model.Towns;
import com.movie.business.repository.BusinessRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ConsumerService {

	@Autowired
	BusinessRepository repo;

	
    @KafkaListener(topics = {"${spring.kafka.topic}"}, containerFactory = "kafkaListenerStringFactory", groupId = "group_id")
    public void consumeMessage(String message) {
        log.info("**** -> Consumed message -> {}", message);
    }


    @KafkaListener(topics = {"${spring.kafka.data-topic}"}, containerFactory = "kafkaListenerJsonFactory", groupId = "group_id")
    public void consumeData(VendorData data) throws JsonProcessingException {
    	log.info("**** -> Consumed Data:: {}", data);
    	Theatres theatres;
    	Shows shows;
    	Towns towns;
    	Times times;
    	Dates dates;
    	SeatPrice seatprice;
    	String theatreStr = data.getVendorName();
    	String showStr = data.getMovieName();
    	String townStr = data.getMoviePlace();
    	String seatStr = data.getMovieSeat();
    	String timeStr = data.getMovieTime();
    	String dateStr = data.getMovieDate();
    	Optional<Theatres> theatresOptional = repo.findById(theatreStr);
    	if(theatresOptional.isPresent()) {
    		theatres = theatresOptional.get();
    		log.info("Theatre is present..."+theatres);
    		Optional<Shows> showOptional = theatres.getShows().stream().filter(a -> a.getShow().equalsIgnoreCase(showStr)).findFirst();
    		if(showOptional.isPresent()) {
    			shows = showOptional.get();
    			log.info("shows are present..."+shows);
    			Optional<Towns> townsOptional = shows.getTowns().stream().filter(b -> b.getTown().equalsIgnoreCase(townStr)).findFirst();
    			if(townsOptional.isPresent()) {
    				towns = townsOptional.get();
    				log.info("towns are present..."+towns);
    				Optional<Dates> datesOptional = towns.getDates().stream().filter(c -> c.getDate().equalsIgnoreCase(dateStr)).findFirst();
    				if(datesOptional.isPresent()) {
    					dates = datesOptional.get();
    					Optional<Times> timessOptional = dates.getTimes().stream().filter(d -> d.getTime().equalsIgnoreCase(timeStr)).findFirst();
    					if(timessOptional.isPresent()) {
    						times = timessOptional.get();
    						Optional<SeatPrice> seatpriceOptional = times.getSeatprice().stream().filter(e -> e.getSeat().equalsIgnoreCase(seatStr)).findFirst();
    						if(seatpriceOptional.isPresent()) {
    							//seat is already present. update the price.
    							seatprice = seatpriceOptional.get();
    							times.getSeatprice().remove(seatprice);
    							seatprice.setPrice(data.getMoviePrice());
    							seatprice.setSeat(seatStr);
    							times.getSeatprice().add(seatprice);
    							//repo.save(theatres);
    						}else {
    							//create new seat
    							seatprice = SeatPrice.builder().price(data.getMoviePrice()).seat(seatStr).build();
    							times.getSeatprice().add(seatprice);
    							//repo.save(theatres);
    						}
    					}else {
    						//create new time
    						seatprice = SeatPrice.builder().price(data.getMoviePrice()).seat(seatStr).build();
    						times = Times.builder().seatprice(Arrays.asList(seatprice)).time(timeStr).build();
                    		dates.getTimes().add(times);
                    		//repo.save(theatres);
    					}
    				}else {
    					//create new date
    					seatprice = SeatPrice.builder().price(data.getMoviePrice()).seat(seatStr).build();
    					times = Times.builder().seatprice(Arrays.asList(seatprice)).time(timeStr).build();
    		    		dates = Dates.builder().times(Arrays.asList(times)).date(dateStr).build();
    		    		//towns = Towns.builder().dates(Arrays.asList(dates)).town(townStr).build();
                		//towns.getTimes().add(times);
    		    		towns.getDates().add(dates);
                		//repo.save(theatres);
    				}
    			}else {
    				//create new town
    				seatprice = SeatPrice.builder().price(data.getMoviePrice()).seat(seatStr).build();
    				times = Times.builder().seatprice(Arrays.asList(seatprice)).time(timeStr).build();
    	    		dates = Dates.builder().times(Arrays.asList(times)).date(dateStr).build();
    	    		towns = Towns.builder().dates(Arrays.asList(dates)).town(townStr).build();
            		shows.getTowns().add(towns);
            		//repo.save(theatres);
    			}
    		}else {
    			//create new show
    			seatprice = SeatPrice.builder().price(data.getMoviePrice()).seat(seatStr).build();
    			times = Times.builder().seatprice(Arrays.asList(seatprice)).time(timeStr).build();
        		dates = Dates.builder().times(Arrays.asList(times)).date(dateStr).build();
        		towns = Towns.builder().dates(Arrays.asList(dates)).town(townStr).build();
        		shows = Shows.builder().towns(Arrays.asList(towns)).show(showStr).build();
        		theatres.getShows().add(shows);
        		//repo.save(theatres);
    		}
    	}else {
    		//create new theatre
    		log.info("create new theatre");
    		seatprice = SeatPrice.builder().price(data.getMoviePrice()).seat(seatStr).build();
    		times = Times.builder().seatprice(Arrays.asList(seatprice)).time(timeStr).build();
    		dates = Dates.builder().times(Arrays.asList(times)).date(dateStr).build();
    		towns = Towns.builder().dates(Arrays.asList(dates)).town(townStr).build();
    		shows = Shows.builder().towns(Arrays.asList(towns)).show(showStr).build();
    		theatres = Theatres.builder().shows(Arrays.asList(shows)).id(theatreStr).build();
    		//repo.save(theatres);
    	}
    	log.info("theatre object is:::"+theatres);
    	log.info("json object for theatre is:::"+new ObjectMapper().writeValueAsString(theatres));
    	repo.save(theatres);
    }

}
