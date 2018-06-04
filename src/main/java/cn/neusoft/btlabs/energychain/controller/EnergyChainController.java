package cn.neusoft.btlabs.energychain.controller;

import cn.neusoft.btlabs.energychain.service.ConsumerService;
import cn.neusoft.btlabs.energychain.service.ProducerService;
import cn.neusoft.btlabs.energychain.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 18:28
 */
@Controller
@RequestMapping("/energyChain")
public class EnergyChainController {
    private final ConsumerService consumerService;
    private final ProducerService producerService;
    private final TransactionService transactionService;

    @Autowired
    public EnergyChainController(ConsumerService consumerService, ProducerService producerService, TransactionService transactionService) {
        this.consumerService = consumerService;
        this.producerService = producerService;
        this.transactionService = transactionService;
    }


    @ResponseBody
    @PostMapping("/consumer.upper")
    public String consumerInvoke(@RequestParam("data") String data) {
        // data={"meter_id":"a09233f5-961e-4ad7-9d63-19d58349bda0","electricity":"999","start_time":"1527211111","end_time":"1527222222","check_sum":"825f28234a176beb142b894425354fcafe7ee199bf41e570dc139cef02f8d639"}
        return consumerService.invoke(data);
    }

    @ResponseBody
    @PostMapping("/consumer.query")
    public String consumerQuery(@RequestParam("data") String data) {
        return consumerService.query(data);
    }

    @ResponseBody
    @PostMapping("/producer.upper")
    public String producerInvoke(@RequestParam("data") String data) {
        // data={"meter_id":"a09233f5-961e-4ad7-9d63-19d58349bda0","electricity":"999","start_time":"1527211111","end_time":"1527222222","check_sum":"825f28234a176beb142b894425354fcafe7ee199bf41e570dc139cef02f8d639"}
        return producerService.invoke(data);

    }

    @ResponseBody
    @PostMapping("/producer.query")
    public String producerQuery(@RequestParam("data") String data) {
        // data={"meter_id":"a09233f5-961e-4ad7-9d63-19d58349bda0","start_time":"1527000000","end_time":"1527999999"}
        return producerService.query(data);
    }

    @ResponseBody
    @PostMapping("/transaction.upper")
    public String transactionInvoke(@RequestParam("data") String data) {
        // data={"company_id":"a09233f5-961e-4ad7-9d63-19d22349bda0","meter_id":"a09233f5-961e-4ad7-9d63-19d583349bda","electricity":"101.1","account":"1011","trading_time":"1527211133","check_sum":"f76002b2f099861891612a9cb9cd23cd4f45697c3afb6a4094f7b194cf800229"}
        return transactionService.invoke(data);

    }

    @ResponseBody
    @PostMapping("/transaction.query")
    public String transactionQuery(@RequestParam("data") String data) {
        // data={"meter_id":"a09233f5-961e-4ad7-9d63-19d58349bda0","start_time":"1527000000","end_time":"1527999999"}
        return transactionService.query(data);
    }
}
