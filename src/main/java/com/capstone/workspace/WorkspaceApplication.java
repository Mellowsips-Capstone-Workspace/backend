package com.capstone.workspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class WorkspaceApplication {
	public static void main(String[] args) {
		SpringApplication.run(WorkspaceApplication.class, args);
	}
}
