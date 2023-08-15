/*
 * @Description: 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 18:16:23
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 10:40:00
 */
package com.example.nfplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nfplus.entity.Collection;
import org.springframework.stereotype.Service;

@Service
public interface CollectionService extends IService<Collection> {
    /**
     * @description: 验证收藏对象中的信息是否正确
     * @param {Collection} collection 待验证收藏对象
     * @return {Boolean} true:正确 false:错误
     * @author: wch
     */    
    public Boolean verifyCollection(Collection collection);

    /**
     * @description: 查找收藏对象是否存在
     * @param {Collection} collection 收藏对象
     * @return {Boolean} true:存在 false:不存在
     * @author: wch
     */    
    public Boolean isCollect(Collection collection);

    /**
     * @description:  删除收藏对象
     * @param {Collection} collection 待删除收藏对象
     * @return {Boolean} true:删除成功 false:删除失败(对象不存在或者其他原因导致的删除失败)
     * @author: wch
     */
    public Boolean removeCollection(Collection collection);
}
