package com.cqupt.art.order.config.mq;

import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RabbitMqConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 注意消息 json转换器
     *
     * @return
     */
    @Configuration
    static class Converter {
        @Bean
        public MessageConverter messageConverter() {
//            //发送端和接收端的路径不一样，导致类转化失败，在接收端配置解决（也可在发送端解决）
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//            Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
//            converter.setClassMapper(new ClassMapper() {
//                @Override
//                public void fromClass(Class<?> aClass, MessageProperties messageProperties) {
//                    //发送端这么写
////                     messageProperties.setHeader("__TypeId__","com.cqupt.art.order.entity.to.SeckillOrderTo");
//                    messageProperties.setHeader("__TypeId__","com.cqupt.art.chain.entity.to.ChainTransferTo.java");
//                }
//
//                @Override
//                public Class<?> toClass(MessageProperties messageProperties) {
//                    return SeckillOrderTo.class;
//                }
//            });
//            return converter;
            return new Jackson2JsonMessageConverter();
        }
    }



    /**
     * 定制RabbitTemplate
     * 1、服务收到消息就会回调
     * 1、spring.rabbitmq.publisher-confirms: true
     * 2、设置确认回调
     * 2、消息正确抵达队列就会进行回调
     * 1、spring.rabbitmq.publisher-returns: true
     * spring.rabbitmq.template.mandatory: true
     * 2、设置确认回调ReturnCallback
     * <p>
     * 3、消费端确认(保证每个消息都被正确消费，此时才可以在队列删除这个消息)
     */
    @PostConstruct
    public void initRabbitTemplate() {
        /**
         * 返回通知
         * 1、只要消息抵达服务器交换机就ack=true
         * correlationData：当前消息的唯一关联数据(拥有唯一id)
         * ack：消息是否成功被交换机接收
         * cause：失败的原因
         */
        //设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("confirm...correlationData[" + correlationData + "]==>ack:[" + ack + "]==>cause:[" + cause + "]");
        });


        /**
         *  异常通知
         * 只要消息没有路由到队列，就触发这个失败回调
         * message：投递失败的消息详细信息
         * replyCode：回复的状态码
         * replyText：回复的文本内容
         * exchange：当时这个消息发给哪个交换机
         * routingKey：当时这个消息用哪个路邮键
         */
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("Fail Message[" + message + "]==>replyCode[" + replyCode + "]" +
                    "==>replyText[" + replyText + "]==>exchange[" + exchange + "]==>routingKey[" + routingKey + "]");
        });
    }


}
