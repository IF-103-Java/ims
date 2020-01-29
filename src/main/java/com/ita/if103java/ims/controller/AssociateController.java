package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AssociateService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/associates")
public class AssociateController {

    private AssociateService associateService;

    @Autowired
    public AssociateController(AssociateService associateService) {
        this.associateService = associateService;
    }

    @GetMapping(value = "/{id}")
    public AssociateDto view(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("id") Long id) {
        return associateService.view(user, id);
    }

    @GetMapping(value = "/")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query"),
        @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query"),
        @ApiImplicitParam(name = "sort", dataType = "string", paramType = "query")
    })
    public Page<AssociateDto> findAllSortedAssociates(@ApiIgnore Pageable pageable, @AuthenticationPrincipal UserDetailsImpl user) {
        return associateService.findSortedAssociates(pageable, user);
    }

    @PostMapping(value = "/")
    public AssociateDto create(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody AssociateDto associateDto) {
        return associateService.create(user, associateDto);
    }

    @PutMapping("/{id}")
    public AssociateDto update(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody AssociateDto associateDto, @PathVariable("id") long id) {
        associateDto.setId(id);
        return associateService.update(user, associateDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("id") Long id) {
        associateService.delete(user, id);
    }

}
