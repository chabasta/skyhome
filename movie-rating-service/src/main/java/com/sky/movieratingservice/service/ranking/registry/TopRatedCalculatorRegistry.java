package com.sky.movieratingservice.service.ranking.registry;

import com.sky.movieratingservice.service.ranking.strategy.RankingStrategy;
import com.sky.movieratingservice.service.ranking.strategy.TopRatedCalculator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class TopRatedCalculatorRegistry {

    private final Map<RankingStrategy, TopRatedCalculator> calculatorsByKey;

    public TopRatedCalculatorRegistry(List<TopRatedCalculator> calculators) {
        this.calculatorsByKey = calculators.stream()
                .collect(Collectors.toMap(TopRatedCalculator::getKey, Function.identity()));
    }

    public TopRatedCalculator getRequired(RankingStrategy key) {
        TopRatedCalculator calculator = calculatorsByKey.get(key);
        if (calculator == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Unknown ranking strategy: " + key);
        }
        return calculator;
    }
}
