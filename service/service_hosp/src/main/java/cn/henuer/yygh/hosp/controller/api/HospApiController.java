package cn.henuer.yygh.hosp.controller.api;

import cn.henuer.model.hosp.Hospital;
import cn.henuer.vo.hosp.DepartmentVo;
import cn.henuer.vo.hosp.HospitalQueryVo;
import cn.henuer.yygh.common.result.Result;
import cn.henuer.yygh.hosp.service.DepartmentService;
import cn.henuer.yygh.hosp.service.HospitalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation("查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }

    @ApiOperation("根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findHospList(@PathVariable String hosname){
         List<Hospital> hospList= hospitalService.findByHosname(hosname);
        return Result.ok(hospList);
    }

    @ApiOperation("根据医院编号获取科室列表")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }

    @ApiOperation("根据医院编号获取医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
       Map<String,Object> map= hospitalService.item(hoscode);
        return Result.ok(map);
    }
}
