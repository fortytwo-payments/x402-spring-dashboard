package io.x402.dashboard.config;

import io.x402.dashboard.logging.X402UsageLogger;
import io.x402.dashboard.repository.X402UsageEventRepository;
import io.x402.dashboard.service.X402UsageAggregationService;
import io.x402.dashboard.service.X402UsageEventService;
import io.x402.dashboard.web.X402ClientLoggingInterceptor;
import io.x402.dashboard.web.X402DashboardController;
import io.x402.dashboard.web.X402DashboardRestController;
import io.x402.dashboard.web.X402UsageLoggingInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration for x402 Dashboard.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@EnableConfigurationProperties(X402DashboardProperties.class)
@EnableJpaRepositories(basePackages = {"io.x402.dashboard.repository", "io.x402.dashboard.buyer.repository"})
@EntityScan(basePackages = {"io.x402.dashboard.domain", "io.x402.dashboard.buyer.domain"})
@ComponentScan(basePackages = "io.x402.dashboard")
@EnableAspectJAutoProxy
public class X402DashboardAutoConfiguration implements WebMvcConfigurer {

    private final X402DashboardProperties properties;

    public X402DashboardAutoConfiguration(X402DashboardProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public X402UsageEventService x402UsageEventService(X402UsageEventRepository repo) {
        return new X402UsageEventService(repo);
    }

    @Bean
    @ConditionalOnMissingBean
    public X402UsageAggregationService x402UsageAggregationService(X402UsageEventRepository repo) {
        return new X402UsageAggregationService(repo);
    }

    @Bean
    @ConditionalOnMissingBean
    public X402DashboardController x402DashboardController(
            X402UsageAggregationService aggregationService,
            X402UsageEventService eventService) {
        return new X402DashboardController(aggregationService, eventService, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public X402DashboardRestController x402DashboardRestController(
            X402UsageAggregationService aggregationService,
            X402UsageEventService eventService) {
        return new X402DashboardRestController(aggregationService, eventService, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public X402UsageLogger x402UsageLogger(X402UsageEventService eventService) {
        return new X402UsageLogger(eventService);
    }

    @Bean
    @ConditionalOnMissingBean
    public X402UsageLoggingInterceptor x402UsageLoggingInterceptor(X402UsageLogger logger) {
        return new X402UsageLoggingInterceptor(logger, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public X402ClientLoggingInterceptor x402ClientLoggingInterceptor(X402UsageLogger logger) {
        return new X402ClientLoggingInterceptor(logger, properties);
    }

    /**
     * Customizer to automatically add X402ClientLoggingInterceptor to all RestTemplate beans
     * when client-auto-logging is enabled.
     */
    @Bean
    @ConditionalOnClass(RestTemplate.class)
    @ConditionalOnProperty(prefix = "x402.dashboard", name = "enable-client-auto-logging", havingValue = "true")
    public RestTemplateCustomizer x402RestTemplateCustomizer(X402ClientLoggingInterceptor interceptor) {
        return restTemplate -> restTemplate.getInterceptors().add(interceptor);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (properties.isEnableAutoLogging()) {
            registry.addInterceptor(x402UsageLoggingInterceptor(null))
                    .addPathPatterns("/**")
                    .excludePathPatterns(
                            properties.getPath() + "/**",
                            properties.getApiPath() + "/**",
                            "/h2-console/**",
                            "/error"
                    );
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(properties.getPath() + "/static/**")
                .addResourceLocations("classpath:/static/x402-dashboard/");
    }
}
