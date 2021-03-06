package cn.henuer.yygh.hosp.service;

import cn.henuer.model.hosp.Department;
import cn.henuer.vo.hosp.DepartmentQueryVo;
import cn.henuer.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    //上传科室接口
    void save(Map<String, Object> paramMap);

    //查询科室接口
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    //删除科室接口
    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);

    //根据科室编号，和医院编号，查询科室名称
    Object getDepName(String hoscode, String depcode);
}
