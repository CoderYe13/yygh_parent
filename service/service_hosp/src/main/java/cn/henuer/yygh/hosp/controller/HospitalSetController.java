package cn.henuer.yygh.hosp.controller;

import cn.henuer.model.hosp.HospitalSet;
import cn.henuer.vo.hosp.HospitalSetQueryVo;
import cn.henuer.yygh.common.result.Result;
import cn.henuer.yygh.hosp.commom.utils.MD5;
import cn.henuer.yygh.hosp.service.HospitalSetService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;

@Api("医院管理系统")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询医院设置表中所有数据
     */
    @ApiOperation("获取所有医院设置")
    @GetMapping("findAll")
    public Result findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        Result<List<HospitalSet>> ok = Result.ok(list);
        return ok;
    }

    @ApiOperation("逻辑删除医院设置信息")
    @DeleteMapping("{id}")
    public Result removeHospital(@PathVariable("id") Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospital(@PathVariable("current") Long current,
                                   @PathVariable("limit") Long limit,
                                   @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo,
                                   HttpServletResponse response) {
        if (current>0&&limit>0){
            //创建page对象，传递当前页，和每页记录数
            Page<HospitalSet> page = new Page<>(current, limit);
            QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
            String hosname = hospitalSetQueryVo.getHosname();
            String hoscode = hospitalSetQueryVo.getHoscode();
            //条件判空
            if (!StringUtils.isEmpty(hosname)) {
                wrapper.like("hosname", hospitalSetQueryVo.getHosname());
            }

            if (hoscode != null && !hoscode.equals("")) {
                wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
            }
            //调用方法实现分页查询
            Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
            response.addHeader("henu", "henu");
            return Result.ok(hospitalSetPage);
        }else {
            return Result.fail("请输入正确参数");
        }

    }

    //添加医院设置
    @ApiOperation("添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        //设置状态1使用 0不能使用
        hospitalSet.setStatus(1);

        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        boolean save = hospitalSetService.save(hospitalSet);

        if (save) {
            return Result.ok();
        }
        return Result.fail();
    }

    //添加医院设置
    @ApiOperation("根据id获取医院设置信息")
    @PostMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable("{id}") Long id) {
        if (id > 0) {
            HospitalSet hospitalSet = hospitalSetService.getById(id);
            return Result.ok(hospitalSet);
        } else {
            return Result.fail("传入id不合法");
        }
    }

    //添加医院设置
    @ApiOperation("根据id修改医院设置信息")
    @PostMapping("updateHospitalSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet) {
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update) {
            return Result.ok();
        }
        return Result.fail();
    }

    //添加医院设置
    @ApiOperation("根据id修改医院设置信息")
    @PostMapping("batchRemove")
    public Result batchRemoveHospSet(@RequestBody List<Long> ids) {
        boolean flag = hospitalSetService.removeByIds(ids);
        if (flag){
           return Result.ok();
        }
       return Result.fail();
    }

    //添加医院设置
    @ApiOperation("医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable("id") Long id,
                                 @PathVariable("status")Integer status ) {
        //根据id获取医院设置
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //修改状态
        hospitalSetService.updateById(hospitalSet);

        return Result.ok();
    }
    @ApiOperation("医院设置锁定和解锁")
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable("id") Long id) {

        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO
        return Result.ok();
    }
}
