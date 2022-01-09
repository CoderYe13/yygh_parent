package cn.henuer.yygh.hosp.repository;

import cn.henuer.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    //查询排班
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

}
