package com.dnrush.service;

import com.dnrush.entity.Statistic;
import com.dnrush.repository.StatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatisticService {
    
    @Autowired
    private StatisticRepository statisticRepository;
    
    public List<Statistic> getActiveStatistics() {
        return statisticRepository.findActiveOrdered();
    }
    
    public Optional<Statistic> getStatisticByKey(String statKey) {
        return statisticRepository.findByStatKeyAndIsActiveTrue(statKey);
    }
    
    public String getStatisticValue(String statKey) {
        return statisticRepository.findByStatKeyAndIsActiveTrue(statKey)
                .map(Statistic::getStatValue)
                .orElse("0");
    }
    
    public Statistic saveStatistic(Statistic statistic) {
        return statisticRepository.save(statistic);
    }
    
    public void deleteStatistic(Long id) {
        statisticRepository.deleteById(id);
    }
    
    public Statistic getStatisticById(Long id) {
        return statisticRepository.findById(id).orElse(null);
    }
}
