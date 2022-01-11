package cn.henuer.yygh.hosp.controller;

import cn.henuer.model.hosp.Hospital;
import cn.henuer.vo.hosp.HospitalQueryVo;
import cn.henuer.yygh.common.result.Result;
import cn.henuer.yygh.hosp.service.HospitalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    //医院列表方法
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable("page")Integer page,
                           @PathVariable("limit")Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel= hospitalService.selectHospPage(page,limit,hospitalQueryVo);

        return Result.ok(pageModel);
    }

    //更新医院上线信息
    @ApiOperation("更新医院上线状态")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,
                                   @PathVariable Integer status){
        hospitalService.updataStatus(id,status);
        return Result.ok();
    }
}
