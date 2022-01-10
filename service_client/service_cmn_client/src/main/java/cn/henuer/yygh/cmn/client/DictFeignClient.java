package cn.henuer.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
public interface DictFeignClient {
    //根据dictcode和value查询 dictName
    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    public String getDictName(@PathVariable("dictCode") String dictCode,
                              @PathVariable("value") String value);

    //根据value查询
    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getDictName(@PathVariable("value") String value);

}
