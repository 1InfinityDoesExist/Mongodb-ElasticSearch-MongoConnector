package io.elastic.querybuilder.model;

import java.util.List;

import lombok.Data;

@Data
public class SearchQueryRequest {

	private String query;
	private List<String> indices;

}
