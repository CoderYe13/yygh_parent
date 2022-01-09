package cn.henuer.yygh.hosp.service;

import cn.henuer.model.hosp.Schedule;
import cn.henuer.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);
    //查询排班接口
    Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo scheduleQueryVo);
    void remove(String hoscode, String hosScheduleId);
}
