package io.elastic.querybuilder.model;

import java.util.List;

import org.json.simple.JSONObject;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class SearchResponse {

	private long pageNumber;
	private long pageSize;
	private long totalRecords;
	private List<JSONObject> response;
}
