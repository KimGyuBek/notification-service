package com.threadly.notification.adapter.persistence.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.threadly.notification.adapter.persistence.notification.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

  @Value("${spring.data.mongodb.uri}")
  private String mongoUri;

  @Override
  protected String getDatabaseName() {
    return "notification-service";
  }

  @Override
  public MongoClient mongoClient() {
    return MongoClients.create(mongoUri);
  }

  @Bean
  public MongoTemplate mongoTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());

    MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));

    return mongoTemplate;
  }
}