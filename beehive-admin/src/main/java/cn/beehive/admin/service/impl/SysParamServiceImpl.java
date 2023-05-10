package cn.beehive.admin.service.impl;

import cn.beehive.admin.domain.query.SysParamPageQuery;
import cn.beehive.admin.domain.request.SysParamRequest;
import cn.beehive.admin.domain.vo.SysParamVO;
import cn.beehive.admin.handler.converter.SysParamConverter;
import cn.beehive.admin.service.SysParamService;
import cn.beehive.base.domain.entity.SysParamDO;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.mapper.SysParamMapper;
import cn.beehive.base.util.PageUtil;
import cn.beehive.base.util.ThrowExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/10
 * 系统参数业务实现类
 */
@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParamDO> implements SysParamService {

    @Override
    public IPage<SysParamVO> page(SysParamPageQuery query) {
        Page<SysParamDO> page = page(new Page<>(query.getPageNum(), query.getPageSize()), new LambdaQueryWrapper<SysParamDO>()
                .like(StrUtil.isNotBlank(query.getName()), SysParamDO::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getParamKey()), SysParamDO::getParamKey, query.getParamKey())
                .like(StrUtil.isNotBlank(query.getParamValue()), SysParamDO::getParamValue, query.getParamValue())
                .orderByDesc(SysParamDO::getUpdateTime));
        return PageUtil.toPage(page, SysParamConverter.INSTANCE.entityToVO(page.getRecords()));
    }

    @Override
    public Integer save(SysParamRequest request) {
        // 校验 key 是否唯一
        checkKeyUnique(request.getKey());

        // 保存系统参数
        SysParamDO sysParamDO = new SysParamDO();
        sysParamDO.setName(request.getName());
        sysParamDO.setParamKey(request.getKey());
        sysParamDO.setParamValue(request.getValue());
        sysParamDO.setIsDeleted(0);
        save(sysParamDO);
        return sysParamDO.getId();
    }

    @Override
    public void update(SysParamRequest request) {
        SysParamDO sysParamDO = checkExist(request.getId());
        // key 改动的话需要校验是否重复
        if (ObjectUtil.notEqual(sysParamDO.getParamKey(), request.getKey())) {
            checkKeyUnique(request.getKey());
        }

        sysParamDO.setName(request.getName());
        sysParamDO.setParamKey(request.getKey());
        sysParamDO.setParamValue(request.getValue());
        updateById(sysParamDO);
    }

    @Override
    public void remove(Integer id) {
        checkExist(id);
        removeById(id);
        // TODO 删除缓存
    }

    /**
     * 校验 key 是否唯一
     *
     * @param key key
     */
    private void checkKeyUnique(String key) {
        long count = count(new LambdaQueryWrapper<SysParamDO>().eq(SysParamDO::getParamKey, key));
        ThrowExceptionUtil.isTrue(count > 0)
                .throwMessage(StrUtil.format("key [{}] 已存在", key));
    }

    /**
     * 校验参数是否存在
     *
     * @param id 主键
     */
    private SysParamDO checkExist(Integer id) {
        SysParamDO sysParamDO = getById(id);
        if (Objects.isNull(sysParamDO)) {
            throw new ServiceException(StrUtil.format("id [{}] 不存在", id));
        }
        return sysParamDO;
    }
}