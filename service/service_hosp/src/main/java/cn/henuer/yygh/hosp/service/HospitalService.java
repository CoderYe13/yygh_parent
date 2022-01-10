package cn.henuer.yygh.hosp.service;

import cn.henuer.model.hosp.Hospital;
import cn.henuer.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalService {
    //上传医院信息到mongo
    void save(Map<String, Object> paramMap);
    //实现根据医院编号查询
    Hospital getByHoscode(String hoscode);
    //医院列表方法
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
}
