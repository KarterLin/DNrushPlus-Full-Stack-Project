package com.dnrush.service;

import com.dnrush.entity.EventPhoto;
import com.dnrush.repository.EventPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventPhotoService {
    
    @Autowired
    private EventPhotoRepository eventPhotoRepository;
    
    public List<EventPhoto> getActivePhotos() {
        return eventPhotoRepository.findActiveOrderedByDate();
    }
    
    public List<EventPhoto> getPhotosByYear(Integer year) {
        return eventPhotoRepository.findByEventYearAndIsActiveTrueOrderByEventMonthDescSortOrder(year);
    }
    
    public List<Integer> getActiveYears() {
        return eventPhotoRepository.findDistinctActiveYears();
    }
    
    public EventPhoto savePhoto(EventPhoto eventPhoto) {
        return eventPhotoRepository.save(eventPhoto);
    }
    
    public void deletePhoto(Long id) {
        eventPhotoRepository.deleteById(id);
    }
    
    public EventPhoto getPhotoById(Long id) {
        return eventPhotoRepository.findById(id).orElse(null);
    }
}
