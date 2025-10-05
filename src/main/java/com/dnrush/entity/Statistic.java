package com.dnrush.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
public class Statistic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "stat_key", nullable = false, unique = true, length = 100)
    private String statKey;
    
    @Column(name = "stat_value", nullable = false, length = 100)
    private String statValue;
    
    @Column(name = "stat_label", length = 100)
    private String statLabel;
    
    @Column(name = "icon_class", length = 100)
    private String iconClass;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Statistic() {}
    
    public Statistic(String statKey, String statValue, String statLabel) {
        this.statKey = statKey;
        this.statValue = statValue;
        this.statLabel = statLabel;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStatKey() {
        return statKey;
    }
    
    public void setStatKey(String statKey) {
        this.statKey = statKey;
    }
    
    public String getStatValue() {
        return statValue;
    }
    
    public void setStatValue(String statValue) {
        this.statValue = statValue;
    }
    
    public String getStatLabel() {
        return statLabel;
    }
    
    public void setStatLabel(String statLabel) {
        this.statLabel = statLabel;
    }
    
    public String getIconClass() {
        return iconClass;
    }
    
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
