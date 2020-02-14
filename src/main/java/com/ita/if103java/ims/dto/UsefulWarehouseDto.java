package com.ita.if103java.ims.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UsefulWarehouseDto {
   @NotNull
   private Long id;
   @NotBlank
   private List<String> path;

    public UsefulWarehouseDto() {
    }

    public UsefulWarehouseDto(@NotNull Long id, @NotBlank List<String> path) {
        this.id = id;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }
}
