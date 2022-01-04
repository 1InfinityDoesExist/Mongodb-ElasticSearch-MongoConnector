package io.elastic.querybuilder.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ElasticConfig {

	@Value("${elastic.host:127.0.0.1}")
	private String host;

	@Value("${elastic.port:9200}")
	private int port;

	@Bean
	public RestHighLevelClient client() {
		log.info("-----RestHighLevelClient Bean Creation");
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost(host, port, HttpHost.DEFAULT_SCHEME_NAME)));
		log.info("----Client created : {}", client);
		return client;
	}
}
