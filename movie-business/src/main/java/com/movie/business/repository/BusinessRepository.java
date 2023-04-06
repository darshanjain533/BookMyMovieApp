package com.movie.business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.movie.business.model.Theatres;

@Repository
public interface BusinessRepository extends MongoRepository<Theatres, String>{
}
