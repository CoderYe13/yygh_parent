package cn.henuer.yygh.hosp.service.impl;

import cn.henuer.model.hosp.Department;
import cn.henuer.vo.hosp.DepartmentQueryVo;
import cn.henuer.vo.hosp.DepartmentVo;
import cn.henuer.yygh.hosp.repository.DepartmentRepository;
import cn.henuer.yygh.hosp.service.DepartmentService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    //上传科室接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //parmaMap转换成Department对象
        String parmaMapObject = JSONObject.toJSONString(paramMap);
        Department department= JSONObject.parseObject(parmaMapObject,Department.class);
        //通过医院编号和科室编号查询科室信息
        Department departmentExist=departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        //判断科室时候存在
        if (departmentExist!=null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageable对象，设置当前页和每页记录数
        //0是第一页
       Pageable  pageable= PageRequest.of(page-1,limit);

       //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Department department=new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);

        Example<Department> example = Example.of(department, matcher);
        Page<Department> departments =departmentRepository.findAll(example,pageable);
        return departments;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        //先查询判断有没有这个科室
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于最终数据对接
        List<DepartmentVo> result=new ArrayList<>();
        //根据医院列表，查询医院所有科室信息
        Department departmentQuery=new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //得到所有的科室信息 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号 bigcode分组， 获取每个大科室下面的子科室
        Map<String, List<Department>> departmentMap = departmentList.stream()
                .collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合
        for(Map.Entry<String,List<Department>> entry: departmentMap.entrySet()){
            //大科室编号
            String bigcode=entry.getKey();
            //大科室编号，对应的全部数据
            List<Department> department1List=entry.getValue();

            //封装大科室对象
            DepartmentVo department1Vo=new DepartmentVo();
            //设置大科室编号
            department1Vo.setDepcode(bigcode);
            //设置大科室的名
            department1Vo.setDepname(department1List.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> childrenDeptList=new ArrayList<>();
            /**
             * collect(Collectors.toList());stream需要加上终止操作，要不然其中的内部操作无效
             */
            department1List.stream().map((item)->{
                DepartmentVo childDept=new DepartmentVo();
                childDept.setDepcode(item.getDepcode());
                childDept.setDepname(item.getDepname());
                //封装到children小科室集合中
                childrenDeptList.add(childDept);
                return childDept;
            }).collect(Collectors.toList());
            department1Vo.setChildren(childrenDeptList);
            //放到最终的result集合这两个
            result.add(department1Vo);
        }
        return result;
    }

    @Override
    public Object getDepName(String hoscode, String depcode) {
        Department department =departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);
        if(department!=null){
            return department.getDepname();
        }
        return null;
    }
}
