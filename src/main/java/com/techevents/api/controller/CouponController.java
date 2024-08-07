package com.techevents.api.controller;

import com.techevents.api.domain.coupon.Coupon;
import com.techevents.api.domain.coupon.CouponRequestDTO;
import com.techevents.api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Coupon> addCouponToEvent(@PathVariable UUID eventId, @RequestBody CouponRequestDTO couponRequestDTO) {
        Coupon coupon = couponService.addCouponToEvent(eventId, couponRequestDTO);
        return ResponseEntity.ok(coupon);
    }
}
