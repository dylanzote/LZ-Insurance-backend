package com.zote.notification.service.api.controller;

import com.zote.notification.service.api.request.RegisterDeviceRequest;
import com.zote.notification.service.api.request.UpdateDeviceTokenRequest;
import com.zote.notification.service.api.response.DeviceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Device Management API")
@RequestMapping("/notification/devices")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface DeviceApi {

    @Operation(summary = "Register a device for push notifications")
    @PostMapping("/register")
    DeviceResponse registerDevice(@Valid @RequestBody RegisterDeviceRequest request);

    @Operation(summary = "Update device token")
    @PutMapping("/token")
    void updateDeviceToken(@Valid @RequestBody UpdateDeviceTokenRequest request);

    @Operation(summary = "Get user devices")
    @GetMapping("/user/{userId}")
    List<DeviceResponse> getUserDevices(@PathVariable("userId") String userId);

    @Operation(summary = "Unregister device")
    @DeleteMapping("/{deviceId}")
    void unregisterDevice(@PathVariable("deviceId") String deviceId);

    @Operation(summary = "Subscribe device to topic")
    @PostMapping("/{deviceId}/subscribe/{topic}")
    void subscribeToTopic(@PathVariable("deviceId") String deviceId, @PathVariable("topic") String topic);

    @Operation(summary = "Unsubscribe device from topic")
    @DeleteMapping("/{deviceId}/subscribe/{topic}")
    void unsubscribeFromTopic(@PathVariable("deviceId") String deviceId, @PathVariable("topic")  String topic);

    @Operation(summary = "Get current user's devices")
    @GetMapping("/my-devices")
    List<DeviceResponse> getMyDevices();

    @Operation(summary = "Get device activity logs")
    @GetMapping("/{deviceId}/activity")
    List<Object> getDeviceActivity(@PathVariable("deviceId") String deviceId);

    @Operation(summary = "Update device metadata")
    @PatchMapping("/{deviceId}/metadata")
    void updateDeviceMetadata(
            @PathVariable("deviceId") String deviceId,
            @RequestBody java.util.Map<String, Object> metadata);

    @Operation(summary = "Mark device as inactive")
    @PostMapping("/{deviceId}/deactivate")
    void deactivateDevice(@PathVariable("deviceId") String deviceId);

    @Operation(summary = "Reactivate device")
    @PostMapping("/{deviceId}/reactivate")
    void reactivateDevice(@PathVariable("deviceId") String deviceId);
}
