package cn.henuer.yygh.hosp.service.impl;

import cn.henuer.model.hosp.Hospital;
import cn.henuer.vo.hosp.HospitalQueryVo;
import cn.henuer.yygh.cmn.client.DictFeignClient;
import cn.henuer.yygh.hosp.repository.HospitalRepository;
import cn.henuer.yygh.hosp.service.HospitalService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    //上传医院信息到mongo
    @Override
    public void save(Map<String, Object> paramMap) {
        //把参数map转换成hospital对象
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //先判断是否存在相同的数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);
        //如果不存在，进行添加
        if (hospitalExist != null) {
            hospitalExist.setStatus(hospitalExist.getStatus());
            hospitalExist.setIsDeleted(0);
            hospitalExist.setCreateTime(hospitalExist.getCreateTime());
            hospitalExist.setUpdateTime(new Date());
            hospitalRepository.save(hospitalExist);
        } else {
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

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建Pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //hospitalSetQueryVo转换为Hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        //创建对象
        Example<Hospital> example = Example.of(hospital, matcher);
        //调用方法实现mongodb查询
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        //获取pages中的所有hospital => content
        List<Hospital> content = pages.getContent();
        content.stream().forEach((item) -> {
            this.setHospitalHosType(item);
        });
        return pages;
    }


    //获取查询list集合，遍历进行医院等级封装在parma参数中
    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictcode和value获取医院等级信息
        String hostypeString = dictFeignClient.getDictName("Hostype", hospital.getHostype());
        //查询所在的省市地区
        String provinceString = dictFeignClient.getDictName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getDictName(hospital.getCityCode());
        String districtString = dictFeignClient.getDictName(hospital.getDistrictCode());


        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress",provinceString+cityString+districtString);
        return hospital;
    }

    //更新医院上线信息
    @Override
    public void updataStatus(String id, Integer status) {
        //根据id查询医院信息
        Hospital hospital=hospitalRepository.findById(id).get();

        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        //设置医院状态值
        hospitalRepository.save(hospital);
    }
}
