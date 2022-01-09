package cn.henuer.yygh.hosp.service.impl;

import cn.henuer.model.hosp.Department;
import cn.henuer.model.hosp.Schedule;
import cn.henuer.vo.hosp.ScheduleQueryVo;
import cn.henuer.yygh.hosp.repository.ScheduleRepository;
import cn.henuer.yygh.hosp.service.ScheduleService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    //上传排班接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //parmaMap转换成Department对象
        String parmaMapObject = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(parmaMapObject,Schedule.class);
        //通过医院编号和科室编号查询科室信息
        Schedule scheduleExist=scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());
        //判断科室时候存在
        if (scheduleExist!=null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }else{
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //创建Pageable对象，设置当前页和每页记录数
        //0是第一页
        Pageable pageable= PageRequest.of(page-1,limit);

        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Schedule schedule=new Schedule ();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);


        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> schedules =scheduleRepository.findAll(example,pageable);
        return schedules;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //先查询判断有没有这个科室
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule!=null){
            scheduleRepository.deleteById(schedule.getId());
        }

    }
}
