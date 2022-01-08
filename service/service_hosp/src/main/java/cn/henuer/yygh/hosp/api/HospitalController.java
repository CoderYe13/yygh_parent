package cn.henuer.yygh.hosp.api;

import cn.henuer.model.hosp.Hospital;
import cn.henuer.yygh.common.exception.HospitalException;
import cn.henuer.yygh.common.result.Result;
import cn.henuer.yygh.common.result.ResultCodeEnum;
import cn.henuer.yygh.hosp.commom.helper.HttpRequestHelper;
import cn.henuer.yygh.hosp.commom.utils.MD5;
import cn.henuer.yygh.hosp.service.HospitalService;
import cn.henuer.yygh.hosp.service.HospitalSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

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
}
