package com.zote.resourcebundle.adapter.api.controller;

import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.SortField;
import com.zote.common.utils.models.Permissions;
import com.zote.resourcebundle.adapter.api.request.CreateTemplateRequest;
import com.zote.resourcebundle.adapter.api.request.GetTemplateRequest;
import com.zote.resourcebundle.adapter.api.request.UpdateTemplateRequest;
import com.zote.resourcebundle.adapter.api.response.TemplatePageResponse;
import com.zote.resourcebundle.adapter.api.response.TemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Message Template API")
@RestController
@RequestMapping("/messageTemplate/")
public interface MessageTemplateApi {

    @Operation(summary = "create a new message template")
    @PostMapping("create")
    TemplateResponse createTemplate(@RequestBody CreateTemplateRequest request);

    @Operation(summary = "get all message templates")
    @GetMapping("get-all")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    List<TemplateResponse> getTemplates();

    @Operation(summary = "get template by page")
    @GetMapping("get-all-page")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    TemplatePageResponse getAllTemplate(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                        @RequestParam(name = "sizePerPage", defaultValue = "5") Integer sizePerPage,
                                        @RequestParam(name = "sortField", defaultValue = "CREATED_ON") SortField sortField,
                                        @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);

    @Operation(summary = "get template by id")
    @GetMapping("get/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    TemplateResponse getTemplate(@PathVariable("id") String id);

    @Operation(summary = "get template by language and template name",
    parameters = {@Parameter(name = "language", in = ParameterIn.QUERY, required = true, example = "EN", schema = @Schema(implementation = Language.class)),
                  @Parameter(name = "templateName", in = ParameterIn.QUERY, required = true, example = "Welcome")}
    )
    @GetMapping("get")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    TemplateResponse getTemplate(GetTemplateRequest getTemplateRequest);

    @Operation(summary = "update template")
    @PutMapping("update")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    TemplateResponse updateTemplate(@RequestBody UpdateTemplateRequest request);

    @Operation(summary = "delete template by id")
    @DeleteMapping("delete/{id}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void deleteTemplate(@PathVariable("id") String id);
}
