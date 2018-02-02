package zk.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zk.springboot.config.ZKEEApplication;

//import zk.zk.springboot.config.ZKCEApplication;

@SpringBootApplication
//@ZKCEApplication
@ZKEEApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
