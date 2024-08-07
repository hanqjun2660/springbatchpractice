package com.example.springbatch.batch;

import com.example.springbatch.entity.BeforeEntity;
import com.example.springbatch.repository.BeforeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.aspectj.lang.annotation.Before;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FifthBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final BeforeRepository beforeRepository;

    @Bean
    public Job fifthJob() {
        System.out.println("fifth job");
        return new JobBuilder("fifthJob", jobRepository)
                .start(fifthStep())
                .build();
    }

    @Bean
    public Step fifthStep() {
        return new StepBuilder("fifthStep", jobRepository)
                .<BeforeEntity, BeforeEntity> chunk(10, platformTransactionManager)
                .reader(fifthBeforeReader())
                .processor(fifthProcessor())
                .writer(excelWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<BeforeEntity> fifthBeforeReader() {

        RepositoryItemReader<BeforeEntity> reader = new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();

        // 전체 데이터 셋에서 어디까지 수행했는지의 값을 지정하지 않음(엑셀 파일로 내보낼때는 새로운 파일이 생성되므로 문제가 생기면 다시 파일을 생성함)
        reader.setSaveState(false);

        return reader;
    }

    @Bean
    public ItemProcessor<BeforeEntity, BeforeEntity> fifthProcessor() {
        return item -> item;
    }

    @Bean
    public ItemStreamWriter<BeforeEntity> excelWriter() {
        return new ExcelRowWriter("C:\\Users\\b2000\\Desktop\\batch_test.xlsx");
    }
}
