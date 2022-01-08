package cn.henuer.yygh.hosp.service;

import cn.henuer.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    //上传医院信息到mongo
    void save(Map<String, Object> paramMap);
    //实现根据医院编号查询
    Hospital getByHoscode(String hoscode);
}
