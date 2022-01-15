package cn.henuer.yygh.hosp.controller;

import cn.henuer.model.hosp.Schedule;
import cn.henuer.yygh.common.result.Result;
import cn.henuer.yygh.hosp.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
@Api("排班接口")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    //根据医院编号和科室编号，查询排班规则数据
    @ApiOperation(value = "查询排班规则接口")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable long page,
                                  @PathVariable long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode) {
        Map<String, Object> map = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    //根据医院编号，科室编号和工作日期，查询排班详细信息
    @ApiOperation("查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> list=scheduleService.getDetailSchedule(hoscode,depcode,workDate);
        return Result.ok(list);
    }
}
