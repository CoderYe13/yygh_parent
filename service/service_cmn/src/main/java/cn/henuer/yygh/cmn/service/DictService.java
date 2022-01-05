package cn.henuer.yygh.cmn.service;

import cn.henuer.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);
}
