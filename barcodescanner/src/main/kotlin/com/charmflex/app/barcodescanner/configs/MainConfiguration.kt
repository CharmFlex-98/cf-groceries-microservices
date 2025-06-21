package com.charmflex.app.barcodescanner.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource


@Configuration
@EnableAspectJAutoProxy
class MainConfiguration {
    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        val template = JdbcTemplate(dataSource)
        return template
    }
}