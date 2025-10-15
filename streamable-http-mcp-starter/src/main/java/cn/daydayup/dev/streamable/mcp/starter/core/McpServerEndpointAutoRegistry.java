package cn.daydayup.dev.streamable.mcp.starter.core;

import cn.daydayup.dev.streamable.mcp.starter.annotation.McpFunction;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpParam;
import cn.daydayup.dev.streamable.mcp.starter.annotation.McpServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName McpServerEndpointAutoRegistry
 * @Description McpServerEndpoint 注解提供者
 * @Author ZhaoYanNing
 * @Date 2025/9/30 15:43
 * @Version 1.0
 */
@Slf4j
public class McpServerEndpointAutoRegistry implements BeanPostProcessor, ApplicationContextAware, BeanFactoryAware {

    private ApplicationContext applicationContext;
    private BeanFactory        beanFactory;
    private WebMvcProperties   webMvcProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.webMvcProperties = applicationContext.getBean(WebMvcProperties.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 在 Bean 初始化后处理
     * 检查并收集被 @McpServerEndpoint 注解标注的类信息
     *
     * @param bean     处理的 Bean 实例
     * @param beanName Bean 的名称
     * @return 处理后的 Bean 实例
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // 检查类是否有 McpServerEndpoint 注解
        McpServerEndpoint annotation = beanClass.getAnnotation(McpServerEndpoint.class);
        if (annotation != null) {
            String path = annotation.path();
            try {
                // 从beanClass中获取所有MCP方法
                List<McpFunctionInfo> functionInfos = collectMcpFunctions(beanClass);

                // 手动创建Bean
                BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) beanFactory;
                String handlerBeanName = beanClass.getSimpleName() + "@McpServerEndpoint";

                // 注册McpStreamableHttpHandler
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                        .genericBeanDefinition(McpStreamableHttpHandler.class);
                beanRegistry.registerBeanDefinition(handlerBeanName, beanDefinitionBuilder.getBeanDefinition());

                // 获取创建的Bean并设置functionInfos
                McpStreamableHttpHandler handler = (McpStreamableHttpHandler) applicationContext.getBean(handlerBeanName);
                handler.setName(annotation.name());
                handler.setVersion(annotation.version());
                handler.setTargetBean(bean);
                handler.setFunctionInfos(functionInfos);

                // 注册MCP接口服务
                registerMcpServerEndpoint(path, handler);

                log.info("StreamableHttp Mcp服务接口创建成功, mcpServerEndpoint:{}, functions:{}个", path, functionInfos.size());
            } catch (Exception e) {
                log.error("Mcp服务接口创建失败: {}", e.getMessage(), e);
            }
        }
        return bean;
    }

    /**
     * 获取所有MCP方法
     *
     * @param beanClass 类
     */
    private List<McpFunctionInfo> collectMcpFunctions(Class<?> beanClass) {
        // 获取类中所有被@McpFunction注解标注的方法
        Method[] methods = beanClass.getDeclaredMethods();
        List<McpFunctionInfo> functionInfos = new ArrayList<>();

        for (Method method : methods) {
            McpFunction mcpFunction = method.getAnnotation(McpFunction.class);
            if (mcpFunction != null) {
                // 获取方法的参数列表
                List<McpFunctionInfo.ParamInfo> paramInfos = collectFunctionParamInfos(method);
                // 构建方法信息对象
                McpFunctionInfo functionInfo = new McpFunctionInfo(mcpFunction.name(), mcpFunction.description(), method, paramInfos);
                functionInfos.add(functionInfo);
            }
        }
        return functionInfos;
    }

    /**
     * 获取方法的参数列表
     *
     * @param method 方法
     */
    private static List<McpFunctionInfo.ParamInfo> collectFunctionParamInfos(Method method) {
        Parameter[] parameters = method.getParameters();
        List<McpFunctionInfo.ParamInfo> paramInfos = new ArrayList<>();

        // 遍历所有参数,查找带有McpParam注解的参数
        for (Parameter parameter : parameters) {
            McpParam mcpParam = parameter.getAnnotation(McpParam.class);
            if (mcpParam != null) {
                // 构建参数信息对象
                McpFunctionInfo.ParamInfo paramInfo = new McpFunctionInfo.ParamInfo(
                        mcpParam.name(), mcpParam.description(), mcpParam.enums(), mcpParam.required());
                paramInfos.add(paramInfo);
            }
        }
        return paramInfos;
    }

    /**
     * 注册McpServerEndpoint
     *
     * @param path  路径
     * @param bean  bean
     * @throws NoSuchMethodException 没有找到方法异常
     */
    private void registerMcpServerEndpoint(String path, Object bean) throws NoSuchMethodException {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

        // 根据 WebMvcProperties 选择路径匹配器
        config.setPathMatcher(new AntPathMatcher());
        RequestMappingInfo handleGet = RequestMappingInfo.paths(path).methods(RequestMethod.GET).options(config).build();
        mapping.registerMapping(handleGet, bean, McpStreamableHttpHandler.class.getMethod("handleGet"));

        RequestMappingInfo handlePost = RequestMappingInfo.paths(path).methods(RequestMethod.POST).options(config).build();
        mapping.registerMapping(handlePost, bean, McpStreamableHttpHandler.class.getMethod("handlePost", String.class, HttpServletRequest.class));
    }
}