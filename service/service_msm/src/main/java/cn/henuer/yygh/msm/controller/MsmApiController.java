package cn.henuer.yygh.msm.controller;

import cn.henuer.yygh.common.result.Result;
import cn.henuer.yygh.msm.service.MsmService;
import cn.henuer.yygh.msm.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //发送手机验证码
    @ApiOperation("发送短信服到redis")
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) {
        //从redis获取验证码，如果获取到，返回OK
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return Result.ok();
        }

        //如果从redis获取不到，生成验证码，
        // 通过整合短信服务进行发送，生成验证码到redis，设置有效时间
        code = RandomUtil.getFourBitRandom();
        //这里是阿里云短信服务，个人无法实现，这里只用本地模拟，不直接使用
        //boolean isSend = msmService.send(phone, code);
        boolean isSend=true;
        if (isSend) {
            //验证码添加到redis
            redisTemplate.opsForValue().set(phone, code, 2, TimeUnit.MINUTES);
            System.out.println("手机号："+phone+"   验证码："+code);
            return Result.ok();
        }else {
            return Result.fail().message("发送短信失败");
        }

    }
}
