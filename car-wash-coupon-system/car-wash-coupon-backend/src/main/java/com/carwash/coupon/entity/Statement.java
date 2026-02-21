package com.carwash.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("statement")
public class Statement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long storeId;
    
    private String yearMonth;
    
    private Integer totalCount;
    
    private BigDecimal totalAmount;
    
    private Integer status;
    
    private LocalDateTime confirmTime;
    
    private LocalDateTime paymentTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
