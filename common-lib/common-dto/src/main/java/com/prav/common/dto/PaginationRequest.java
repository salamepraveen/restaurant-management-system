//package com.prav.common.dto;
//
//import jakarta.validation.constraints.Max;
//import jakarta.validation.constraints.Min;
//import lombok.Data;
//
//@Data
//public class PaginationRequest {
//
//    @Min(value = 0, message = "Page must be >= 0")
//    private int page = 0;
//
//    @Min(value = 1, message = "Size must be >= 1")
//    @Max(value = 100, message = "Size must be <= 100")
//    private int size = 10;
//
//    private String sortBy;
//    private String sortDir = "asc";
//
//    public String getSortDir() {
//        return sortDir.equalsIgnoreCase("desc") ? "desc" : "asc";
//    }
//}