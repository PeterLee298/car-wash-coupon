package com.carwash.coupon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.ApiResponse;
import com.carwash.coupon.entity.Activity;
import com.carwash.coupon.entity.FunctionEntry;
import com.carwash.coupon.repository.ActivityRepository;
import com.carwash.coupon.repository.FunctionEntryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "公共接口", description = "无需认证的公共接口")
@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final ActivityRepository activityRepository;
    private final FunctionEntryRepository functionEntryRepository;

    public PublicController(ActivityRepository activityRepository, FunctionEntryRepository functionEntryRepository) {
        this.activityRepository = activityRepository;
        this.functionEntryRepository = functionEntryRepository;
    }

    @Operation(summary = "获取活动轮播图")
    @GetMapping("/activities")
    public ApiResponse<List<Activity>> getActivities() {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 1)
               .le(Activity::getStartTime, LocalDateTime.now())
               .ge(Activity::getEndTime, LocalDateTime.now())
               .orderByAsc(Activity::getSortOrder);
        List<Activity> activities = activityRepository.selectList(wrapper);
        return ApiResponse.success(activities);
    }

    @Operation(summary = "获取功能入口")
    @GetMapping("/function-entries")
    public ApiResponse<List<FunctionEntry>> getFunctionEntries() {
        LambdaQueryWrapper<FunctionEntry> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FunctionEntry::getStatus, 1)
               .orderByAsc(FunctionEntry::getSortOrder);
        List<FunctionEntry> entries = functionEntryRepository.selectList(wrapper);
        return ApiResponse.success(entries);
    }
}
