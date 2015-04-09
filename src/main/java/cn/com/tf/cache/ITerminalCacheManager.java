package cn.com.tf.cache;

import cn.com.hypt.db.model.Terminal;

/**
 * 终端缓存管理
 * @author tianfei
 *
 */
public interface ITerminalCacheManager {
	
	public void initCache();
	
	Terminal addOrUpdateTerminal(Terminal t) ;
	
	Terminal getTerminalBySimNo(String simNo);

}
