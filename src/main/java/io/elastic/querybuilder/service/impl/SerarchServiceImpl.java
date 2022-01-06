package io.elastic.querybuilder.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;

import io.elastic.querybuilder.model.SearchQueryRequest;
import io.elastic.querybuilder.model.SearchResponse;
import io.elastic.querybuilder.service.SearchService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SerarchServiceImpl implements SearchService {

	private static final ObjectMapper objectMapper;

	@Autowired
	private RestHighLevelClient esClient;

	@Value("${search.regex:*%s*}")
	private String searchRegex;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public SearchResponse searchCore(SearchQueryRequest request, Pageable pageable)
			throws IOException, ElasticsearchParseException, ParseException {
		String query = request.getQuery();
		if (ObjectUtils.isEmpty(query)) {
			query = query.replace(" ", "*");
		}
		log.info("Final query : {}, and indeces : {}", request.getQuery(), request.getIndices().get(0));
		List<String> indices = request.getIndices(); // list of indices in which you want to search
		BoolQueryBuilder searchQuery = QueryBuilders.boolQuery();
		for (String index : indices) {
			searchQuery.should(getSearchQuery(index, query, request));
		}
		log.info("-----Final Query : {}", searchQuery);

		SearchSourceBuilder builder = new SearchSourceBuilder().query(searchQuery)
				.from(pageable.getPageNumber() * pageable.getPageSize()).size(pageable.getPageSize()).explain(true);

		// In case of sorting
		Optional.ofNullable(pageable.getSort()).map(Sort::iterator)
				.ifPresent(iterator -> iterator.forEachRemaining(
						order -> builder.sort(order.getProperty(), Optional.ofNullable(order.getDirection())
								.map(Direction::toString).map(SortOrder::fromString).orElse(SortOrder.ASC))));

		SearchHits searchHits = esClient
				.search(new SearchRequest(Iterables.toArray(indices, String.class)).source(builder),
						RequestOptions.DEFAULT)
				.getHits();

		SearchHit[] hits = searchHits.getHits();
		List<JSONObject> collect = Arrays.stream(hits).map(document -> {
			JSONObject source = new JSONObject();
			try {
				source = (JSONObject) new JSONParser().parse(document.getSourceAsString());
				log.info("---Source : {}", source);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return source;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return SearchResponse.builder().pageNumber(pageable.getPageNumber()).pageSize(pageable.getPageSize())
				.totalRecords(searchHits.getTotalHits().value).response(collect).build();

	}

	private QueryBuilder getSearchQuery(String index, String keyword, SearchQueryRequest request)
			throws ElasticsearchParseException, IOException, ParseException {
		log.info("----Create QueryBuilder");
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.typeQuery("instance"));

		if (CollectionUtils.isEmpty(request.getFields()) || CollectionUtils.isEmpty(request.getFields().get(index))) {
			queryBuilder.must(QueryBuilders.queryStringQuery(String.format("*%s*", keyword)));
		} else {
			List<String> fields = request.getFields().get(index);
			BoolQueryBuilder qbuilder = QueryBuilders.boolQuery();
			for (String field : fields) {
				qbuilder.should(QueryBuilders.queryStringQuery(String.format("*%s*", keyword)).field(field));
			}
			queryBuilder.must(qbuilder);
		}

		log.info("----Query : {}", queryBuilder);
		return queryBuilder;
	}

	// method to get all the mapping using esClient
	public List<String> getAllMappings(String index) throws IOException, ElasticsearchParseException, ParseException {
		log.info("----Retrieving all the mapping data. for index : {}", index);
		List<String> att = new ArrayList<>();
		GetMappingsRequest request = new GetMappingsRequest();
		request.indices(index);
		request.setMasterTimeout(TimeValue.timeValueMinutes(1));
		// sync call
		GetMappingsResponse syncMappingResponse = esClient.indices().getMapping(request, RequestOptions.DEFAULT);
		JSONObject entityJsonObject = (JSONObject) ((JSONObject) new JSONParser()
				.parse(new ObjectMapper().writeValueAsString(syncMappingResponse.mappings().get(index).sourceAsMap())))
						.get("properties");
		log.info("-----Object : {}", entityJsonObject);

		for (Object obj : entityJsonObject.keySet()) {
			String prop = (String) obj;
			log.info("---Prop : {}", prop);
			att.add(prop);
		}
		log.info("----Attribute on which searcing will be called : {}", att.size());
		return att;
	}

}
