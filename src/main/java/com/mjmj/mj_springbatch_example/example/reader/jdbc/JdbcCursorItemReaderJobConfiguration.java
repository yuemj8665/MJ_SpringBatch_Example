package com.mjmj.mj_springbatch_example.example.reader.jdbc;

import com.mjmj.mj_springbatch_example.entity.Pay.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcCursorItemReaderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource; // DataSource DI

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcCursorItemReaderJob() {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
                .start(jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")
                .<Pay, Pay>chunk(chunkSize)
                // 첫번쨰 Pay는 Reader에서 반환 할 타입
                // 두번쨰 Pay는 Writer에 파라미터로 넘어 올 타입
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Pay> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Pay>()
                .fetchSize(chunkSize) // Database에서 한번에 가져올 데이터 양
                .dataSource(dataSource) // Database에 접근하기 위해 사용 할 Datasource객체 할당
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                // 쿼리 결과를 자바 인스턴스로 맵핑하기 위한 맵퍼
                // 보통은 BeanPropertyRowMapper 이거 씀
                .sql("SELECT id, amount, tx_name, tx_date_time FROM pay") // Reader에 사용 할 쿼리문
                .name("jdbcCursorItemReader") // Reader의 이름. ExcutionContext에 저장됨.
                .build();
    }

    private ItemWriter<Pay> jdbcCursorItemWriter() {
        return list -> {
            for (Pay pay: list) {
                log.info("Current Pay={}", pay);
            }
        };
    }
}