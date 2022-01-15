package cn.henuer.yygh.hosp.service;

import cn.henuer.model.hosp.Schedule;
import cn.henuer.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);
    //查询排班接口
    Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo scheduleQueryVo);
    void remove(String hoscode, String hosScheduleId);
    //根据医院编号和科室编号，查询排班规则数据
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);
    //根据医院编号，科室编号和工作日期，查询排班详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);
}
