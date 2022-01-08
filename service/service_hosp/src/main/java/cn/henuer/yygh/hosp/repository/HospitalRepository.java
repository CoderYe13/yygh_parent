package cn.henuer.yygh.hosp.repository;

import cn.henuer.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String>{
    //通过hoscode判断是否存在这个数据，这里按照规则写方法，会自动实现
    Hospital getHospitalByHoscode(String hoscode);

}
