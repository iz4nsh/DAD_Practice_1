package es.codeurjc.helloworld_spring;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component	
public class DiskRequestListener {

	private final DiskRepository repository;
	private final RabbitTemplate rabbitTemplate;
	
	public DiskRequestListener(DiskRepository repository, RabbitTemplate rabbitTemplate) {
		super();
		this.repository = repository;
		this.rabbitTemplate = rabbitTemplate;
	}
	
	@RabbitListener(queues = "disk-requests")
	public void handle(DiskRequestDto dto) {
		Disk disk = new Disk(dto.getSize(), Disk.diskType.valueOf(dto.getType().toUpperCase()), Disk.diskStatus.REQUESTED);
		disk = repository.save(disk);
		sendStatus(disk);
		final Disk[] finalDisk = new Disk[] { disk };
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            finalDisk[0].setStatus(Disk.diskStatus.INITIALIZING);
            repository.save(finalDisk[0]);
            sendStatus(finalDisk[0]); 
        }, 5, TimeUnit.SECONDS);

        executor.schedule(() -> {
        	finalDisk[0].setStatus(Disk.diskStatus.ASSIGNED);
            repository.save(finalDisk[0]);
            sendStatus(finalDisk[0]);
        }, 15, TimeUnit.SECONDS);
	}
	
	private void sendStatus(Disk disk) {
        DiskStatusDto msg = new DiskStatusDto(
                disk.getId(),
                disk.getSize(),
                disk.getType().name(),
                disk.getStatus().name()
        );
        rabbitTemplate.convertAndSend("disk-statuses", msg);
    }
}
