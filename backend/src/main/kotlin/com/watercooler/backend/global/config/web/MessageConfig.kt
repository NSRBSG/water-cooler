package com.watercooler.backend.global.config.web

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

@Configuration
class MessageConfig {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()

        messageSource.setBasenames(
            "classpath:/i18n/errors"
        )

        messageSource.setDefaultEncoding("UTF-8")
        messageSource.setCacheSeconds(60)

        return messageSource
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = AcceptHeaderLocaleResolver()

        resolver.setDefaultLocale(Locale.ENGLISH)

        return resolver
    }

}