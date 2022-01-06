package io.elastic.querybuilder.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SearchQueryRequest {

	private String query;
	private List<String> indices;
	private Map<String, List<String>> fields;
}
