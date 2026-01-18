package com.zote.notification.service.api.controller;

import com.zote.notification.service.api.request.UpdatePreferencesRequest;
import com.zote.notification.service.api.response.UserPreferencesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Preferences API")
@RequestMapping("/notification/preferences")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface PreferencesApi {

    @Operation(summary = "Get user preferences")
    @GetMapping("/user/{userId}")
    UserPreferencesResponse getPreferences(@PathVariable("userId") String userId);

    @Operation(summary = "Update user preferences")
    @PutMapping("/user/{userId}")
    UserPreferencesResponse updatePreferences(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UpdatePreferencesRequest request);

    @Operation(summary = "Enable notification channel")
    @PutMapping("/user/{userId}/channels/{channel}/enable")
    void enableChannel(
            @PathVariable("userId") String userId,
            @PathVariable("channel")  String channel);

    @Operation(summary = "Disable notification channel")
    @PutMapping("/user/{userId}/channels/{channel}/disable")
    void disableChannel(
            @PathVariable("userId") String userId,
            @PathVariable("channel") String channel);

    @Operation(summary = "Set user locale")
    @PutMapping("/user/{userId}/locale/{locale}")
    void setLocale(
            @PathVariable("userId") String userId,
            @PathVariable("locale") String locale);

    @Operation(summary = "Set quiet hours")
    @PutMapping("/user/{userId}/quiet-hours")
    void setQuietHours(
            @PathVariable("userId") String userId,
            @RequestParam String startTime,
            @RequestParam String endTime);
}
