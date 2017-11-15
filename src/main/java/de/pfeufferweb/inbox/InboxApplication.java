package de.pfeufferweb.inbox;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.io.IOException;
import java.nio.file.FileSystems;

import static java.util.Arrays.asList;

@SpringBootApplication
public class InboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(InboxApplication.class, args);
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().fullyAuthenticated().and().
                httpBasic().and().csrf().disable();
    }
}

@Configuration
class LuceneConfiguration {
    @Bean
    @Profile("file")
    public Directory fileBasedLuceneDirectory(@Value("${lucene.directory}") String directory) throws IOException {
        return new SimpleFSDirectory(FileSystems.getDefault().getPath(directory));

    }

    @Bean
    @Profile("ram")
    public Directory ramLuceneDirectory() {
        return new RAMDirectory();
    }
}

@Configuration
class FileTypeConfiguration {
    @Bean
    public FileTypeChecker supportedFileTypes(@Value("${filetypes}") String filetypes) {
        return new FileTypeChecker(asList(filetypes.toLowerCase().trim().split("\\s*,\\s*")));
    }
}
