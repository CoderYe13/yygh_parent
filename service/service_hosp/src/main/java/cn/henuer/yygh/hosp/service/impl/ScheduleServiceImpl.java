package cn.henuer.yygh.hosp.service.impl;

import cn.henuer.model.hosp.Schedule;
import cn.henuer.vo.hosp.BookingScheduleRuleVo;
import cn.henuer.vo.hosp.ScheduleQueryVo;
import cn.henuer.yygh.hosp.repository.ScheduleRepository;
import cn.henuer.yygh.hosp.service.DepartmentService;
import cn.henuer.yygh.hosp.service.HospitalService;
import cn.henuer.yygh.hosp.service.ScheduleService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    //上传排班接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //parmaMap转换成Department对象
        String parmaMapObject = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(parmaMapObject, Schedule.class);
        //通过医院编号和科室编号查询科室信息
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        //判断科室时候存在
        if (scheduleExist != null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
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
        Pageable pageable = PageRequest.of(page - 1, limit);

        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);

        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> schedules = scheduleRepository.findAll(example, pageable);
        return schedules;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //先查询判断有没有这个科室
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    //根据医院编号和科室编号，查询排班规则数据
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {

        //1、根据医院编号和科室编号 查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //2、根据工作日期workDate 进行分组
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段，按照workDate分组
                        .first("workDate").as("workDate")
                        //3、统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //4、实现分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        //调用方法最终去执行
        AggregationResults<BookingScheduleRuleVo> aggResult = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);

        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResult.getMappedResults();
        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        //把日期转换成星期几
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String week = this.getWeekOfDate(workDate);
            bookingScheduleRuleVo.setDayOfWeek(week);
        }
        //设置最终的数据，并返回
        HashMap<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleVoList", bookingScheduleRuleVoList);
        result.put("total", total);

        //获取医院名称
        String hosname = hospitalService.getHospName(hoscode);
        Map<String, Object> baseMap = new HashMap<>();
        baseMap.put("hosname", hosname);
        result.put("baseMap", baseMap);

        return result;
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime
     * @return
     */
    public String getWeekOfDate(Date dateTime) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    //根据医院编号，科室编号和工作日期，查询排班详细信息
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        try {
            List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new SimpleDateFormat("yyyy-MM-dd").parse(workDate));

            //把得到list集合遍历，设置其他值，医院名称，科室名称，日期对应星期几
            scheduleList.stream().forEach(item->{
                this.packageSchedule(item);
            });
            return scheduleList;
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return null;
    }

    private void packageSchedule(Schedule schedule){
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应的星期
        schedule.getParam().put("dayOfWeek",this.getWeekOfDate(schedule.getWorkDate()));
    }
}
