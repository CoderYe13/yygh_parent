package cn.henuer.yygh.cmn.controller;

import cn.henuer.model.cmn.Dict;
import cn.henuer.yygh.cmn.service.DictService;
import cn.henuer.yygh.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api("数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    //导出数据字典接口，使用response输出
    @GetMapping("exportData")
    public Result exportDict(HttpServletResponse response){
        dictService.exportDictData(response);
        return Result.ok();
    }

    //根据数据id查询子查询数据列表
    @ApiOperation("根据数据id查询子查询列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> childData = dictService.findChildData(id);
        return Result.ok(childData);
    }

    //根据dictcode和value查询 dictName
    @GetMapping("getName/{dictCode}/{value}")
    public String getDictName(@PathVariable("dictCode") String dictCode,
                              @PathVariable("value") String value){
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }
    //根据value查询
    @GetMapping("getName/{value}")
    public String getDictName(@PathVariable("value") String value){
        String dictName = dictService.getDictName("", value);
        return dictName;
    }

    @ApiOperation("根据数据dict_code查询下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }
}

