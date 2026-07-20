package com.guleryigitcan.WalletManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
		RedisAutoConfiguration.class,
		RabbitAutoConfiguration.class
})
@EnableCaching
public class WalletManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletManagementApplication.class, args);
	}

}
