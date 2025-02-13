package com.books.jwtservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsulRegisteredService {
    @JsonProperty("ServiceName")
    private String serviceName;
    @JsonProperty("ServiceTags")
    private List<String> serviceTags;
}
