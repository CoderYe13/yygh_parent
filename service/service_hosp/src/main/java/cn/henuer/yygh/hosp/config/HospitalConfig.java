package cn.henuer.yygh.hosp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("cn.henuer.yygh.hosp.mapper")
public class HospitalConfig {
}
