package cn.henuer.yygh.hosp.service;

import cn.henuer.model.hosp.Department;
import cn.henuer.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {
    //上传科室接口
    void save(Map<String, Object> paramMap);

    //查询科室接口
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);
    //删除科室接口
    void remove(String hoscode, String depcode);
}
