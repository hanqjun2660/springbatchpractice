package com.example.springbatch.batch;

import com.example.springbatch.entity.AfterEntity;
import com.example.springbatch.repository.AfterRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class FourthBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final AfterRepository afterRepository;

    @Bean
    public Job fourthJob() {
        System.out.println("fourth Job");

        return new JobBuilder("fourthJob", jobRepository)
                .start(fourthStep())
                .build();
    }

    @Bean
    public Step fourthStep() {
        return new StepBuilder("fourthStep", jobRepository)
                .<Row, AfterEntity> chunk(10, platformTransactionManager)
                .reader(excelReader())
                .processor(fourthProcessor())
                .writer(fourthAfterWriter())
                .build();
    }

    @Bean
    public ItemStreamReader<Row> excelReader() {
        try {
            return new ExcelRowReader("C:\\Users\\b2000\\Desktop\\yummi.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public ItemProcessor<Row, AfterEntity> fourthProcessor() {
        return new ItemProcessor<Row, AfterEntity>() {
            @Override
            public AfterEntity process(Row item) throws Exception {
                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUsername(item.getCell(0).getStringCellValue());
                return afterEntity;
            }
        };
    }

    @Bean
    public RepositoryItemWriter<AfterEntity> fourthAfterWriter() {

        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
