package cn.henuer.yygh.hosp.service.impl;

import cn.henuer.model.hosp.Hospital;
import cn.henuer.yygh.hosp.repository.HospitalRepository;
import cn.henuer.yygh.hosp.service.HospitalService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    //上传医院信息到mongo
    public void save(Map<String, Object> paramMap) {
        //把参数map转换成hospital对象
        String mapString= JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //先判断是否存在相同的数据
        String hoscode=hospital.getHoscode();
        Hospital hospitalExist=hospitalRepository.getHospitalByHoscode(hoscode);
        //如果不存在，进行添加
        if(hospitalExist!=null){
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setIsDeleted(0);
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
        else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
        //如果存在
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);

        return hospital;
    }
}
