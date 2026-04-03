package com.example.ai.demo.configuration;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.PgVectorStore.PgDistanceType;
import org.springframework.ai.vectorstore.PgVectorStore.PgIndexType;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

// @Configuration
// public class VectorStoreConfig {

//     @Bean
//     public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
//         return new PgVectorStore(jdbcTemplate, embeddingModel,
//                 1536,                              // dimensions (match your embedding model)
//                 PgDistanceType.COSINE_DISTANCE,    // distance type
//                 false,                             // remove existing vector store table
//                 PgIndexType.HNSW,                  // index type
//                 true                               // initialize schema
//         );
//     }
// }
