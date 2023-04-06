package com.movie.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorData {
	
	private String vendorId;
	private String vendorName;
	private String movieScreen;
	private String movieName;
	private String movieSeat;
	private String movieDate;
	private String movieTime;
	private String moviePrice;
	private String moviePlace;
}
