package com.zote.user.service.api.controller;

import com.zote.common.utils.models.Permissions;
import com.zote.user.service.api.request.CreateRoleRequest;
import com.zote.user.service.api.request.UpdateRoleRequest;
import com.zote.user.service.api.response.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Role API")
@RestController
@RequestMapping("/role/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface RoleApi {

    @Operation(summary = "create a new Role")
    @PostMapping("create")
    RoleResponse createRole(@RequestBody CreateRoleRequest request);

    @Operation(summary = "get all Roles")
    @GetMapping("get-all")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    List<RoleResponse> getAllRoles();

    @Operation(summary = "get a Role by id")
    @GetMapping("get/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    RoleResponse getRole(@PathVariable("id") String id);

    @Operation(summary = "update a Role")
    @PutMapping("update")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    RoleResponse updateRole(@RequestBody UpdateRoleRequest request);

    @Operation(summary = "delete a Role by id")
    @DeleteMapping("delete/{id}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void deleteRole(@PathVariable("id") String id);
}
