package cn.henuer.yygh.hosp.controller.api;

import cn.henuer.model.hosp.Department;
import cn.henuer.model.hosp.Hospital;
import cn.henuer.model.hosp.Schedule;
import cn.henuer.vo.hosp.DepartmentQueryVo;
import cn.henuer.vo.hosp.ScheduleQueryVo;
import cn.henuer.yygh.common.exception.HospitalException;
import cn.henuer.yygh.common.result.Result;
import cn.henuer.yygh.common.result.ResultCodeEnum;
import cn.henuer.yygh.hosp.commom.helper.HttpRequestHelper;
import cn.henuer.yygh.hosp.commom.utils.MD5;
import cn.henuer.yygh.hosp.service.DepartmentService;
import cn.henuer.yygh.hosp.service.HospitalService;
import cn.henuer.yygh.hosp.service.HospitalSetService;
import cn.henuer.yygh.hosp.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取到传递过来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap= HttpRequestHelper.switchMap(requestMap);

        //医院编号和科室编号
        String hoscode= (String) paramMap.get("hoscode");
        String depcode=(String) paramMap.get("depcode");

        //TODO校验签名
        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }

    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取到传递过来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap= HttpRequestHelper.switchMap(requestMap);

        //1、获取医院编号
        String hoscode = (String)paramMap.get("hoscode");
        //当前页码和每页的记录数
        int page =StringUtils.isEmpty((String)paramMap.get("page"))?1:
               Integer.parseInt((String) paramMap.get("page"));
        int limit =StringUtils.isEmpty((String)paramMap.get("limit"))?1:
                Integer.parseInt((String) paramMap.get("limit"));
        //TODO签名校验

        DepartmentQueryVo departmentQueryVo=new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        //调用service通过page-1,limit,departmentQueryVo
       Page<Department> pageModel=departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
    }

    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取到传递过来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap= HttpRequestHelper.switchMap(requestMap);

        //1、获取医院系统接口传递过来的签名，签名进行MD5加密
        String hospSign=(String) paramMap.get("sign");
        //2、根据传递过来的医院编码，查询数据库，查询签名
        String hoscode=(String)paramMap.get("hoscode");
        String signKey=hospitalSetService.getSignKey(hoscode);

        //3、吧数据库查询签名进行MD5加密
        String signKeyMd5=MD5.encrypt(signKey);

        //4、判断签名是否一致
        if(!hospSign.equals(signKeyMd5)){
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service的方法
        departmentService.save(paramMap);

        return Result.ok();
    }


    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取到传递过来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap= HttpRequestHelper.switchMap(requestMap);

        //1、获取医院系统接口传递过来的签名，签名进行MD5加密
        String hospSign=(String) paramMap.get("sign");
        //2、根据传递过来的医院编码，查询数据库，查询签名
        String hoscode=(String)paramMap.get("hoscode");
        String signKey=hospitalSetService.getSignKey(hoscode);

        //3、吧数据库查询签名进行MD5加密
        String signKeyMd5=MD5.encrypt(signKey);

        //4、判断签名是否一致
        if(!hospSign.equals(signKeyMd5)){
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }

        //传输过程中"+"转换成了" ",这里我们要转换回来
        String logoData=(String) paramMap.get("logoData");
        logoData=logoData.replaceAll(" ","+");

        paramMap.put("logoData",logoData);

        //签名一致调用service方法save
        hospitalService.save(paramMap);
        return Result.ok();
    }



    //查询医院
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        //获取传递过来医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode = (String)paramMap.get("hoscode");
        //1 获取医院系统传递过来的签名,签名进行MD5加密
        String hospSign = (String)paramMap.get("sign");

        //2 根据传递过来医院编码，查询数据库，查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        //3 把数据库查询签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);

        //4 判断签名是否一致
        if(!hospSign.equals(signKeyMd5)) {
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用service方法实现根据医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }
    //上传排班接口
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        //获取传递过来医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode = (String)paramMap.get("hoscode");
        //1 获取医院系统传递过来的签名,签名进行MD5加密
        String hospSign = (String)paramMap.get("sign");

        //2 根据传递过来医院编码，查询数据库，查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        //3 把数据库查询签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);

        //4 判断签名是否一致
        if(!hospSign.equals(signKeyMd5)) {
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(paramMap);
        return Result.ok();
    }

    //查询排班接口
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        //获取到传递过来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap= HttpRequestHelper.switchMap(requestMap);

        //1、获取医院编号和科室编号
        String hoscode = (String)paramMap.get("hoscode");
        String depcode= (String) paramMap.get("depcode");
        //当前页码和每页的记录数
        int page =StringUtils.isEmpty((String)paramMap.get("page"))?1:
                Integer.parseInt((String) paramMap.get("page"));
        int limit =StringUtils.isEmpty((String)paramMap.get("limit"))?1:
                Integer.parseInt((String) paramMap.get("limit"));
        //TODO签名校验
        //1 获取医院系统传递过来的签名,签名进行MD5加密
        String hospSign = (String)paramMap.get("sign");

        //2 根据传递过来医院编码，查询数据库，查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        //3 把数据库查询签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);

        //4 判断签名是否一致
        if(!hospSign.equals(signKeyMd5)) {
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }
        ScheduleQueryVo scheduleQueryVo=new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        //调用service通过page-1,limit,departmentQueryVo
        Page<Schedule> pageModel=scheduleService.findPageDepartment(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }

    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        //获取到传递过来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap= HttpRequestHelper.switchMap(requestMap);

        //医院编号和科室编号
        String hoscode= (String) paramMap.get("hoscode");
        String hosScheduleId=(String) paramMap.get("hosScheduleId");

        //TODO校验签名
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }
}
