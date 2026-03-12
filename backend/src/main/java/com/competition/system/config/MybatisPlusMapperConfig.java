package com.competition.system.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({
        "com.competition.system.user.mapper",
        "com.competition.system.competition.mapper"
})
public class MybatisPlusMapperConfig {
}
