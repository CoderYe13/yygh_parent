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

import java.util.List;

@Api("数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    //根据数据id查询子查询数据列表
    @ApiOperation("根据数据id查询子查询列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> childData = dictService.findChildData(id);
        return Result.ok(childData);
    }
}

