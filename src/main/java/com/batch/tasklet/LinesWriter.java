package com.batch.tasklet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.batch.db.Record;

public class LinesWriter implements Tasklet, StepExecutionListener {
	 
    private final Logger logger = LoggerFactory
      .getLogger(LinesWriter.class);
 
    private List<Record> records;
    private FileUtils fu;
 
    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
          .getJobExecution()
          .getExecutionContext();
        this.records = (List<Record>) executionContext.get("records");
        fu = new FileUtils("output.csv");
        logger.debug("Lines Writer initialized.");
    }
 
    @Override
    public RepeatStatus execute(StepContribution stepContribution, 
      ChunkContext chunkContext) throws Exception {
        for (Record record : records) {
           // fu.writeLine(record);
            logger.debug("Wrote line " + record.toString());
        }
        return RepeatStatus.FINISHED;
    }
 
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        fu.closeWriter();
        logger.debug("Lines Writer ended.");
        return ExitStatus.COMPLETED;
    }
}