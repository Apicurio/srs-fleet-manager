package io.bf2fc6cc711aee1a0c2a.execution.manager.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bf2fc6cc711aee1a0c2a.execution.jobs.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class JobWrapper implements Job {

    @Inject
    ObjectMapper mapper;

    @Inject
    Instance<Worker> workers;

    @Override
    @SneakyThrows
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String taskSerialized = (String) context.getJobDetail().getJobDataMap().get("task");

        Task task = mapper.readValue(taskSerialized, Task.class);

        List<Worker> selectedWorkers = workers.stream().filter(w -> w.supports(task)).collect(Collectors.toList());

        for (Worker worker : selectedWorkers) {
            worker.execute(task);
        }
    }
}
