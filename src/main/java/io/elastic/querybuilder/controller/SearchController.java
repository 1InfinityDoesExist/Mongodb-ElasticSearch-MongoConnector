package io.elastic.querybuilder.controller;

import java.io.IOException;

import org.elasticsearch.ElasticsearchParseException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.elastic.querybuilder.model.SearchQueryRequest;
import io.elastic.querybuilder.service.SearchService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private SearchService searchService;

	@PostMapping("")
	public ResponseEntity<?> search(@RequestBody SearchQueryRequest request, Pageable pageable)
			throws IOException, ElasticsearchParseException, ParseException {
		log.info("-----Search Controller");
		return ResponseEntity.status(HttpStatus.OK).body(searchService.searchCore(request, pageable));
	}
}
