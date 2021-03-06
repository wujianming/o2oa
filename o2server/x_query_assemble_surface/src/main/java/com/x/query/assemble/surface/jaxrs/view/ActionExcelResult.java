package com.x.query.assemble.surface.jaxrs.view;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import java.util.Optional;

class ActionExcelResult extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExcelResult.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.info("{}", flag);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(flag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				ExcelResultObject obj = (ExcelResultObject) optional.get();
				if (!StringUtils.equals(effectivePerson.getDistinguishedName(), obj.getPerson())) {
					throw new ExceptionAccessDenied(effectivePerson);
				}
				Wo wo = new Wo(obj.getBytes(), this.contentType(true, obj.getName()),
						this.contentDisposition(true, obj.getName()));
				result.setData(wo);
			} else {
				throw new ExceptionExcelResultObject(flag);
			}
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}