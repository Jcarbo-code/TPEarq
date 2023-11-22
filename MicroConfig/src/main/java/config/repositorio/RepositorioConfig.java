package config.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;

import config.modelo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;


import java.time.LocalDate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public interface RepositorioConfig extends MongoRepository<Config, String>{

    default Optional<Double> findCurrentStandardPrice(LocalDate today, MongoTemplate mongoTemplate) {
        AggregationOperation match = match(Criteria.where("fechaCambio").lte(today));
        AggregationOperation sort = sort(Sort.by(Sort.Order.desc("fechaCambio")));
        AggregationOperation limit = limit(1);
        AggregationOperation project = project("tarifa").andExclude("_id");

        Aggregation aggregation = newAggregation(match, sort, limit, project);

        List<Config> fares = mongoTemplate.aggregate(aggregation, Config.class, Config.class).getMappedResults();

        return fares.isEmpty() ? Optional.empty() : Optional.of(fares.get(0).getStandardPrice());
    }

    default Optional<Double> findCurrentExtendedPausePrice(LocalDate today, MongoTemplate mongoTemplate) {
        AggregationOperation match = match(Criteria.where("fechaCambio").lte(today));
        AggregationOperation sort = sort(Sort.by(Sort.Order.desc("fechaCambio")));
        AggregationOperation limit = limit(1);
        AggregationOperation project = project("tarifa2").andExclude("_id");

        Aggregation aggregation = newAggregation(match, sort, limit, project);

        List<Config> fares = mongoTemplate.aggregate(aggregation, Config.class, Config.class).getMappedResults();

        return fares.isEmpty() ? Optional.empty() : Optional.of(fares.get(0).getExtendedPausePrice());
    }

}