package com.gitee.myclouds.admin.modules.cache;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.gitee.myclouds.admin.domain.mymodule.MyModuleEntity;
import com.gitee.myclouds.admin.domain.mymodule.MyModuleMapper;
import com.gitee.myclouds.toolbox.util.MyCons;
import com.gitee.myclouds.toolbox.wrap.Dto;
import com.gitee.myclouds.toolbox.wrap.Dtos;
import com.google.common.collect.Maps;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * 通用数据缓存服务
 * 
 * @author xiongchun
 *
 */
@Service
public class CacheMiscService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MyModuleMapper myModuleMapper;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 缓存全量模块菜单表数据<br>
	 * 用途 1：计算面包屑导航路径提示文本；
	 */
	public Dto cacheModules() {
		List<MyModuleEntity> myModuleEntities = myModuleMapper.list(null);
		Map<String, String> cacheMap = Maps.newHashMap();
		for (MyModuleEntity myModuleEntity : myModuleEntities) {
			cacheMap.put(myModuleEntity.getId().toString(), JSON.toJSONString(myModuleEntity));
		}
		stringRedisTemplate.opsForHash().putAll(MyCons.CacheKeyOrPrefix.MyModule.getValue(), cacheMap);
		stringRedisTemplate.opsForHash().put(MyCons.CacheKeyOrPrefix.LastCacheTime.getValue(),
				MyCons.CacheKeyOrPrefix.MyModule.getValue(), DateUtil.now());
		String msgString = "完成模块菜单数据的全量缓存或刷新";
		log.info(msgString);
		return Dtos.newDto().put2("code", "1").put2("msg", msgString);
	}

}
