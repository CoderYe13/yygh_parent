package cn.henuer.yygh.cmn.service.impl;

import cn.henuer.model.cmn.Dict;
import cn.henuer.vo.cmn.DictEeVo;
import cn.henuer.yygh.cmn.listener.DictListener;
import cn.henuer.yygh.cmn.mapper.DictMapper;
import cn.henuer.yygh.cmn.service.DictService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    //根据数据id查询子查询数据列表
    @Override
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);

        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向dictList集合中每个dict对象中设置hasChild属性
        dictList = dictList.stream().map((dict) -> {
            boolean hasChildren = isChildren(dict.getId());
            if (hasChildren) {
                dict.setHasChildren(true);
            }
            return dict;
        }).collect(Collectors.toList());

        return dictList;
    }
    //判断id下面时候有子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void exportDictData(HttpServletResponse response) {

    }

    @Override
    public void importDictData(MultipartFile file) {

    }

    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictcode为null，那么直接通过value查询
        if (StringUtils.isEmpty(dictCode)){
            QueryWrapper wrapper=new QueryWrapper();
            wrapper.eq("value",value);
            Dict dict=baseMapper.selectOne(wrapper);
            return dict.getName();
        }else{
            //如果dictCode不为null，根据dictCode和value查询
            Dict codeDict=this.getDictByDictCode(dictCode);
            Long parent_id=codeDict.getId();
            //根据parent_id和value查询唯一的dict
           Dict finalDict= baseMapper.selectOne(new QueryWrapper<Dict>()
            .eq("dict_code",dictCode).eq("value",value));
           return finalDict.getName();
        }

    }
    private Dict getDictByDictCode(String dictCode){
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("dict_code",dictCode);
        Dict dict=baseMapper.selectOne(wrapper);
        return dict;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        return null;
    }



}
