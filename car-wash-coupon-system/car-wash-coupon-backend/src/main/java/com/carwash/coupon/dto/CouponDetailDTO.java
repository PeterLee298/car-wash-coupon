package com.carwash.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CouponDetailDTO {
    private Long id;
    private String code;
    private String name;
    private String typeName;
    private String carType;
    private String serviceType;
    private BigDecimal settlementPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String qrCodeUrl;
    private Long qrCodeExpireTime;
    private List<StoreDTO> applicableStores;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getSettlementPrice() {
        return settlementPrice;
    }

    public void setSettlementPrice(BigDecimal settlementPrice) {
        this.settlementPrice = settlementPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Long getQrCodeExpireTime() {
        return qrCodeExpireTime;
    }

    public void setQrCodeExpireTime(Long qrCodeExpireTime) {
        this.qrCodeExpireTime = qrCodeExpireTime;
    }

    public List<StoreDTO> getApplicableStores() {
        return applicableStores;
    }

    public void setApplicableStores(List<StoreDTO> applicableStores) {
        this.applicableStores = applicableStores;
    }
}
