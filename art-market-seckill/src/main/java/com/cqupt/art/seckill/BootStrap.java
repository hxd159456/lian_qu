//package com.cqupt.art.seckill;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.boot.*;
//import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.util.Assert;
//import org.springframework.util.StopWatch;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Set;
//
//import static jdk.nashorn.internal.objects.Global.load;
//
//public class BootStrap {
//    public ConfigurableApplicationContext run(String... args) {
//        // 开启关于启动时间的信息监控
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        // 准备 ApplicationContext
//        ConfigurableApplicationContext context = null;
//        Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
//        configureHeadlessProperty();
//        // 1. 获取Spring的监听器类，这里是从 spring.factories 中去获取，
//        // 默认的是以 org.springframework.boot.SpringApplicationRunListener 为key,
//        // 获取到的监听器类型为 EventPublishingRunListener。
//        SpringApplicationRunListeners listeners = getRunListeners(args);
//        // 1.1 监听器发送启动事件
//        listeners.starting();
//        try {
//            // 封装参数
//            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
//            // 2. 构造容器环境。将容器的一些配置内容加载到 environment 中
//            ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
//            // 配置BeanInfo的忽略 ：“spring.beaninfo.ignore”，值为“true”表示跳过对BeanInfo类的搜索
//            configureIgnoreBeanInfo(environment);
//            // 打印信息对象
//            Banner printedBanner = printBanner(environment);
//            // 3. 创建上下文对象
//            context = createApplicationContext();
//            // 从 spring.factries 中获取错误报告的类。出错的时候会调用其方法通知
//            exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
//                    new Class[] { ConfigurableApplicationContext.class }, context);
//            // 4. 准备刷新上下文
//            prepareContext(context, environment, listeners, applicationArguments, printedBanner);
//            // 5. 刷新上下文
//            refreshContext(context);
//            // 结束刷新，留待扩展功能，并未实现什么
//            afterRefresh(context, applicationArguments);
//            // 停止监听
//            stopWatch.stop();
//            if (this.logStartupInfo) {
//                new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
//            }
//            // 监听器发送启动结束时间
//            listeners.started(context);
//            // 监听器发送启动结束时间
//            callRunners(context, applicationArguments);
//        }
//        catch (Throwable ex) {
//            handleRunFailure(context, ex, exceptionReporters, listeners);
//            throw new IllegalStateException(ex);
//        }
//
//        try {
//            // 发送容器运行事件
//            listeners.running(context);
//        }
//        catch (Throwable ex) {
//            handleRunFailure(context, ex, exceptionReporters, null);
//            throw new IllegalStateException(ex);
//        }
//        return context;
//    }
//
//
//    private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners,
//                                                       ApplicationArguments applicationArguments) {
//        // Create and configure the environment
//        // 获取或者创建 environment。这里获取类型是 StandardServletEnvironment
//        ConfigurableEnvironment environment = getOrCreateEnvironment();
//        // 将入参配置到环境配置中（即启动类的args）
//        configureEnvironment(environment, applicationArguments.getSourceArgs());
//        ConfigurationPropertySources.attach(environment);
//        // 发布环境准备事件。 ConfigFileApplicationListener#onApplicationEnvironmentPreparedEvent——>ConfigFileApplicationListener#postProcessEnvironmen加载配置文件
//        listeners.environmentPrepared(environment);
//        bindToSpringApplication(environment);
//        if (!this.isCustomEnvironment) {
//            environment = new EnvironmentConverter(getClassLoader()).convertEnvironmentIfNecessary(environment,
//                    deduceEnvironmentClass());
//        }
//        ConfigurationPropertySources.attach(environment);
//        return environment;
//    }
//
//    protected ConfigurableApplicationContext createApplicationContext() {
//        Class<?> contextClass = this.applicationContextClass;
//        if (contextClass == null) {
//            try {
//                switch (this.webApplicationType) {
//                    case SERVLET:
//                        contextClass = Class.forName(DEFAULT_SERVLET_WEB_CONTEXT_CLASS);
//                        break;
//                    case REACTIVE:
//                        contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
//                        break;
//                    default:
//                        contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
//                }
//            }
//            catch (ClassNotFoundException ex) {
//                throw new IllegalStateException(
//                        "Unable create a default ApplicationContext, please specify an ApplicationContextClass", ex);
//            }
//        }
//        return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
//    }
//
//    private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
//                                SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
//        // 设置上下文的环境变量
//        context.setEnvironment(environment);
//        // 执行容器后置处理 ： 可以注册beanName策略生成器、设置资源加载器，设置转换服务等。但这里默认是没有做任何处理。目的是留给后续可以扩展
//        postProcessApplicationContext(context);
//        // 处理所有的初始化类的初始化方法。即 spring.factories 中key 为 org.springframework.context.ApplicationContextInitializer 指向的类，调用其 initialize 方法
//        applyInitializers(context);
//        // 向监听器发送容器准备事件
//        listeners.contextPrepared(context);
//        if (this.logStartupInfo) {
//            logStartupInfo(context.getParent() == null);
//            logStartupProfileInfo(context);
//        }
//        // Add boot specific singleton beans
//        // 获取上下文中的 BeanFactory。这里的BeanFactory 实际类型是  DefaultListableBeanFactory。BeanFactory 在初始化的时候，直接在构造函数里创建为 DefaultListableBeanFactory
//        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
//        // 注册 springApplicationArguments等一系列bean
//        beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
//        if (printedBanner != null) {
//            beanFactory.registerSingleton("springBootBanner", printedBanner);
//        }
//        if (beanFactory instanceof DefaultListableBeanFactory) {
//            // 设置是否允许bean定义覆盖
//            ((DefaultListableBeanFactory) beanFactory)
//                    .setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
//        }
//        // 如果允许懒加载，则添加对应的BeanFactory后置处理器
//        if (this.lazyInitialization) {
//            context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
//        }
//        // Load the sources
//        // 这里加载的实际上是启动类
//        Set<Object> sources = getAllSources();
//        Assert.notEmpty(sources, "Sources must not be empty");
//        // 这里将启动类加入到 beanDefinitionMap 中，为后续的自动化配置做好了基础
//        load(context, sources.toArray(new Object[0]));
//        // 发送容器加载完成事件
//        listeners.contextLoaded(context);
//    }
//
//    // 需要注意这里的 sources参数实际上是 启动类的 Class
//    protected void load(ApplicationContext context, Object[] sources) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("Loading source " + StringUtils.arrayToCommaDelimitedString(sources));
//        }
//        // 从上下文中获取 BeanDefinitionRegistry并依次创建出 BeanDefinitionLoader 。这里将sources作为参数保存到了 loader  中。也就是 loader  中保存了 启动类的Class信息
//        BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
//        if (this.beanNameGenerator != null) {
//            loader.setBeanNameGenerator(this.beanNameGenerator);
//        }
//        if (this.resourceLoader != null) {
//            loader.setResourceLoader(this.resourceLoader);
//        }
//        if (this.environment != null) {
//            loader.setEnvironment(this.environment);
//        }
//        loader.load();
//    }
//
//
//}
