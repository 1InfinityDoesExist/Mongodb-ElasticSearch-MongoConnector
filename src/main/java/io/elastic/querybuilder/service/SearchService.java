package io.elastic.querybuilder.service;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.ElasticsearchParseException;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.elastic.querybuilder.model.SearchQueryRequest;
import io.elastic.querybuilder.model.SearchResponse;

@Service
public interface SearchService {

	public SearchResponse searchCore(SearchQueryRequest requ, Pageable pageable)
			throws IOException, ElasticsearchParseException, ParseException;

	public List<String> getAllMappings(String index) throws IOException, ElasticsearchParseException, ParseException;
}
